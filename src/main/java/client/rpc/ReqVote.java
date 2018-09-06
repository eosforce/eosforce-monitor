package client.rpc;

/**
 * @author lxg
 * @create 2018-06-08 16:14
 * @desc 投票
 */
public class ReqVote {
    private String voter;
    private String bpname;
    private double change;
    private int loop = 1;
    private boolean async = false;
    private String privacyKey;
    private String contract;
    private double fee;

    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }

    public String getBpname() {
        return bpname;
    }

    public void setBpname(String bpname) {
        this.bpname = bpname;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
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
