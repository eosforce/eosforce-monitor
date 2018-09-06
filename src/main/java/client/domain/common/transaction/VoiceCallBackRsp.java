package client.domain.common.transaction;

/**
 * @author lxg
 * @create 2018-06-28 11:31
 * @desc
 */
public class VoiceCallBackRsp {
    private String respCode;
    private String message;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
