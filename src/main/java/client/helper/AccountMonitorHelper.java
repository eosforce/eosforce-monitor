package client.helper;

import client.CallConfig;
import client.EosApiClientFactory;
import client.EosApiRestClient;
import client.domain.BpProducer;
import client.domain.GenesisAccount;
import client.domain.NewAccount;
import client.domain.common.transaction.VoiceNotifyReq;
import client.domain.response.chain.ChainInfo;
import client.domain.response.chain.TableRow;
import client.exception.EosApiException;
import client.job.BlockAccount;
import client.util.*;
import client.util.caller.HttpRequestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lxg
 * @create 2018-06-22 16:43
 * @desc
 */
public class AccountMonitorHelper {
    private static final LLogger lLogger = LLoggerFactory.getLogger(AccountMonitorHelper.class);
    private static final LLogger activeLogger = LLoggerFactory.getLogger("activeLogger");
    private static AtomicInteger counter = new AtomicInteger(0);
    private static long basicValue = 1000L;

    public static void main(String[] args) throws Exception {
        //statisticAllBalance(getEosApiRestClientList("http://47.104.74.61:8888"));
        ChainInfo chainInfo = getEosApi(getEosApiRestClientList("http://47.104.74.61:8888")).getChainInfo();
//
//        lLogger.info("result: {}", Utils.getBJDate(chainInfo.getHeadBlockTime()));
        String s = "1";
        JsonNode jsonNode = new BigIntegerNode(new BigInteger(s));
        int j = jsonNode.asInt();
        System.out.println(j);
    }

    public static List<EosApiRestClient> getEosApiRestClientList(String accountMonitorUrl) {
        List<EosApiRestClient> eosApiRestClientList = new ArrayList<>();
        if (accountMonitorUrl == null) {
            lLogger.error("accountMonitorUrl is null");
            return eosApiRestClientList;
        }
        String[] accountMonitorUrls = accountMonitorUrl.split(",");
        EosApiRestClient eosApiRestClient = null;
        for (String url : accountMonitorUrls) {
            eosApiRestClient = EosApiClientFactory.newInstance(url).newRestClient();
            eosApiRestClientList.add(eosApiRestClient);
        }
        return eosApiRestClientList;
    }

    public static BlockAccount statisticAllBalance(List<EosApiRestClient> eosApiRestClientList) {
        lLogger.info("statistic is start");
        BlockAccount blockAccount = new BlockAccount();
        BigDecimal allBalance = new BigDecimal("0");
        TableRow accountList = getAllAccountAvaliabeBalance(eosApiRestClientList);
        BigDecimal bpBalance = statisticBpBalance(eosApiRestClientList, blockAccount);
        BigDecimal voterBalance = statisticVoterBalance(eosApiRestClientList, accountList.getRows(), blockAccount);
        BigDecimal avaliableBalance = getAllBalance(accountList.getRows(), blockAccount);
        allBalance = allBalance.add(bpBalance);
        allBalance = allBalance.add(avaliableBalance);
        allBalance = allBalance.add(voterBalance);

        blockAccount.setTotalAccount(accountList.getRows().size());
        blockAccount.setTotalSupply(allBalance.longValue());
        blockAccount.setTotalAccountBalance(new BigDecimal(Utils.formatBigDecimal(avaliableBalance)));
        blockAccount.setTotalVoterBalance(new BigDecimal(Utils.formatBigDecimal(voterBalance)));
        blockAccount.setTotalBpBalance(new BigDecimal(Utils.formatBigDecimal(bpBalance)));
        lLogger.info("statistics account: {}", Utils.toJson(blockAccount));
        return blockAccount;
    }

    public static BlockAccount statisticAllBalance(List<EosApiRestClient> eosApiRestClientList, Map<String, GenesisAccount> veteranAccountMap) {
        lLogger.info("statistic is start");
        BlockAccount blockAccount = new BlockAccount();
        BigDecimal allBalance = new BigDecimal("0");
        TableRow accountList = getAllAccountAvaliabeBalance(eosApiRestClientList);
        NewAccount newAccount = statisticNewAccount(accountList.getRows(), veteranAccountMap);
        BigDecimal avaliableBalance = getAllBalance(accountList.getRows(), blockAccount);
        BigDecimal bpBalance = statisticBpBalance(eosApiRestClientList, blockAccount);
        BigDecimal voterBalance = statisticVoterBalance(eosApiRestClientList, accountList.getRows(), blockAccount);
        allBalance = allBalance.add(bpBalance);
        allBalance = allBalance.add(avaliableBalance);
        allBalance = allBalance.add(voterBalance);

        blockAccount.setActiveAccount(newAccount.getNum());
        blockAccount.setActiveAccountBalance(newAccount.getTotalBalance());
        blockAccount.setTotalAccount(accountList.getRows().size());
        blockAccount.setTotalSupply(allBalance.longValue());
        blockAccount.setTotalAccountBalance(new BigDecimal(Utils.formatBigDecimal(avaliableBalance)));
        blockAccount.setTotalVoterBalance(new BigDecimal(Utils.formatBigDecimal(voterBalance)));
        blockAccount.setTotalBpBalance(new BigDecimal(Utils.formatBigDecimal(bpBalance)));
        lLogger.info("statistics account: {}", Utils.toJson(blockAccount));
        return blockAccount;
    }

    public static NewAccount statisticNewAccount(List<Map<String, String>> accountList, Map<String, GenesisAccount> veteranAccountMap) {
        if (veteranAccountMap == null || veteranAccountMap.size() == 0) return null;
        NewAccount newAccount = new NewAccount();
        long counter = 0;
        List<String> genesisNameList = new ArrayList<>();
        GenesisAccount genesisAccount = null;
        for (Map<String, String> entry : accountList) {
            if (veteranAccountMap.containsKey(entry.get("name"))) {
                genesisAccount = veteranAccountMap.get(entry.get("name"));
                BigDecimal avaiable = new BigDecimal(String.valueOf(Utils.matchDouble(entry.get("available"))));
                BigDecimal asset = new BigDecimal(String.valueOf(Utils.matchDouble(genesisAccount.getAsset())));
                if (avaiable.compareTo(asset) != 0) {
                    counter++;
                    genesisNameList.add(genesisAccount.getName());
                    newAccount.setTotalBalance(newAccount.getTotalBalance().add(asset));
                }
            }
        }
        newAccount.setNum(counter);
        activeLogger.info("activeName: {}", Utils.toJson(genesisNameList));
        lLogger.info("statisticNewAccount activeState: {}", Utils.toJson(newAccount));
        return newAccount;
    }

    public static TableRow getAllAccountAvaliabeBalance(List<EosApiRestClient> eosApiRestClients) {
        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
        requestParameters.put("scope", "eosio");
        requestParameters.put("code", "eosio");
        requestParameters.put("table", "accounts");
        requestParameters.put("json", true);
        requestParameters.put("limit", 500000000);
        lLogger.info("accountsRpc is start");
        TableRow tableRow = getEosApi(eosApiRestClients).getTableRows(requestParameters);
        lLogger.info("accountsRpc is end, totalAccount: {}", tableRow.getRows().size());
        return tableRow;
    }

    public static BigDecimal statisticBpBalance(List<EosApiRestClient> eosApiRestClients, BlockAccount blockAccount) {
        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
        requestParameters.put("scope", "eosio");
        requestParameters.put("code", "eosio");
        requestParameters.put("table", "bps");
        requestParameters.put("json", true);
        requestParameters.put("limit", 10000);
        lLogger.info("bpsRpc is start");
        TableRow tableRow = getEosApi(eosApiRestClients).getTableRows(requestParameters);
        lLogger.info("bpsRpc is end, bp: {}, bpsSize: {}", Utils.toJson(tableRow), tableRow.getRows().size());
        BigDecimal rewardPool = new BigDecimal("0");
        BigDecimal bpRewardPool = null;
        for (Map<String, String> entry : tableRow.getRows()) {
            bpRewardPool = new BigDecimal(String.valueOf(Utils.matchDouble(entry.get("rewards_pool"))));
            if (bpRewardPool.doubleValue() < 0) {
                blockAccount.getBpWarn().put(entry.get("name"), bpRewardPool);
            }
            rewardPool = rewardPool.add(bpRewardPool);
        }
        lLogger.info("bp rewardPool: {}", rewardPool);
        return rewardPool;
    }

    public static JsonNode getSchedules(List<EosApiRestClient> eosApiRestClients) {
        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
        requestParameters.put("scope", "eosio");
        requestParameters.put("code", "eosio");
        requestParameters.put("table", "schedules");
        requestParameters.put("json", true);
        requestParameters.put("limit", 230000000);
        lLogger.info("schedulesRpc is start");
        return getEosApi(eosApiRestClients).getSpecialTableRows(requestParameters);
    }

    public static List<JsonNode> getVersions(JsonNode versions) {
        if (versions != null) {
            return versions.findValues("version");
        }
        return null;
    }

    public static List<JsonNode> getProducersList(JsonNode schedules) {
        if (schedules != null) {
            return schedules.findValues("producers");
        }
        return null;
    }

    public static int getSkipBlockLoop(List<JsonNode> producers) {
        if (producers != null) {
            return producers.size() - 1;
        }
        return 0;
    }

    public static Integer getVersion(List<JsonNode> versionList) {
        if (versionList != null && versionList.size() > 0) {
            JsonNode version = versionList.get(versionList.size() - 1);
            lLogger.info("schedules is end, version: {}", Utils.toJson(version));
            return version.asInt();
        }
        return null;
    }

    public static List<BpProducer> getBpProducerList(List<JsonNode> producerList) {
        if (producerList != null && producerList.size() > 0) {
            JsonNode producer = producerList.get(producerList.size() - 1);
            lLogger.info("schedulesRpc is end, schedules: {}", Utils.toJson(producer));
            List<BpProducer> bpProducerList = Utils.toObject(Utils.toJson(producer), new TypeReference<List<BpProducer>>() {
            });
            return bpProducerList;
        }
        return null;
    }

    public static List<BpProducer> skipBpProducer(List<BpProducer> bpProducerList, int totalNode, double shrink, double skipBlockNumberScale) {
        if (bpProducerList != null && bpProducerList.size() > 0) {
            long total = 0;
            for (BpProducer bpProducer : bpProducerList) {
                total += bpProducer.getAmount();
            }
            long avg = total / totalNode;
            if (avg < shrink) {
                return null;
            }
            List<BpProducer> skipBpProducerList = new ArrayList<>();
            double diff = 0;
            for (BpProducer bpProducer : bpProducerList) {
                diff = avg - bpProducer.getAmount();
                if (diff > 0 && (diff / avg >= skipBlockNumberScale)) {
                    skipBpProducerList.add(bpProducer);
                }
            }
            return skipBpProducerList;
        }
        return null;
    }


    public static long expandThousand(float v) {

        return basicValue;
    }

    public static long shrinkThousand() {
        return basicValue;
    }

    public static BigDecimal getAllBalance(List<Map<String, String>> avaliableList, BlockAccount blockAccount) {
        lLogger.info("totalSize: {}", avaliableList.size());
        BigDecimal all = new BigDecimal("0");
        for (Map<String, String> entry : avaliableList) {
            String name = entry.get("name");
            String available = entry.get("available");
            BigDecimal avaiable = new BigDecimal(String.valueOf(Utils.matchDouble(available)));
            if (avaiable.doubleValue() < 0) {
                blockAccount.getAccountWarn().put(name, avaiable);
            }
            all = all.add(avaiable);
        }
        lLogger.info("totalAccountBalance: {}", all);
        return all;
    }

    public static EosApiRestClient getEosApi(List<EosApiRestClient> eosApiRestClients) {
        int size = eosApiRestClients.size();
        int nodeIndex = Math.abs(counter.getAndIncrement() % size);
        return eosApiRestClients.get(nodeIndex);
    }

    public static BigDecimal statisticVoterBalance(List<EosApiRestClient> eosApiRestClients, List<Map<String, String>> accountList, BlockAccount blockAccount) {
        BigDecimal[] voterBalances = new BigDecimal[1];
        voterBalances[0] = new BigDecimal("0");
        long startTime = System.currentTimeMillis();
        int totalAccountSize = accountList.size();
        AtomicInteger accountCounter = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(totalAccountSize);
        for (Map<String, String> entry : accountList) {
            ExecutorsService.execute(() -> {
                BigDecimal votersBalance = new BigDecimal("0");
                try {
                    votersBalance = getVoterBalance(eosApiRestClients, entry.get("name"), accountCounter, blockAccount);
                } catch (EosApiException eae) {
                    lLogger.error("{}", eae.getMessage());
                } catch (Throwable t) {
                    lLogger.error("{}", t.getMessage());
                }
                synchronized (voterBalances) {
                    voterBalances[0] = voterBalances[0].add(votersBalance);
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            lLogger.error("{}", e.getMessage());
        }
        long diffTime = System.currentTimeMillis() - startTime;
        lLogger.info("totalVoterBalance: {}, costTime: {}s", voterBalances[0], (diffTime / 1000));
        return voterBalances[0];
    }

    public static BigDecimal getVoterBalance(List<EosApiRestClient> eosApiRestClients, String accountName, AtomicInteger accountCounter, BlockAccount blockAccount) {
        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
        requestParameters.put("scope", accountName);
        requestParameters.put("code", "eosio");
        requestParameters.put("table", "votes");
        requestParameters.put("json", true);
        requestParameters.put("limit", 90000);
        TableRow tableRow = getEosApi(eosApiRestClients).getTableRows(requestParameters);
        BigDecimal voterAmount = new BigDecimal("0");
        if (tableRow.getRows().size() > 0) {
            blockAccount.addupVoter();
        }
        for (Map<String, String> entry : tableRow.getRows()) {
            BigDecimal staked = new BigDecimal(String.valueOf(Utils.matchDouble(entry.get("staked"))));
            synchronized (blockAccount.getVoteWarn()) {
                if (staked.doubleValue() < 0) {
                    blockAccount.getVoteWarn().put(accountName, staked);
                }
                blockAccount.setStakedBalance(blockAccount.getStakedBalance().add(staked));
                blockAccount.setUnstakeBalance(blockAccount.getUnstakeBalance().add(new BigDecimal(String.valueOf(Utils.matchDouble(entry.get("unstaking"))))));
            }
            voterAmount = voterAmount.add(staked);
            voterAmount = voterAmount.add(new BigDecimal(String.valueOf(Utils.matchDouble(entry.get("unstaking")))));
        }
        lLogger.debug("index: {}, voter: {}, voterAmount: {}, tableRow: {}", accountCounter.incrementAndGet(), accountName, voterAmount, Utils.toJson(tableRow));
        return voterAmount;
    }

    public static ChainInfo getBlockInfo(List<EosApiRestClient> eosApiRestClients) {
        return getEosApi(eosApiRestClients).getChainInfo();
    }

    public static Map<String, Boolean> call(CallConfig callConfig) throws Exception {
        if (StringUtils.isBlank(callConfig.getCallee())) {
            lLogger.error("phone config is blank!");
            return null;
        }
        String[] phones = callConfig.getCallee().split(",");
        if (phones == null || phones.length == 0) {
            return null;
        }
        Map<String, Boolean> result = new HashMap<>(phones.length);
        for (String p : phones) {
            if (StringUtils.isBlank(p)) {
                continue;
            }
            VoiceNotifyReq voiceNotifyReq = new VoiceNotifyReq();
            BeanUtils.copyProperties(callConfig, voiceNotifyReq);
            voiceNotifyReq.setCallee(p);
            boolean sendVoiceNotifyResult = sendVoiceNotify(voiceNotifyReq, callConfig);
            result.put(p, sendVoiceNotifyResult);
        }
        lLogger.info("call is result: {}", result);
        return result;
    }

    public static boolean sendVoiceNotify(VoiceNotifyReq voiceNotifyReq, CallConfig callConfig) throws Exception {
        String userId = callConfig.getUserId();
        String token = callConfig.getToken();
        String publicKey = callConfig.getPublicKey();
        long timestamp = System.currentTimeMillis();
        String sign = CallUtils.getSignature(userId, token, timestamp, publicKey);
        String url = callConfig.getCallUrl().replace("USERID", userId);
        String result = HttpRequestUtils.doPost(url, token, timestamp, sign, voiceNotifyReq);
        lLogger.info("sendPhone: {}, voiceNotifyRsp: {}", Utils.toJson(result));
        return result.indexOf("100000") >= 0;
    }
//    public static void sendVoiceNotify(VoiceNotifyReq voiceNotifyReq, String caller) throws Exception {
//        voiceNotifyReq.setCaller("95054006");
//        voiceNotifyReq.setCallee("18621368458");
//        voiceNotifyReq.setContent("{\"NUM_8\":1234}");
//        voiceNotifyReq.setPlayTimes("1");
//        voiceNotifyReq.setType("0");
//        voiceNotifyReq.setTemplateId("8c51eff7595f4d6bbb422e2f4a0539e0");
//        voiceNotifyReq.setUserData("");
//        voiceNotifyReq.setHangupUrl("");
//
//        String url = "http://api.ucpalm.cn/rest/v3/voice/notify/USERID";
//        long timestamp = System.currentTimeMillis();
//        String userId = "277e939ca1674951b55320a928db474a";
//        String token = "f2e4ea6b97034aff82151bd7218344d7";
//        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCkuFHn+RM/7QegKlirRSiR06Maq1HO6TqKnsQo\n" +
//                "+ieG56qmukE31oFhXM2SeznxbkpJzZS+tshLXhl1OJZCdRrrSr2igTU4v/8MSRV2HamYv4u0IFDK\n" +
//                "W5WIYib0h5df/0xo63xySOC+z5p3hlDphBWvpI0GXtK0GDRsJMEiLrghZQIDAQAB";
//        String sign = CallUtils.getSignature(userId, token, timestamp, publicKey);
//        url = url.replace("USERID", userId);
//        String voiceNotifyRsp = HttpRequestUtils.doPost(url, token, timestamp, sign, voiceNotifyReq);
//        lLogger.info("voiceNotifyRsp: {}", Utils.toJson(voiceNotifyRsp));
//        lLogger.info("voiceNotifyRsp: {}", Utils.toJson(voiceNotifyRsp));
//    }

}

