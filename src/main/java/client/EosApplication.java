package client;

import client.crypto.digest.Sha256;
import client.crypto.ec.EcDsa;
import client.crypto.ec.EcSignature;
import client.crypto.ec.EosPrivateKey;
import client.crypto.ec.EosPublicKey;
import client.crypto.model.api.EosChainInfo;
import client.crypto.model.api.GetRequiredKeys;
import client.crypto.model.chain.Action;
import client.crypto.model.chain.SignedTransaction;
import client.crypto.model.types.TypeChainId;
import client.crypto.util.HexUtils;
import client.domain.common.transaction.*;
import client.domain.request.chain.transaction.PushBlockRequest;
import client.domain.response.chain.AbiJsonToBin;
import client.domain.response.chain.Block;
import client.domain.response.chain.ChainInfo;
import client.domain.response.chain.TableRow;
import client.domain.response.chain.account.Account;
import client.domain.response.chain.transaction.PushedTransaction;
import client.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lxg
 * @create 2018-06-06 14:55
 * @desc
 */
@SpringBootApplication
@EnableScheduling
public class EosApplication {
    private static LLogger lLogger = LLoggerFactory.getLogger(EosApplication.class);
    private static LLogger errorLogger = LLoggerFactory.getLogger("errorAppender");
    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] argss) throws UnsupportedEncodingException {
        SpringApplication.run(EosApplication.class, argss);
        //buildPushBlockRequest();
        //execTrx();

        //comprareNonTxt();
        //estAccount();

//        List<EosApiRestClient> eosApiRestClientList = new ArrayList<>();
//        EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance("http://47.254.71.89:8888").newRestClient();
//        eosApiRestClientList.add(eosApiRestClient);
////        EosApiRestClient eosApiRestClient1 = EosApiClientFactory.newInstance("http://47.97.122.109:8888").newRestClient();
////        eosApiRestClientList.add(eosApiRestClient1);
//        EosApiRestClient eosApiRestClient2 = EosApiClientFactory.newInstance("http://47.104.255.49:8888").newRestClient();
//        eosApiRestClientList.add(eosApiRestClient2);
//        EosApiRestClient eosApiRestClient3 = EosApiClientFactory.newInstance("http://47.75.126.7:8888").newRestClient();
//        eosApiRestClientList.add(eosApiRestClient3);
////        EosApiRestClient eosApiRestClient4 = EosApiClientFactory.newInstance("http://101.132.77.22:8888").newRestClient();
////        eosApiRestClientList.add(eosApiRestClient4);
////        EosApiRestClient eosApiRestClient5 = EosApiClientFactory.newInstance("http://47.96.232.211:8888").newRestClient();
////        eosApiRestClientList.add(eosApiRestClient5);
//
//        for (int i = 0; i < eosApiRestClientList.size(); i++) {
//            ChainInfo chainInfo = eosApiRestClientList.get(i).getChainInfo();
//            lLogger.info("index: {}, blockNumber: {}, chainInfo: {}", (i+1), chainInfo.getHeadBlockNum(), Utils.toJson(chainInfo));
//        }
//        execTrx(eosApiRestClientList);

        //statisticVoterBalance(eosApiRestClientList);
        //statisticAllAccountAvaliabeBalance(eosApiRestClientList);
        //statisticAllBalance(eosApiRestClientList);
//        for (EosApiRestClient eo : eosApiRestClientList) {
//            ChainInfo chainInfo = eo.getChainInfo();
//            lLogger.info("blockNumber: {}, chainInfo: {}", chainInfo.getHeadBlockNum(), Utils.toJson(chainInfo));
//        }
    }

    private static void statisticAllBalance(List<EosApiRestClient> eosApiRestClientList) {
        BigDecimal allBalance = new BigDecimal("0");
        TableRow accountList = getAllAccountAvaliabeBalance(eosApiRestClientList);
        BigDecimal bpBalance = statisticBpBalance(eosApiRestClientList);
        //BigDecimal voterBalance = statisticVoterBalance(eosApiRestClientList, accountList.getRows());
        BigDecimal avaliableBalance = getAllBalance(accountList.getRows());
        allBalance = allBalance.add(bpBalance);
        allBalance = allBalance.add(avaliableBalance);
        //allBalance = allBalance.add(voterBalance);
        //lLogger.info("bpBalance: {}, voterBalance: {}, avaliableBalance: {}, allBalance: {}", bpBalance, voterBalance, avaliableBalance, allBalance);
        lLogger.info("bpBalance: {}, avaliableBalance: {}, allBalance: {}", bpBalance, avaliableBalance, allBalance);
    }

    public static void comprareNonTxt() {
        LinkedBlockingQueue<String> records = null;
        try {
            records = FileUtils.readFile("E:\\res\\snapshot.csv");
        } catch (InterruptedException e) {
            lLogger.error("{}", e);
        }

        LinkedBlockingQueue<String> records4 = null;
        try {
            records4 = FileUtils.readFile("E:\\res\\snapshot4.csv");
        } catch (InterruptedException e) {
            lLogger.error("{}", e);
        }

        lLogger.info("records1 length: {}", records4.size());
        lLogger.info("records length: {}", records.size());
        for (String s : records) {
            records4.remove(s);
        }
        lLogger.info("records: {}", records4);
    }

    public static void comprareTxt() {
        LinkedBlockingQueue<String> records = null;
        try {
            records = FileUtils.readFile("E:\\res\\snapshot1.csv");
        } catch (InterruptedException e) {
            lLogger.error("{}", e);
        }

        LinkedBlockingQueue<String> records1 = null;
        try {
            records1 = FileUtils.readFile("E:\\res\\snapshot2.csv");
        } catch (InterruptedException e) {
            lLogger.error("{}", e);
        }

        LinkedBlockingQueue<String> records2 = null;
        try {
            records2 = FileUtils.readFile("E:\\res\\snapshot3.csv");
        } catch (InterruptedException e) {
            lLogger.error("{}", e);
        }

        lLogger.info("records1 length: {}", records1.size());
        lLogger.info("records2 length: {}", records2.size());
        lLogger.info("records length: {}", records.size());
        for (String s : records) {
            records1.remove(s);
            records2.remove(s);
        }
        lLogger.info("records1: {}", records1);
        lLogger.info("records2: {}", records2);
    }

    public static void execTrx(List<EosApiRestClient> eosApiRestClientList) {
//        List<EosApiRestClient> eosApiRestClientList = new ArrayList<>();
//        EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance("http://47.97.122.109:8888").newRestClient();
//        eosApiRestClientList.add(eosApiRestClient);
//        EosApiRestClient eosApiRestClient1 = EosApiClientFactory.newInstance("http://47.254.71.89:8888").newRestClient();
//        eosApiRestClientList.add(eosApiRestClient1);
//        EosApiRestClient eosApiRestClient2 = EosApiClientFactory.newInstance("http://47.254.71.89:8888").newRestClient();
//        eosApiRestClientList.add(eosApiRestClient2);
        LinkedBlockingQueue<String> records = null;
        try {
            records = FileUtils.readFile("E:\\res\\accounts_eosforce.csv");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final BigDecimal[] totalBalance = new BigDecimal[1];
        totalBalance[0] = new BigDecimal("0");
        CountDownLatch countDownLatch = new CountDownLatch(records.size());
        statistic(records, countDownLatch, eosApiRestClientList, totalBalance);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lLogger.info("totalBalance: {}", totalBalance[0]);
    }

    public static void testAccount() {
        EosApiRestClient eosApiRestClient1 = EosApiClientFactory.newInstance("http://47.254.71.89:8888").newRestClient();

        lLogger.info("account: {}", Utils.toJson(eosApiRestClient1.getAccountByName("gq2tsgenesis")));
    }

    public static void statistic(LinkedBlockingQueue<String> records, CountDownLatch countDownLatch, List<EosApiRestClient> eosApiRestClients, BigDecimal[] totalBalance) {
        final AtomicInteger counter_start = new AtomicInteger(0);
        lLogger.info("totalRecords: {}", records.size());
        for (String record : records) {
            ExecutorsService.execute(() -> {
                countDownLatch.countDown();
                double balance = 0;
                try {
                    String[] r = record.split(",");
//                    String o_name = r[1];
//                    double o_balance = Utils.matchDouble(r[3]);
//                    String o_pk = r[2];
                    String o_name = r[0];
                    double o_balance = Utils.matchDouble(r[2]);
                    String o_pk = r[1];
                    JsonNode account = getEosApi(eosApiRestClients).getAccountByName(o_name);
                    String pk = getPk(Utils.toJson(account));
                    TableRow tableRow = getEosApi(eosApiRestClients).getTableRows("eosio", "eosio", "accounts", o_name);
                    balance = getBalance(tableRow);
                    boolean match_balance = balance == o_balance;
                    boolean match_pk = o_pk.equals(pk);
                    counter_start.incrementAndGet();
                    if (match_balance && match_pk) {
                        lLogger.info("cursor: {}, accountName: {} is OK", counter_start.get(), o_name);
                    } else {
                        lLogger.error("cursor: {}, accountName: {}, o_pk: {}, o_balance: {}", counter_start.get(), o_name, o_pk, o_balance);
                        lLogger.error("cursor: {}, accountName: {}, pk: {}, balance: {}", counter_start.get(), o_name, pk, balance);
                    }
                } catch (Exception e) {
                }
                synchronized (totalBalance) {
                    totalBalance[0] = totalBalance[0].add(new BigDecimal(String.valueOf(balance)));
                }
            });
        }
    }

    public static BigDecimal statisticAllAccountAvaliabeBalance(List<EosApiRestClient> eosApiRestClients) {
        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
        requestParameters.put("scope", "eosio");
        requestParameters.put("code", "eosio");
        requestParameters.put("table", "accounts");
        requestParameters.put("json", true);
        requestParameters.put("limit", 500000);
        TableRow tableRow = getEosApi(eosApiRestClients).getTableRows(requestParameters);
        return getAllBalance(tableRow.getRows());
    }

    public static TableRow getAllAccountAvaliabeBalance(List<EosApiRestClient> eosApiRestClients) {
        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
        requestParameters.put("scope", "eosio");
        requestParameters.put("code", "eosio");
        requestParameters.put("table", "accounts");
        requestParameters.put("json", true);
        requestParameters.put("limit", 500000);
        TableRow tableRow = getEosApi(eosApiRestClients).getTableRows(requestParameters);
        return tableRow;
    }

    public static BigDecimal statisticVoterBalance(List<EosApiRestClient> eosApiRestClients, List<Map<String, String>> accountList) {
        BigDecimal votersBalance = new BigDecimal("0");
        for (Map<String, String> entry : accountList) {
            votersBalance = votersBalance.add(getVoterBalance(eosApiRestClients, entry.get("name")));
        }
        lLogger.info("voterBalance: {}", votersBalance);
        return votersBalance;
    }

    public static BigDecimal getVoterBalance(List<EosApiRestClient> eosApiRestClients, String accountName) {
        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
        requestParameters.put("scope", accountName);
        requestParameters.put("code", "eosio");
        requestParameters.put("table", "votes");
        requestParameters.put("json", true);
        requestParameters.put("limit", 1000);
        TableRow tableRow = getEosApi(eosApiRestClients).getTableRows(requestParameters);
        lLogger.info("talbeRow: {}", Utils.toJson(tableRow));
        BigDecimal voterAmount = new BigDecimal("0");
        for (Map<String, String> entry : tableRow.getRows()) {
            voterAmount = voterAmount.add(new BigDecimal(Utils.matchDouble(entry.get("rewards_pool"))));
        }
        lLogger.info("voter: {}, voterAmount: {}", accountName, voterAmount);
        return voterAmount;
    }

    public static BigDecimal statisticBpBalance(List<EosApiRestClient> eosApiRestClients) {
        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
        requestParameters.put("scope", "eosio");
        requestParameters.put("code", "eosio");
        requestParameters.put("table", "bps");
        requestParameters.put("json", true);
        requestParameters.put("limit", 100000);
        TableRow tableRow = getEosApi(eosApiRestClients).getTableRows(requestParameters);
        lLogger.info("bp: {}, bpsSize: {}", Utils.toJson(tableRow), tableRow.getRows().size());
        BigDecimal rewardPool = new BigDecimal("0");
        for (Map<String, String> entry : tableRow.getRows()) {
            rewardPool = rewardPool.add(new BigDecimal(Utils.matchDouble(entry.get("rewards_pool"))));
        }
        lLogger.info("bp rewardPool: {}", rewardPool);
        return rewardPool;
    }

//    public static BigDecimal statisticBpBalance(List<EosApiRestClient> eosApiRestClients) {
//        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
//        requestParameters.put("scope", "eosio");
//        requestParameters.put("code", "eosio");
//        requestParameters.put("table", "bps");
//        requestParameters.put("json", true);
//        requestParameters.put("limit", 100000);
//        TableRow tableRow = getEosApi(eosApiRestClients).getTableRows(requestParameters);
//        lLogger.info("bp: {}, bpsSize: {}", Utils.toJson(tableRow), tableRow.getRows().size());
//        BigDecimal rewardPool = new BigDecimal("0");
//        for (Map<String, String> entry : tableRow.getRows()) {
//            rewardPool = rewardPool.add(new BigDecimal(Utils.matchDouble(entry.get("rewards_pool"))));
//        }
//        lLogger.info("bp rewardPool: {}", rewardPool);
//        return rewardPool;
//    }

    public static BigDecimal statisticAccountBalance(List<EosApiRestClient> eosApiRestClients) {
        LinkedHashMap<String, Object> requestParameters = new LinkedHashMap<>();
        requestParameters.put("scope", "eosio");
        requestParameters.put("code", "eosio");
        requestParameters.put("table", "bps");
        requestParameters.put("json", true);
        requestParameters.put("limit", 10000);
        TableRow tableRow = getEosApi(eosApiRestClients).getTableRows(requestParameters);
        BigDecimal rewardPool = new BigDecimal("0");
        for (Map<String, String> entry : tableRow.getRows()) {
            rewardPool = rewardPool.add(new BigDecimal(Utils.matchDouble(entry.get("rewards_pool"))));
        }
        return rewardPool;
    }

    public static EosApiRestClient getEosApi(List<EosApiRestClient> eosApiRestClients) {
        int size = eosApiRestClients.size();
        int nodeIndex = Math.abs(counter.getAndIncrement() % size);
        return eosApiRestClients.get(nodeIndex);
    }

    public static String getPk(String data) {
        Map<String, Object> dataObj = Utils.toObject(data, Map.class);
        if (dataObj != null && dataObj.containsKey("permissions")) {
            List<Map<String, Object>> permissions = (List<Map<String, Object>>) dataObj.get("permissions");
            if (permissions != null && permissions.size() > 0) {
                Map<String, Object> permission = permissions.get(0);
                if (permission != null && permission.containsKey("required_auth")) {
                    Map<String, Object> required_auth = (Map<String, Object>) permission.get("required_auth");
                    if (required_auth != null && required_auth.containsKey("keys")) {
                        List<Map<String, Object>> keys = (List<Map<String, Object>>) required_auth.get("keys");
                        if (keys != null && keys.size() > 0) {
                            return ((String) keys.get(0).get("key")).trim();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static double getBalance(TableRow tableRow) {
        for (Map<String, String> entry : tableRow.getRows()) {
            String available = entry.get("available");
            return Utils.matchDouble(available);
        }
        return 0;
    }

    public static BigDecimal getAllBalance(List<Map<String, String>> avaliableList) {
        lLogger.info("totalSize: {}", avaliableList.size());
        BigDecimal all = new BigDecimal("0");
        for (Map<String, String> entry : avaliableList) {
            lLogger.info("entry: {}", Utils.toJson(entry));
            String available = entry.get("available");
            all = all.add(new BigDecimal(Utils.matchDouble(available)));
        }
        lLogger.info("totalAccountBalance: {}", all);
        return all;
    }

}
