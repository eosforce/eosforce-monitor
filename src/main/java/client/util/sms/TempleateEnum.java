package client.util.sms;

/**
 * @author lxg
 * @create 2018-07-04 10:13
 * @desc
 */
public enum TempleateEnum {
    sms("SMS_133960266", "{\"code\":\"CODE\"}", "短信码"),
    warn("SMS_138079536", "{\"product\": \"PRODUCT\"}", "警情通知");
    private String templeateCode;
    private String desc;
    private String templeateParam;

    TempleateEnum(String templeateCode, String templeateParam, String desc) {
        this.templeateCode = templeateCode;
        this.templeateParam = templeateParam;
        this.desc = desc;
    }

    public String getTempleateCode() {
        return this.templeateCode;
    }

    public String getTempleateParam() {
        return this.templeateParam;
    }
}
