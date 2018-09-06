package client.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author lxg
 * @create 2018-04-27 15:52
 * @desc 缓存工具
 */
public class CacheUtils {

    private static LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .refreshAfterWrite(1, TimeUnit.DAYS)
            .initialCapacity(100)
            .maximumSize(300).build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws ExecutionException {
                    return "";
                }
            });
    public static void put(String key, String value) {
        cache.put(key, value);
    }

    public static String get(String key) {
        try {
            return cache.get(key);
        } catch (ExecutionException ex) {

        } catch (Exception e) {

        } catch (Throwable e){

        }
        return null;
    }
}
