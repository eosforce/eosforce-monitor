package client.util;

/**
 * @author lxg
 * @create 2018-04-25 14:26
 * @desc 常量
 */
public interface Constant {
    static String more_than_max_height_exception = "Height must be less than or equal to the current blockchain height";
    static String phone_regex = "^1[3|4|5|7|8][0-9]\\d{4,8}$";
    static String code_regex = "([0-9]{4})";
    static String chain_name_regix = "[a-z]{1,12}";
    static String digit_regix = "[0-9]*(\\.?)[0-9]*";
    static String integer_regix = "^[1-9]+[0-9]*$";
    static String email_regex = "\\w+(\\.\\w)*@\\w+(\\.\\w{2,3}){1,3}";
    static String[] header = new String[]{"区块时间戳",
            "最新块高度",
            "全链用户数",
            "激活用户数",
            "激活总金额",
            "可用总金额",
            "抵押总金额",
            "奖池总金额",
            "全链总金额",
            "块目录容量",
            "块状态容量",
            "历史投票用户数",
            "投票总金额",
            "赎回总金额",
            "不可逆高度",
            "不可逆差值"
    };
}
