package client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lxg
 * @create 2018-06-08 16:54
 * @desc
 */
@ConfigurationProperties(prefix = "eos")
@Component
public class EosConfig {
    private String nodeUrl;
    private String walletUrl;
    private String accountMonitor;
    private String accountUrl;
    private String netStart;
    private String netEnd;
    private String eosStatus;
    private String appId;
    private String secret;
    private String agentid;
    private int sendWeixinLimit;
    private int sendWeixinDelay;
    private int blockAward;
    private long initialSupply;
    private int notify;
    private double voteScale;
    private int confirmed;
    private int producerNode;
    private double producerBlockshrink;
    private int scheduleLoop;
    private String nodeDiskDir;
    private int blockNumberLayer;
    private int sendSmsPeriod;
    private int sendSmsCount;
    private double skipBlockNumberScale;
    private String csvDir;

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public String getWalletUrl() {
        return walletUrl;
    }

    public void setWalletUrl(String walletUrl) {
        this.walletUrl = walletUrl;
    }

    public String getAccountMonitor() {
        return accountMonitor;
    }

    public void setAccountMonitor(String accountMonitor) {
        this.accountMonitor = accountMonitor;
    }

    public String getAccountUrl() {
        return accountUrl;
    }

    public void setAccountUrl(String accountUrl) {
        this.accountUrl = accountUrl;
    }

    public String getNetStart() {
        return netStart;
    }

    public void setNetStart(String netStart) {
        this.netStart = netStart;
    }

    public String getNetEnd() {
        return netEnd;
    }

    public void setNetEnd(String netEnd) {
        this.netEnd = netEnd;
    }

    public String getEosStatus() {
        return eosStatus;
    }

    public void setEosStatus(String eosStatus) {
        this.eosStatus = eosStatus;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public int getBlockAward() {
        return blockAward;
    }

    public void setBlockAward(int blockAward) {
        this.blockAward = blockAward;
    }

    public long getInitialSupply() {
        return initialSupply;
    }

    public void setInitialSupply(long initialSupply) {
        this.initialSupply = initialSupply;
    }

    public int getSendWeixinLimit() {
        return sendWeixinLimit;
    }

    public void setSendWeixinLimit(int sendWeixinLimit) {
        this.sendWeixinLimit = sendWeixinLimit;
    }

    public int getSendWeixinDelay() {
        return sendWeixinDelay;
    }

    public void setSendWeixinDelay(int sendWeixinDelay) {
        this.sendWeixinDelay = sendWeixinDelay;
    }

    public int getNotify() {
        return notify;
    }

    public void setNotify(int notify) {
        this.notify = notify;
    }

    public double getVoteScale() {
        return voteScale;
    }

    public void setVoteScale(double voteScale) {
        this.voteScale = voteScale;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getProducerNode() {
        return producerNode;
    }

    public void setProducerNode(int producerNode) {
        this.producerNode = producerNode;
    }

    public double getProducerBlockshrink() {
        return producerBlockshrink;
    }

    public void setProducerBlockshrink(double producerBlockshrink) {
        this.producerBlockshrink = producerBlockshrink;
    }

    public int getScheduleLoop() {
        return scheduleLoop;
    }

    public void setScheduleLoop(int scheduleLoop) {
        this.scheduleLoop = scheduleLoop;
    }

    public String getNodeDiskDir() {
        return nodeDiskDir;
    }

    public void setNodeDiskDir(String nodeDiskDir) {
        this.nodeDiskDir = nodeDiskDir;
    }

    public int getBlockNumberLayer() {
        return blockNumberLayer;
    }

    public void setBlockNumberLayer(int blockNumberLayer) {
        this.blockNumberLayer = blockNumberLayer;
    }

    public int getSendSmsPeriod() {
        return sendSmsPeriod;
    }

    public void setSendSmsPeriod(int sendSmsPeriod) {
        this.sendSmsPeriod = sendSmsPeriod;
    }

    public int getSendSmsCount() {
        return sendSmsCount;
    }

    public void setSendSmsCount(int sendSmsCount) {
        this.sendSmsCount = sendSmsCount;
    }

    public double getSkipBlockNumberScale() {
        return skipBlockNumberScale;
    }

    public void setSkipBlockNumberScale(double skipBlockNumberScale) {
        this.skipBlockNumberScale = skipBlockNumberScale;
    }

    public String getCsvDir() {
        return csvDir;
    }

    public void setCsvDir(String csvDir) {
        this.csvDir = csvDir;
    }
}
