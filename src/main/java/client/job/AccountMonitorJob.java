package client.job;

import client.CallConfig;
import client.EosApiRestClient;
import client.EosConfig;
import client.MonitorApi;
import client.cache.CacheUtils;
import client.domain.BpProducer;
import client.domain.GenesisAccount;
import client.domain.response.chain.Block;
import client.domain.response.chain.ChainInfo;
import client.exception.EosApiException;
import client.helper.AccountMonitorHelper;
import client.util.ExecutorsService;
import client.util.Utils;
import client.util.sms.NotifyEnum;
import client.util.sms.SmsSender;
import client.weixin.send.AccessToken;
import client.weixin.send.SendMessage;
import client.weixin.send.WeixinUtil;
import com.aliyuncs.exceptions.ClientException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * @author lxg
 * @create 2018-06-22 16:37
 * @desc
 */
@Component
@Configuration
public class AccountMonitorJob {

    private Logger logger = LoggerFactory.getLogger(AccountMonitorJob.class);
    private Logger notifyLogger = LoggerFactory.getLogger("notifyLogger");
    @Autowired
    private MonitorApi monitorApi;
    @Autowired
    private EosConfig eosConfig;
    @Autowired
    private CallConfig callConfig;

    private Map<String, Object> cache = new HashMap<>();
    private Map<NotifyEnum, String> illegal = new HashMap<>();
    private List<EosApiRestClient> eosApiRestClientList = null;
    private Map<String, GenesisAccount> veteranAccountMap = null;


    @Scheduled(cron = "${eos.accountMonitor}")
    public void statisticsAcount() throws Exception {
        init();
        doJob();
    }

    private void init() {
        logger.info("init job!");
        if (eosApiRestClientList == null) {
            logger.info("eosApiRestClientList is null");
            eosApiRestClientList = AccountMonitorHelper.getEosApiRestClientList(eosConfig.getAccountUrl());
        }

        if (veteranAccountMap == null) {
            logger.info("veteranAccountMap is null");
            List<GenesisAccount> veteranAccountList = Utils.toObject(Utils.getResource("account_snapshots.json"), new TypeReference<List<GenesisAccount>>() {
            });
            veteranAccountMap = new HashMap<>();
            for (GenesisAccount genesisAccount : veteranAccountList) {
                veteranAccountMap.put(genesisAccount.getName(), genesisAccount);
            }
        }
        illegal.clear();
        logger.info("init end!");
    }

    private void doJob() {
        logger.info("doJob!");
        String templeate = null;
        monitorApi.closeNet();
        try {
            ChainInfo chainInfo = AccountMonitorHelper.getBlockInfo(eosApiRestClientList);
            BlockAccount blockAccount = monitorApi.findBlockAccount(veteranAccountMap);
            blockAccount.setBlockNum(Long.parseLong(chainInfo.getHeadBlockNum()) - 1);
            blockAccount.setBlockDirSize(Utils.matchDirSize(monitorApi.statBlockDirSize()));
            blockAccount.setStateDirSize(Utils.matchDirSize(monitorApi.statStateDirSize()));
            blockAccount.setIrreversibleBlockNum(Long.parseLong(chainInfo.getLastIrreversibleBlockNum()));
            logger.info("blockAccount: {}", Utils.toJson(blockAccount));
            verifyBalance(blockAccount);
            verifySupply(blockAccount);
            verifyBlockPause(blockAccount);
            warnLargeVote(blockAccount);
            JsonNode producers = AccountMonitorHelper.getSchedules(eosApiRestClientList);
            verifyBlockConfirm(producers, blockAccount);
            verifySkipBlockAndChangeBlockLoop(producers);
            templeate = getTemplate(blockAccount.getTotalAccount(), blockAccount.getTotalAccountBalance(), blockAccount.getTotalVoterBalance(),
                    blockAccount.getTotalBpBalance(), blockAccount.getBlockNum(), blockAccount.getTotalSupply(), Utils.getBJDate(chainInfo.getHeadBlockTime()),
                    blockAccount.getActiveAccount(), blockAccount.getActiveAccountBalance(), blockAccount.getBlockDirSize(),
                    blockAccount.getStateDirSize(), blockAccount.getVoterNum(),
                    blockAccount.getStakedBalance(), blockAccount.getUnstakeBalance(),
                    blockAccount.getIrreversibleBlockNum()
            );
            if (illegal.size() > 0) {
                templeate += "\n错误如下: \n";
            }
            for (Map.Entry<NotifyEnum, String> entry : illegal.entrySet()) {
                templeate += entry.getValue() + ";\n";
            }
            if (illegal.size() > 0) {
                templeate += "监控状态: error";
            } else {
                templeate += "监控状态: ok";
            }
            notifyLogger.info("notifyResult: \n{}", templeate);
        } catch (EosApiException e) {
            if (e.getMessage().indexOf("timed out") >= 0) {
                templeate = "节点连接超时";
                illegal.put(NotifyEnum.node_conn, "节点连接超时");
            } else {
                templeate = "节点进程异常或未知错误";
                illegal.put(NotifyEnum.node_conn, templeate);
            }
            logger.info("error: {}", e.getMessage(), e);
        } catch (Exception e) {
            templeate = "节点进程异常或未知错误";
            illegal.put(NotifyEnum.node_conn, templeate);
            logger.info("exception: {}", e.getMessage(), e);
        } catch (Throwable e) {
            templeate = "节点进程异常或未知错误";
            illegal.put(NotifyEnum.node_conn, templeate);
            logger.info("throwable: {}", e.getMessage(), e);
        }
        logger.info("cache data: {}", Utils.toJson(cache));
        monitorApi.openNet();
        Map<NotifyEnum, String> warnMap = new HashMap<>(illegal);
        sendWarn(templeate, warnMap);
    }

    private void sendWarn(String templeate, Map<NotifyEnum, String> warnMap) {
        logger.info("sendWarn! template: {}, warnMap: {}", templeate, Utils.toJson(warnMap));
        ExecutorsService.getSingleExecutorService().execute(()->{
            sendWeixin(templeate);
        });
        if (warnMap != null && warnMap.size() > 0) {
            ExecutorsService.getSingleExecutorService().execute(() -> {
                for (int i = 0; i < eosConfig.getSendSmsCount(); i++) {
                    for (Map.Entry<NotifyEnum, String> notify : warnMap.entrySet()) {
                        if (notify.getKey().getLayer() > 0) {
                            String warnContent = CacheUtils.get(notify.getValue());
                            if (StringUtils.isNotBlank(warnContent) && i > 0) {
                                continue;
                            }
                        }
                        sendSms(notify.getKey());
                    }
                    Utils.sleep(eosConfig.getSendSmsPeriod());
                }
            });
        }
        for (Map.Entry<NotifyEnum, String> notify : warnMap.entrySet()) {
            CacheUtils.put(notify.getValue(), notify.getValue());
        }
    }

    private void verifyBalance(BlockAccount blockAccount) {
        if (blockAccount.getVoteWarn().size() > 0 || eosConfig.getNotify() == 1) {
            logger.warn("vote_illegal!, voterAccount: {}", blockAccount.getVoteWarn());
            illegal.put(NotifyEnum.vote_illegal, "投票负数异常, 错误账号" + blockAccount.getVoteWarn());
        }

        if (blockAccount.getAccountWarn().size() > 0 || eosConfig.getNotify() == 1) {
            logger.warn("balance_illegal!, accountBalnace: {}", blockAccount.getAccountWarn());
            illegal.put(NotifyEnum.balance_illegal, "余额负数异常, 错误账号" + blockAccount.getAccountWarn());
        }

        if (blockAccount.getBpWarn().size() > 0 || eosConfig.getNotify() == 1) {
            logger.warn("bp_illegal!, bpAccount: {}", blockAccount.getBpWarn());
            illegal.put(NotifyEnum.bp_illegal, "奖金池负数异常, 错误账号" + blockAccount.getBpWarn());
        }
    }

    private void verifyBlockPause(BlockAccount blockAccount) {
        Long blockNumber = (Long) cache.get(NotifyEnum.block_number_pause.name());
        if ((blockNumber != null && (blockAccount.getBlockNum() - blockNumber) < eosConfig.getBlockNumberLayer()) || eosConfig.getNotify() == 1) {
            logger.warn("block_number_pause! blockNumber: {}", blockNumber);
            illegal.put(NotifyEnum.block_number_pause, "同步服务漏块，当前块号" + blockNumber);
        }
        cache.put(NotifyEnum.block_number_pause.name(), blockAccount.getBlockNum());
    }

    private void verifySupply(BlockAccount blockAccount) {
        long inflation = blockAccount.getTotalSupply() - eosConfig.getInitialSupply();
        long inflationOfStatistical = blockAccount.getBlockNum() * eosConfig.getBlockAward();
        logger.info("job is doing, initialSupply: {}, realSupply: {}, inflation: {}, inflationOfStatistical: {}, blockNum: {}, statisticalResult: ",
                eosConfig.getInitialSupply(), blockAccount.getTotalSupply(), inflation, inflationOfStatistical, blockAccount.getBlockNum(), Utils.toJson(blockAccount));

        if ((inflationOfStatistical != inflation) || eosConfig.getNotify() == 1) {
            logger.warn("balance_not_equals! inflation: {}, inflationOfStatistical: {}", inflation, inflationOfStatistical);
            illegal.put(NotifyEnum.balance_not_equals, "账号余额不匹配: 用户实际拥有" + inflation + ", 统计拥有" + inflationOfStatistical);
        }
    }

    private void warnLargeVote(BlockAccount blockAccount) {
        BigDecimal voteLarge = (BigDecimal) cache.get(NotifyEnum.balance_vote_large.name());
        if (voteLarge != null) {
            if (voteLarge.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal diffV = blockAccount.getTotalVoterBalance().subtract(voteLarge);
                double scaleV = (diffV.doubleValue() / (voteLarge.doubleValue()));
                scaleV = scaleV < 0 ? -scaleV : scaleV;
                if (scaleV >= eosConfig.getVoteScale() || eosConfig.getNotify() == 1) {
                    logger.warn("balance_vote_large! oldValue: {}, newValue: {}, diffValue: {}, scaleValue: {}", voteLarge, blockAccount.getTotalVoterBalance(), diffV, scaleV);
                    illegal.put(NotifyEnum.balance_vote_large, "大额变动: 变化前" + voteLarge + ", 变化后" + blockAccount.getTotalVoterBalance() + ", 变化比例值" + Utils.formatEosBalance(scaleV));
                }
            } else if (voteLarge.compareTo(BigDecimal.ZERO) == 0) {
                if (blockAccount.getTotalVoterBalance().doubleValue() >= eosConfig.getVoteScale() || eosConfig.getNotify() == 1) {
                    logger.warn("balance_vote_large! oldValue: {}, newValue: {}, diffValue: {}, scaleValue: {}", voteLarge, blockAccount.getTotalVoterBalance(), blockAccount.getTotalVoterBalance(), blockAccount.getTotalVoterBalance());
                    illegal.put(NotifyEnum.balance_vote_large, "大额变动: 变化前" + voteLarge + ", 变化后" + blockAccount.getTotalVoterBalance() + ", 变化比例值" + blockAccount.getTotalVoterBalance());
                }
            }
        }
        cache.put(NotifyEnum.balance_vote_large.name(), blockAccount.getTotalVoterBalance());
    }

    private long getBpProducerAvg(JsonNode producers) {
        List<JsonNode> productList = AccountMonitorHelper.getProducersList(producers);
        List<BpProducer> bpProducerList = AccountMonitorHelper.getBpProducerList(productList);
        if (bpProducerList != null && bpProducerList.size() > 0) {
            long total = 0;
            for (BpProducer bpProducer : bpProducerList) {
                total += bpProducer.getAmount();
            }
            return total / eosConfig.getProducerNode();
        }
        return 0;
    }

    private void verifySkipBlockAndChangeBlockLoop(JsonNode producers) {
        List<JsonNode> productList = AccountMonitorHelper.getProducersList(producers);
        List<BpProducer> bpProducerList = AccountMonitorHelper.getBpProducerList(productList);
        Integer cacheVersionLoop = (Integer) cache.get(NotifyEnum.block_version.name());
        List<JsonNode> versionList = AccountMonitorHelper.getVersions(producers);
        int version = AccountMonitorHelper.getVersion(versionList);
        if (cacheVersionLoop != null && version != cacheVersionLoop.intValue()) {
            logger.warn("cacheVersionLoop: {}, version: {}", cacheVersionLoop, version);
            List<BpProducer> preBpProducerList = (List<BpProducer>) cache.get(NotifyEnum.block_loop.name());
            if (preBpProducerList != null) {
                Set<String> a = new HashSet<>();
                for (BpProducer bpProducer : preBpProducerList) {
                    a.add(bpProducer.getBpname());
                }
                Set<String> c = new HashSet<>(a);
                Set<String> b = new HashSet<>();
                for (BpProducer bpProducer : bpProducerList) {
                    b.add(bpProducer.getBpname());
                }
                a.removeAll(b);
                b.removeAll(c);
                logger.info("节点换届前: {}, 换届后: {}", a, b);
                if (a.size() > 0 || b.size() > 0) {
                    illegal.put(NotifyEnum.block_loop, "换届提醒, 被换届节点" + a + ", 换届节点" + b);
                }
            }
        }
        cache.put(NotifyEnum.block_version.name(), version);
        if (bpProducerList != null && bpProducerList.size() > 0) {
            cache.put(NotifyEnum.block_loop.name(), bpProducerList);
        }

        List<BpProducer> skipBlockBpList = AccountMonitorHelper.skipBpProducer(bpProducerList, eosConfig.getProducerNode(), eosConfig.getProducerBlockshrink(), eosConfig.getSkipBlockNumberScale());
        if (skipBlockBpList != null && skipBlockBpList.size() > 0 || eosConfig.getNotify() == 1) {
            long loop = AccountMonitorHelper.getSkipBlockLoop(productList);
            logger.warn("block_skip!, skipBlockBp: {}, loop: {}", Utils.toJson(skipBlockBpList), loop);
            String[] skipBlockArray = new String[skipBlockBpList.size()];
            for (int i = 0; i < skipBlockArray.length; i++) {
                skipBlockArray[i] = skipBlockBpList.get(i).getBpname();
            }
            illegal.put(NotifyEnum.block_skip, "跳块异常, 跳块节点" + StringUtils.join(skipBlockArray, ",") + ", 其所在轮次" + loop);
        }
    }

    private void verifyBlockConfirm(JsonNode producers, BlockAccount blockAccount) {
        long scheduleAvg = getBpProducerAvg(producers);
        if (scheduleAvg > eosConfig.getScheduleLoop()) {
            Block block = AccountMonitorHelper.getEosApi(eosApiRestClientList).getBlock(String.valueOf(blockAccount.getBlockNum()));
            if (block.getConfirmed() <= eosConfig.getConfirmed()) {
                illegal.put(NotifyEnum.block_confirmed, "块号确认异常, 所在块号" + block.getBlockNum() + ", confirmed" + block.getConfirmed() + ", schedulesAvg" + scheduleAvg);
                logger.warn("block_confirmed!, scheduleAvg: {}, blockNumber: {}, confirmed: {}", scheduleAvg, blockAccount.getBlockNum(), block.getConfirmed());
            }
        }
    }

    private void sendSms(NotifyEnum notifyEnum) {
        if (notifyEnum == null) return;
        String[] phones = getAllPhone();
        if (phones != null) {
            for (String p : phones) {
                try {
                    SmsSender.sendWarnSms(p, notifyEnum);
                } catch (ClientException e) {
                    logger.error("send sms is error", e);
                } catch (Exception e) {
                    logger.error("send sms is error", e);
                } catch (Throwable e) {
                    logger.error("send sms is error", e);
                }
            }
        }
    }

    private void sendWeixin(String content) {
        logger.info("sendWeixin content: {}", content);
        if (StringUtils.isBlank(content)) {
            logger.info("content is null!");
            return;
        }
        try {
            AccessToken accessToken = WeixinUtil.getAccessToken(eosConfig.getAppId(), eosConfig.getSecret());
            SendMessage msg = new SendMessage();
            msg.setAgentid(eosConfig.getAgentid());
            msg.getText().put("content", content);
            String result = WeixinUtil.sendText(msg, accessToken.getAccess_token());
            logger.info("{}", result);
        }catch (Exception ex){
            logger.error("weixin net error: exception", ex);
        }catch (Throwable tr){
            logger.error("weixin net error: throwable", tr);
        }
        Utils.sleep(eosConfig.getSendWeixinDelay());
    }

    private String[] getAllPhone() {
        if (StringUtils.isBlank(callConfig.getCallee())) {
            logger.error("phone config is blank!");
            return null;
        }
        String[] phones = callConfig.getCallee().split(",");
        if (phones == null || phones.length == 0) {
            return null;
        }
        return phones;
    }

    private String getTemplate(long accountNum, BigDecimal avaiable, BigDecimal vote, BigDecimal reward, long blockNum,
                               long supply, String date, long activeAccount, BigDecimal activeAccountBalance,
                               String blockDirSize, String stateDirSize, BigDecimal voterNum, BigDecimal stakedBalance,
                               BigDecimal unstakingBalance, long irreversibleBlockNum) {
        long blockNumDiff = blockNum - irreversibleBlockNum;
        String path = eosConfig.getCsvDir() + "chainBrief.csv";
        StringBuilder content = new StringBuilder();
        content.append(date).append(",");
        content.append(blockNum).append(",");
        content.append(accountNum).append(",");
        content.append(activeAccount).append(",");
        content.append(activeAccountBalance).append(",");
        content.append(avaiable).append(",");
        content.append(vote).append(",");
        content.append(reward).append(",");
        content.append(supply).append(",");
        content.append(blockDirSize).append(",");
        content.append(stateDirSize).append(",");
        content.append(voterNum).append(",");
        content.append(stakedBalance).append(",");
        content.append(unstakingBalance).append(",");
        content.append(irreversibleBlockNum).append(",");
        content.append(blockNumDiff);
        Utils.writeCsv(path, content.toString());

        return "区块时间戳:" + date.substring(5) + "\n" +
                "最新块高度:" + blockNum + "\n" +
                "不可逆高度:" + irreversibleBlockNum + "+" + blockNumDiff + "\n" +
                "全链用户数:" + accountNum + "\n" +
                "激活用户数:" + activeAccount + "\n" +
                "历史投票用户数:" + voterNum + "\n" +
                "激活总金额:" + activeAccountBalance + "\n" +
                "可用总金额:" + avaiable + "\n" +
                "投票总金额:" + stakedBalance.setScale(4, BigDecimal.ROUND_UP) + "\n" +
                "赎回总金额:" + unstakingBalance.setScale(4, BigDecimal.ROUND_UP) + "\n" +
                "奖池总金额:" + reward + " \n" +
                "全链总金额:" + supply + "\n" +
                "块目录容量:" + blockDirSize + "\n" +
                "块状态容量:" + stateDirSize + "\n"
                ;
    }
}
