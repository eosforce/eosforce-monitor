package client.util;

import java.util.Map;

/**
 * @author lxg
 * @create 2018-05-01 10:27
 * @desc
 */
public interface ShellExecutor {
    public Map<String, String> exec(String cmds) throws Exception;
}
