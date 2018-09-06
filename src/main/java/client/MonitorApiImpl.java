package client;

import client.domain.GenesisAccount;
import client.domain.response.chain.ChainInfo;
import client.helper.AccountMonitorHelper;
import client.job.BlockAccount;
import client.util.ShellExecutor;
import client.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lxg
 * @create 2018-06-22 16:36
 * @desc
 */
@Component
public class MonitorApiImpl implements MonitorApi {
    private Logger logger = LoggerFactory.getLogger(MonitorApiImpl.class);
    @Autowired
    private EosConfig eosConfig;
    @Autowired
    private ShellExecutor executor;

    @Override
    public BlockAccount findBlockAccount(Map<String, GenesisAccount> veteranAccountMap) {
        BlockAccount blockAccount = AccountMonitorHelper.statisticAllBalance(
                AccountMonitorHelper.getEosApiRestClientList(
                        eosConfig.getAccountUrl()),  veteranAccountMap);
        return blockAccount;
    }

    @Override
    public BlockAccount getBlockAccount() {
        closeNet();
        BlockAccount blockAccount = null;
        try {
            List<EosApiRestClient> eosApiRestClientList = AccountMonitorHelper.getEosApiRestClientList(eosConfig.getAccountUrl());
            blockAccount = AccountMonitorHelper.statisticAllBalance(
                    AccountMonitorHelper.getEosApiRestClientList(eosConfig.getAccountUrl()));
            ChainInfo chainInfo = AccountMonitorHelper.getBlockInfo(eosApiRestClientList);
            blockAccount.setBlockNum(Long.parseLong(chainInfo.getHeadBlockNum()) - 1);
            long inflation = blockAccount.getTotalSupply() - eosConfig.getInitialSupply();
            long inflationOfStatistical = blockAccount.getBlockNum() * eosConfig.getBlockAward();
            logger.info("task is doing, initialSupply: {}, realSupply: {}, inflation: {}, inflationOfStatistical: {}, blockNum: {}, statisticalResult: ",
                    eosConfig.getInitialSupply(), blockAccount.getTotalSupply(), inflation, inflationOfStatistical, blockAccount.getBlockNum(), Utils.toJson(blockAccount));
        } catch (Exception e) {
            logger.error("error is: {}", e);
        } catch (Throwable t) {
            logger.error("error is: {}", t);
        }
        openNet();
        return blockAccount;
    }

    public void closeNet() {
        try {
            executor.exec(eosConfig.getNetEnd());
        } catch (Exception e) {
            logger.error("close network!", e);
        } catch (Throwable t) {
            logger.error("close network!", t);
        }
        Utils.sleep(1000);
    }

    public void openNet() {
        try {
            executor.exec(eosConfig.getNetStart());
        } catch (Exception e) {
            logger.error("open network!", e);
        } catch (Throwable t) {
            logger.error("open network", t);
        }
        Utils.sleep(1000);
    }

    public String statBlockDirSize(){
        try {
            Map<String, String> result = executor.exec(eosConfig.getNodeDiskDir()+"blocks");
            if (result != null && result.containsKey("data")) {
                return result.get("data");
            }
        } catch (Exception e) {
            logger.error("state blocksDirSize!", e);
        } catch (Throwable t) {
            logger.error("state blocksDirSize!", t);
        }
        return null;
    }

    public String statStateDirSize(){
        try {
            Map<String, String> result = executor.exec(eosConfig.getNodeDiskDir()+"state");
            if (result != null && result.containsKey("data")) {
                return result.get("data");
            }
        } catch (Exception e) {
            logger.error("state dataDirSize!", e);
        } catch (Throwable t) {
            logger.error("state dataDirSize!", t);
        }
        return null;
    }
}
