package client.domain.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lxg
 * @create 2018-06-20 10:49
 * @desc
 */
public class BlockRequest {
    private Date timestamp;
    private String producer;
    private String previous;
    private String transaction_mroot;
    private String action_mroot;
    private long schedule_version;
    private List<String> header_extensions = new ArrayList<>();
    private String producer_signature;
    private List<TransactionRequest> transactions = new ArrayList<>();
    private List<String> block_extensions = new ArrayList<>();

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getTransaction_mroot() {
        return transaction_mroot;
    }

    public void setTransaction_mroot(String transaction_mroot) {
        this.transaction_mroot = transaction_mroot;
    }

    public String getAction_mroot() {
        return action_mroot;
    }

    public void setAction_mroot(String action_mroot) {
        this.action_mroot = action_mroot;
    }

    public long getSchedule_version() {
        return schedule_version;
    }

    public void setSchedule_version(long schedule_version) {
        this.schedule_version = schedule_version;
    }

    public List<String> getHeader_extensions() {
        return header_extensions;
    }

    public void setHeader_extensions(List<String> header_extensions) {
        this.header_extensions = header_extensions;
    }

    public String getProducer_signature() {
        return producer_signature;
    }

    public void setProducer_signature(String producer_signature) {
        this.producer_signature = producer_signature;
    }

    public List<String> getBlock_extensions() {
        return block_extensions;
    }

    public void setBlock_extensions(List<String> block_extensions) {
        this.block_extensions = block_extensions;
    }

    public List<TransactionRequest> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionRequest> transactions) {
        this.transactions = transactions;
    }
}
