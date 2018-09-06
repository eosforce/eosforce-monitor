package client.util;


import java.util.concurrent.*;

/**
 * @author lxg
 * @create 2018-06-08 16:23
 * @desc
 */
public class ExecutorsService {
    private static ExecutorService executorService = Executors.newFixedThreadPool(25);
    private static ExecutorService singleService = Executors.newSingleThreadExecutor();


    public static void execute(Runnable r) {
        executorService.execute(r);
    }

    public static <T> T executeCall(Callable<T> task) throws InterruptedException, ExecutionException {
        Future<T> feature = executorService.submit(task);
        return feature.get();
    }

    public static ExecutorService getSingleExecutorService(){
        return singleService;
    }
}
