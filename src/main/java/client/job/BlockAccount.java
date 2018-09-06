package client.job;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lxg
 * @create 2018-06-23 17:30
 * @desc
 */
public class BlockAccount {
    private long totalAccount;
    private long totalSupply;
    private BigDecimal totalAccountBalance;
    private BigDecimal totalVoterBalance;
    private BigDecimal totalBpBalance;
    private long blockNum;
    private Map<String, BigDecimal> voteWarn = new HashMap<>();
    private Map<String, BigDecimal> accountWarn = new HashMap<>();
    private Map<String, BigDecimal> bpWarn = new HashMap<>();
    private long activeAccount;
    private BigDecimal activeAccountBalance;
    private String blockDirSize;
    private String stateDirSize;
    private BigDecimal voterNum = new BigDecimal("0");
    private BigDecimal unstakeBalance = new BigDecimal("0");
    private BigDecimal stakedBalance = new BigDecimal("0");
    private long irreversibleBlockNum;

    public long getTotalAccount() {
        return totalAccount;
    }

    public void setTotalAccount(long totalAccount) {
        this.totalAccount = totalAccount;
    }

    public void setTotalAccountBalance(BigDecimal totalAccountBalance) {
        this.totalAccountBalance = totalAccountBalance;
    }

    public void setTotalVoterBalance(BigDecimal totalVoterBalance) {
        this.totalVoterBalance = totalVoterBalance;
    }

    public void setTotalBpBalance(BigDecimal totalBpBalance) {
        this.totalBpBalance = totalBpBalance;
    }

    public long getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(long totalSupply) {
        this.totalSupply = totalSupply;
    }

    public BigDecimal getTotalAccountBalance() {
        return totalAccountBalance;
    }

    public BigDecimal getTotalVoterBalance() {
        return totalVoterBalance;
    }

    public BigDecimal getTotalBpBalance() {
        return totalBpBalance;
    }

    public long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(long blockNum) {
        this.blockNum = blockNum;
    }

    public Map<String, BigDecimal> getVoteWarn() {
        return voteWarn;
    }

    public void setVoteWarn(Map<String, BigDecimal> voteWarn) {
        this.voteWarn = voteWarn;
    }

    public Map<String, BigDecimal> getAccountWarn() {
        return accountWarn;
    }

    public void setAccountWarn(Map<String, BigDecimal> accountWarn) {
        this.accountWarn = accountWarn;
    }

    public Map<String, BigDecimal> getBpWarn() {
        return bpWarn;
    }

    public void setBpWarn(Map<String, BigDecimal> bpWarn) {
        this.bpWarn = bpWarn;
    }

    public long getActiveAccount() {
        return activeAccount;
    }

    public void setActiveAccount(long activeAccount) {
        this.activeAccount = activeAccount;
    }

    public BigDecimal getActiveAccountBalance() {
        return activeAccountBalance;
    }

    public void setActiveAccountBalance(BigDecimal activeAccountBalance) {
        this.activeAccountBalance = activeAccountBalance;
    }

    public String getBlockDirSize() {
        return blockDirSize;
    }

    public void setBlockDirSize(String blockDirSize) {
        this.blockDirSize = blockDirSize;
    }

    public String getStateDirSize() {
        return stateDirSize;
    }

    public void setStateDirSize(String stateDirSize) {
        this.stateDirSize = stateDirSize;
    }

    public BigDecimal getVoterNum() {
        return voterNum;
    }

    public void setVoterNum(BigDecimal voterNum) {
        this.voterNum = voterNum;
    }

    public void addupVoter() {
        synchronized (voterNum) {
            voterNum = voterNum.add(BigDecimal.ONE);
        }
    }

    public BigDecimal getUnstakeBalance() {
        return unstakeBalance;
    }

    public void setUnstakeBalance(BigDecimal unstakeBalance) {
        this.unstakeBalance = unstakeBalance;
    }

    public BigDecimal getStakedBalance() {
        return stakedBalance;
    }

    public void setStakedBalance(BigDecimal stakedBalance) {
        this.stakedBalance = stakedBalance;
    }

    public long getIrreversibleBlockNum() {
        return irreversibleBlockNum;
    }

    public void setIrreversibleBlockNum(long irreversibleBlockNum) {
        this.irreversibleBlockNum = irreversibleBlockNum;
    }
}
