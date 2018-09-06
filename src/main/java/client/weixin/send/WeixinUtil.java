package client.weixin.send;

import client.util.LLogger;
import client.util.LLoggerFactory;
import client.util.Utils;

import java.io.IOException;

/**
 * @author lxg
 * @create 2018-06-24 14:53
 * @desc
 */
public class WeixinUtil {

    private static final String access_token_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=CorpID&corpsecret=SECRET";
    private static final String send_text_url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
    private static final LLogger logger = LLoggerFactory.getLogger(WeixinUtil.class);

    /**
     * 获取access_token
     *
     * @param corpID 企业Id
     * @param secret 管理组的凭证密钥，每个secret代表了对应用、通讯录、接口的不同权限；不同的管理组拥有不同的secret
     * @return
     */
    public static AccessToken getAccessToken(String corpID, String secret) {
        logger.info("getAccessToken!");
        String requestUrl = access_token_url.replace("CorpID", corpID).replace("SECRET", secret);
        BasicRequest basicRequest = new BasicRequest();
        basicRequest.setUrl(requestUrl);
        AccessToken accessToken = null;
        String result = HttpRequest.doGet(basicRequest);
        if (null != result) {
            accessToken = Utils.toObject(result, AccessToken.class);
        }
        return accessToken;
    }

    public static String sendText(SendMessage sendMessage, String accessToken) {
        logger.info("sendText!");
        String requestUrl = send_text_url.replace("ACCESS_TOKEN", accessToken);
        BasicRequest basicRequest = new BasicRequest();
        basicRequest.setUrl(requestUrl);
        basicRequest.setParams(sendMessage);
        try {
            String result = HttpRequest.doPost(basicRequest);
            logger.info("result: {}", result);
            return result;
        } catch (IOException e) {
            logger.error("error!", e);
        }
        return null;
    }

    public static void main(String[] args) {
        AccessToken accessToken = getAccessToken("ww2d5d437b61032271", "TROsTOzJ1U4aDWzyGxN174vNiCDllqH9WXpoMTB_-G4");
        //System.out.println(accessToken.getAccess_token());
        String accToken = "B6VkNzElRADznaKOkW6SeVNHmxUuJQFLdlAVA-rvEozYBCZmIp8csSah2Cxzh2h1XRup84_7z4MbE6LLi4G8mTpImTxdTHFvsvSbKlPO900_5QjSt_L_SKK5KA-OAeBHeqAUyygDWqoeltHG1taTtMpWOu-Z_VSZ1MFIrggmnGhp7JKzbcWMC_Ar_JJf6pLH5CC6RjYtHzAWCdDp6qOgqg";

        SendMessage msg = new SendMessage();
        msg.setAgentid("1000002");
        msg.getText().put("content", "中文test");
        sendText(msg, accessToken.getAccess_token());
    }
}
