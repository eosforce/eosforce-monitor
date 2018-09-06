package client.rpc;

/**
 * @author lxg
 * @create 2018-06-08 16:11
 * @desc
 */
public class ReqTransfer {
    private String from;
    private String to;
    private double amount;
    private int loop = 1;
    private boolean async = false;
    private String privacyKey;
    private String contract;
    private double fee;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public String getPrivacyKey() {
        return privacyKey;
    }

    public void setPrivacyKey(String privacyKey) {
        this.privacyKey = privacyKey;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
