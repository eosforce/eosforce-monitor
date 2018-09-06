package client.util;

import client.crypto.ec.EosPrivateKey;
import client.domain.GenesisAccount;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * Created by lxg on 2016/10/21.
 */
public class Utils {
    private static final Logger logger = getLogger(Utils.class);
    public static final ObjectMapper mapper = new ObjectMapper();
    public static final String FULL_DATE = "yyyyMMddHHmmss";
    public static final String FULL_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String FULL_DATE_TWO = "yyyy-MM-dd HH:mm:ss";
    private static final Random rand = new Random();
    private static final Gson gson = new Gson();
    private static final int DEFAULT_SEED = 10000;
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    public static SimpleDateFormat fullDateFormat_no_xxx = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final int FILE_MAX_SIZE = 20 * 1024 * 1024;
    private static final Random random = new Random();
    private static final String pattern_regex_float = "(-?\\d+.?\\d+)";
    private static final String pattern_regex_string = "^[ \\t]*[\\{\\[]";
    private static final String pattern_regex_disk_size = "(-?\\d+.?\\d*G*)|(-?\\d+.?\\d*M*)";
    private static ThreadLocal threadLocalDate = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new SimpleDateFormat(FULL_DATE_TIME);
        }
    };

    private static ThreadLocal threadLocalDateTwo = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new SimpleDateFormat(FULL_DATE_TWO);
        }
    };
    public static final int PAGE_DEFAULT = 1;

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    public static final int PAGE_SIZE = 10;

    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

    public static String toJsonString(Object... params) {
        try {
            return mapper.writeValueAsString(params);
        } catch (Exception e) {
            logger.error("param: {}, error: {}", params, e.getMessage());
        }
        return "";
    }

    public static String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static <T> T toObject(String source, Class<T> t) {
        try {
            return mapper.readValue(source, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T toObject(String source, TypeReference t) {
        try {
            return mapper.readValue(source, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String joinUrl(String addr, String methodName) {
        if (isEmpty(addr)) {
            throw new IllegalArgumentException();
        }

        return addr.endsWith("/") ? (addr + methodName) : (addr + "/" + methodName);
    }

    public static JsonNode getJsonNode(String str) {
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(str);
        } catch (IOException io) {
            logger.error("parse str is error : {}", io.getMessage());
        }
        return jsonNode;
    }

    /**
     * 获取完整日期
     *
     * @return
     */
    public static String getFullDate() {
        return new SimpleDateFormat(FULL_DATE).format(new Date());
    }

    /**
     * 生成一个随机数字，在种子范围内
     *
     * @param seed
     * @return
     */
    public static int getRandNumber(int seed) {
        return rand.nextInt(seed);
    }

    /**
     * 生成一个随机数字,在10000范围内
     *
     * @return
     */
    public static int getRandNumber() {
        return getRandNumber(DEFAULT_SEED);
    }

    /**
     * 生成一个随机数字, 大于等于seed, 小于等于seed+rand
     *
     * @param seed
     * @param rand
     * @return
     */
    public static int getRandNumber(int seed, int rand) {
        return seed + getRandNumber(rand);
    }

    /**
     * 线程唯一号
     *
     * @return
     */
    public static String getUUID() {
        String uuid = getFullDate() + getRandNumber();
        return uuid;
    }

    /**
     * 获取当前网络ip
     *
     * @param request
     * @return
     */
    public String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (inet != null)
                    ipAddress = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    public static DateFormat getDateFormat() {
        return (DateFormat) threadLocalDate.get();
    }

    public static Date parse(String textDate) {
        try {
            return getDateFormat().parse(textDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return null;
    }

    public static DateFormat getDateFormatTwo() {
        return (DateFormat) threadLocalDateTwo.get();
    }

    public static String formatDate(String sourceDate) {
        if (isEmpty(sourceDate))
            return "";
        try {
            Date date = getDateFormat().parse(sourceDate);
            return getDateFormatTwo().format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String formatData(long time) {
        Date date = new Date(time);
        return getDateFormatTwo().format(date);
    }

    public static long getTotalPage(long totalRecord, int pageSize) {
        if (totalRecord == 0) return 0;
        long pages = totalRecord / pageSize;
        return totalRecord % pageSize == 0 ? pages : pages + 1;
    }

    public static String toJsonByGson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T fromJsonByGson(String string, Class<T> clazz) {
        return gson.fromJson(string, clazz);
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request
     * @return
     * @throws java.io.IOException
     */
    public final static String getIpAddress(HttpServletRequest request) throws IOException {
        String ip = request.getHeader("X-Forwarded-For");
        if (logger.isInfoEnabled()) {
            logger.debug("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip={}", ip);
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                logger.debug("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip={}", ip);
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                logger.debug("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                logger.debug("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip = {}", ip);
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                logger.debug("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip= {}", ip);
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                logger.debug("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip= {}", ip);
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    public static int getPageNum(long totalPage, int pageNum) {
        if (pageNum > totalPage) {
            return totalPage == 0 ? 1 : (int) totalPage;
        } else if (pageNum < 0) {
            return 1;
        } else {
            return pageNum;
        }
    }


    public static String getResource(String fileName) {
        StringBuilder textBuilder = new StringBuilder();
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String str;
            while ((str = in.readLine()) != null) {
                textBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        return textBuilder.toString();
    }

    public static String getResourceText(String fileName) {
        StringBuilder textBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String str;
            while ((str = in.readLine()) != null) {
                textBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        return textBuilder.toString();
    }

    public static void writeInFile(String filepath, String content) {
        File f = new File(filepath);
        FileOutputStream fos = null;
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            fos = new FileOutputStream(f);
            fos.write(content.getBytes());
        } catch (IOException e) {
            logger.error("io error!", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] arg) throws ParseException {
//        StringBuilder textBuilder = new StringBuilder();
//        List<GenesisAccount> genesisAccountList = new ArrayList<>();
//        GenesisAccount genesisAccount = null;
//        try {
//            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\code\\accounts_snapshot.csv")));
//            String str;
//            while ((str = in.readLine()) != null) {
//                String[] line = str.replaceAll("\"", "").split(",");
//                genesisAccount = new GenesisAccount();
//                genesisAccount.setName(line[1]);
//                genesisAccount.setAsset(line[3]);
//                genesisAccountList.add(genesisAccount);
//            }
//            in.close();
//        } catch (IOException e) {
//        }
//        logger.info("totalSize : {}", genesisAccountList.size());
//        writeInFile("D:\\code\\account_snapshots.json", Utils.toJson(genesisAccountList));
//        JsonNode jsonNode = Utils.toObject(resource, JsonNode.class);
//        JsonNode initial_account_list = jsonNode.get("initial_account_list");
//        List<JsonNode> jsonNodes = initial_account_list.findValues("name");


//        String s = "E:/res/" + Utils.dateFormat.format(Utils.parse(Utils.dateFormat, "2017-01-04 12:34:53")) + ".csv";
//        writeCsv(s, StringUtils.join(Constant.header, ","));
//          System.out.println(formatEosBalance(0.03556332));
//        EosPrivateKey eosPrivateKey = new EosPrivateKey("5KRSqFqF3namG6wSG6C6hAmqFZhCHpzAhTz3boeYW7C4XAf9bmC");
//        System.out.println(eosPrivateKey.getPublicKey());

    }


    public static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static void sleepSeed(int time) {
        try {
            Thread.sleep(random.nextInt(time));
        } catch (InterruptedException ex) {
        }
    }

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
        }
    }

    public static void sleepSeed(int time, int fixed) {
        try {
            Thread.sleep(random.nextInt(time) + fixed);
        } catch (InterruptedException ex) {
        }
    }

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(Constant.phone_regex, mobile);
    }

    /**
     * 校验验证码
     *
     * @param code
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isCode(String code) {
        return Pattern.matches(Constant.code_regex, code);
    }

    /**
     * 校验eos用户名称
     *
     * @param chainName
     * @return
     */
    public static boolean isChainName(String chainName) {
        return Pattern.matches(Constant.chain_name_regix, chainName);
    }

    /**
     * 校验用户余额
     *
     * @param amount
     * @return
     */
    public static boolean isDigit(String amount) {
        return Pattern.matches(Constant.digit_regix, amount);
    }

    /**
     * 校验整数
     *
     * @param amount
     * @return
     */
    public static boolean isInteger(String amount) {
        return Pattern.matches(Constant.integer_regix, amount);
    }

    public static boolean isEmail(String email) {
        return Pattern.matches(Constant.email_regex, email);
    }

    public static String getCode() {
        return String.format("%04d", random.nextInt(9999));
    }

    public static String getRandomUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static Date parseTimestamp(String timestamp) {
        try {
            return Utils.sdf.parse(timestamp);
        } catch (ParseException e) {
            logger.error("parseException : {}", e);
        }
        return null;
    }

    public static String match(String patternRegex, String source) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static float matchFloat(String source) {
        String result = match(pattern_regex_float, source);
        if (result != null) {
            return Float.parseFloat(result);
        }
        return 0;
    }

    public static double matchDouble(String source) {
        String result = match(pattern_regex_float, source);
        if (result != null) {
            return Double.parseDouble(result);
        }
        return 0;
    }

    public static Date parse(SimpleDateFormat format, String date) {
        try {
            return format.parse(date);
        } catch (ParseException e) {
            logger.error("parse is {}", e);
        }
        return null;
    }

    public static Date parseDate(String date) {
        try {
            return fullDateFormat_no_xxx.parse(date);
        } catch (ParseException e) {
            logger.error("parse is {}", e);
        }
        return null;
    }

    public static String toRate(double v) {
        return v * 100 + "%";
    }

    public static String formatEosBalance(double balance) {
        DecimalFormat fnum = new DecimalFormat("##0.0000");
        return fnum.format(balance);
    }

    public static String formatBigDecimal(BigDecimal bigDecimal) {
        DecimalFormat fnum = new DecimalFormat("0.0000");
        return fnum.format(bigDecimal);
    }

    public static String getEosBalance(double balance) {
        return formatEosBalance(balance) + " EOS";
    }

    public static String getBJDate(String datetime) {
        Date date = null;
        try {
            date = Utils.fullDateFormat_no_xxx.parse(datetime);
        } catch (ParseException e) {
            logger.error("date is error!", e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
        return Utils.formatData(calendar.getTimeInMillis());
    }

    public static String matchDirSize(String dirData) {
        String dirSizeInfo = match(pattern_regex_disk_size, dirData);
        if (dirSizeInfo.endsWith("G")) {
            return dirSizeInfo.trim();
        } else if (!dirSizeInfo.endsWith("M")) {
            return dirSizeInfo.trim() + "M";
        }
        return dirSizeInfo;
    }


    public static void writeCsv(String path, String content) {
        try {
            File csv = new File(path);
            boolean isExists = true;
            if (!csv.exists()) {
                isExists = false;
                csv.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true));
            if (!isExists) {
                bw.write(StringUtils.join(Constant.header, ","));
            }
            bw.newLine();
            bw.write(content);
            bw.close();
        } catch (FileNotFoundException e) {
            logger.error("file not found", e);
        } catch (IOException e) {
            logger.error("file not found", e);
        } catch (Throwable e) {
            logger.error("writeCsv is error", e);
        }
    }
}

