package client.weixin.send;

/**
 * @author lxg
 * @create 2018-04-17 11:25
 * @desc 远程请求类型
 */
public enum MethodTypeEnum {
    post("post请求"),
    get("get请求");

    private String desc;

    MethodTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
