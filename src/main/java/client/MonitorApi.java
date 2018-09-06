package client;

import client.domain.GenesisAccount;
import client.job.BlockAccount;

import java.util.Map;
import java.util.Set;

/**
 * @author lxg
 * @create 2018-06-22 16:36
 * @desc
 */
public interface MonitorApi {
    BlockAccount findBlockAccount(Map<String, GenesisAccount> veteranAccountMap);

    BlockAccount getBlockAccount();

    void closeNet();

    void openNet();

    String statBlockDirSize();

    String statStateDirSize();
}
