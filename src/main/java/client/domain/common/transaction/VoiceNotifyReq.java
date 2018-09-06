package client.domain.common.transaction;

/**
 * @author lxg
 * @create 2018-06-28 11:08
 * @desc
 */
public class VoiceNotifyReq {
    private String caller = "";
    private String playTimes = "1";
    private String callee;
    private String type = "0";
    private String templateId = "";
    private String content;
    private String userData;
    private String hangupUrl;

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getPlayTimes() {
        return playTimes;
    }

    public void setPlayTimes(String playTimes) {
        this.playTimes = playTimes;
    }


    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getHangupUrl() {
        return hangupUrl;
    }

    public void setHangupUrl(String hangupUrl) {
        this.hangupUrl = hangupUrl;
    }
}
