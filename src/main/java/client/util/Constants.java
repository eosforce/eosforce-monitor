package client.util;

/**
 * Created by lxg on 2017/9/17.
 */
public class Constants {
    public static final String AUTHORIZATION = "token";
    public static final long DAY = 24 * 60 * 60 * 1000L;
    public static final String USER_ID = "userId";
    public static final String LOGIN_TIME = "loginTime";
    public static final String LUCKY_PREFIX = "0";
    public static final String DELIMITER_DOT = ",";
    public static final String TRACE_UUID = "uuid: ";
    public static final String STAR = "xxxx";
    public static final int NUMBER_PER_PAGE = 10;
    public static final String SUBFIX  = "file:///";


    public enum LotteryEnum {
        READY(0, "待审核"),
        OPEN(1, "开放"),
        FUND(2, "已投满，待开奖"),
        COSE(3, "开奖");
        private int status;
        private String desc;

        LotteryEnum(int status, String desc) {
            this.status = status;
            this.desc = desc;
        }

        public int getStatus() {
            return status;
        }
    }

    public enum BillingEnum {
        OPEN(0, "开启"),
        CLOSE(1, "关闭");
        private int status;
        private String desc;

        BillingEnum(int status, String desc) {
            this.status = status;
            this.desc = desc;
        }

        public int getStatus() {
            return status;
        }
    }
}
