package client.util;

import client.util.EncryptUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lxg
 * @create 2018-06-28 15:47
 * @desc
 */
public class CallUtils {

    public static void main(String[] args){
        String userId = "";
        String token = "";
        long timestamp = 1525455683909L;
        System.out.println();
    }

    public static String getSignature(String accountSid, String authToken, long timestamp, String publickKey) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", accountSid);
        params.put("token", authToken);
        params.put("timestamp", timestamp);
        String signParams = Utils.toJson(params);
        System.out.print("signParams: "+signParams);
        String sign = EncryptUtil.RSAEncoderByPublic(signParams, publickKey);
        System.out.print("sign: \n"+sign);
        return sign;
    }

}
