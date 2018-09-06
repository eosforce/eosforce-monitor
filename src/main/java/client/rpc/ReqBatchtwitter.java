package client.rpc;

/**
 * @author lxg
 * @create 2018-06-08 16:11
 * @desc
 */
public class ReqBatchtwitter {
    private String from;
    private int loop = 1;
    private boolean async = false;
    private String privacyKey;
    private String message;
    private int count;
    private boolean isGoOn;
    private boolean isSuffix;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isGoOn() {
        return isGoOn;
    }

    public void setGoOn(boolean isGoOn) {
        this.isGoOn = isGoOn;
    }

    public boolean isSuffix() {
        return isSuffix;
    }

    public void setSuffix(boolean isSuffix) {
        this.isSuffix = isSuffix;
    }
}
