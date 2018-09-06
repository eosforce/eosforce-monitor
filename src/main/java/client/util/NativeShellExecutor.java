package client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lxg
 * @create 2018-04-30 16:26
 * @desc 本地shell命令调用
 */
@Component
public class NativeShellExecutor implements ShellExecutor {
    private static Logger logger = LoggerFactory.getLogger(NativeShellExecutor.class);

    /**
     * 执行本地命令
     *
     * @param cmd
     * @return
     */
    public static Map<String, String> execCmd(String... cmd) {
        logger.info("executor : nativeCmd : {}", Utils.toJson(cmd));
        Map<String, String> result = new HashMap<>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd, null, null);
            String data = processStream(proc.getInputStream(), "UTF-8");
            String err = processStream(proc.getErrorStream(), "UTF-8");
            result.put("data", data);
            result.put("err", err);
            int status = proc.waitFor();
            result.put("status", String.valueOf(status));
        } catch (IllegalThreadStateException threadState) {
            logger.info("thread status is error!");
        } catch (Exception e) {
            logger.error("native cmd is error", e);
        } catch (Throwable t) {
            logger.error("native cmd is error", t);
        }
        logger.info("nativeCmd result : {}", result);
        return result;
    }

    /**
     * 过程结果
     *
     * @param in
     * @param charset
     * @return
     * @throws Exception
     */
    private static String processStream(InputStream in, String charset) throws Exception {
        InputStreamReader isr = new InputStreamReader(in, charset);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        int i = 10;
        while (i-- > 0) {
            String[] cmd = new String[]{
                    "cmd.exe", "/C", "echo \"hello word\""};
            System.out.println(execCmd(cmd));
        }
    }

    @Override
    public Map<String, String> exec(String cmds) throws Exception {
        return execCmd("/bin/bash", "-c", cmds);
    }
}
