package client.domain;

/**
 * @author lxg
 * @create 2018-07-04 17:54
 * @desc
 */
public class BpProducer {
    private String bpname;
    private long amount;

    public String getBpname() {
        return bpname;
    }

    public void setBpname(String bpname) {
        this.bpname = bpname;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
