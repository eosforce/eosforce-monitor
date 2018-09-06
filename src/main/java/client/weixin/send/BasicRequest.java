package client.weixin.send;

/**
 * @author lxg
 * @create 2018-06-24 15:16
 * @desc
 */
public class BasicRequest {
    private String url;
    private MethodTypeEnum methodType = MethodTypeEnum.get;
    private Object params;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MethodTypeEnum getType() {
        return methodType;
    }

    public void setType(MethodTypeEnum methodType) {
        this.methodType = methodType;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }
}
