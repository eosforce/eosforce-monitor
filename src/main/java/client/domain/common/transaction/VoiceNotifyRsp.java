package client.domain.common.transaction;

/**
 * @author lxg
 * @create 2018-06-28 11:16
 * @desc
 */
public class VoiceNotifyRsp {
    private String callId;
    private String respCode;
    private String createDate;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
