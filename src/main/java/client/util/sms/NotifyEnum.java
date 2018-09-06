package client.util.sms;

/**
 * @Author: lxg
 * @Description:
 * @Date: {DATE}.
 */
public enum NotifyEnum {

    balance_not_equals("账号余额", 0),
    balance_vote_large("大额投票", 1),
    block_number_pause("同步服务", 0),
    vote_illegal("投票", 0),
    bp_illegal("奖金池", 0),
    balance_illegal("余额", 0),
    block_confirmed("confirm", 1),
    block_skip("跳块", 1),
    node_conn("节点连接", 0),
    block_loop("换届", 1),
    block_version("换届版本号", 1)
    ;
    private String desc;
    //0 极端重要， 1一般
    private int layer;

    NotifyEnum(String desc, int layer) {
        this.desc = desc;
        this.layer = layer;
    }

    public String getDesc() {
        return this.desc;
    }

    public int getLayer(){
        return this.layer;
    }

    public static String[] getDescs(){
        NotifyEnum[] values = values();
        String[] descs = new String[values.length];
        for(int i = 0;i < values.length; i++){
            descs[i] = values[i].desc;
        }
        return descs;
    }

    public static String[] getDescs(NotifyEnum... notifyEnums){
        String[] descs = new String[notifyEnums.length];
        for(int i = 0;i < notifyEnums.length; i++){
            descs[i] = notifyEnums[i].desc;
        }
        return descs;
    }
}
