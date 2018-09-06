package client.controller;

import client.TransactionApi;
import client.rpc.ReqBatchtwitter;
import client.rpc.ReqBlocktwitter;
import client.rpc.ReqTransfer;
import client.rpc.ReqVote;
import client.util.ExecutorsService;
import client.util.LLogger;
import client.util.LLoggerFactory;
import client.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lxg
 * @create 2018-06-08 16:03
 * @desc 压测交易
 */
@RequestMapping("/tx")
@RestController
public class BatchTransactionController {

    private LLogger lLogger = LLoggerFactory.getLogger(BatchTransactionController.class);
    @Autowired
    private TransactionApi transactionApi;
    private static ExecutorService singleService = Executors.newSingleThreadExecutor();

    @RequestMapping("/transfer")
    public void transfer(@RequestBody ReqTransfer reqTransfer) {
        if (!reqTransfer.isAsync()) {
            lLogger.info("transfer isAsync : false");
            for (int i = 0; i < reqTransfer.getLoop(); i++) {
                transactionApi.transfer(reqTransfer.getPrivacyKey(), reqTransfer.getFrom(), reqTransfer.getTo(), reqTransfer.getAmount(), reqTransfer.getContract(), reqTransfer.getFee());
            }
        } else {
            lLogger.info("transfer isAsync : true");
            for (int i = 0; i < reqTransfer.getLoop(); i++) {
                ExecutorsService.execute(() -> {
                    transactionApi.transfer(reqTransfer.getPrivacyKey(), reqTransfer.getFrom(), reqTransfer.getTo(), reqTransfer.getAmount(), reqTransfer.getContract(), reqTransfer.getFee());
                });
            }
        }
    }

    @RequestMapping("/tweet")
    public void tweet(@RequestBody ReqBlocktwitter reqBlocktwitter) {
        if (!reqBlocktwitter.isAsync()) {
            lLogger.info("blocktwitter isAsync : false");
            for (int i = 0; i < reqBlocktwitter.getLoop(); i++) {
                transactionApi.blocktwitter(reqBlocktwitter.getPrivacyKey(), reqBlocktwitter.getFrom(), reqBlocktwitter.getMessage(), reqBlocktwitter.getContract(), reqBlocktwitter.getFee());
            }
        } else {
            lLogger.info("blocktwitter isAsync : true");
            for (int i = 0; i < reqBlocktwitter.getLoop(); i++) {
                ExecutorsService.execute(() -> {
                    transactionApi.blocktwitter(reqBlocktwitter.getPrivacyKey(), reqBlocktwitter.getFrom(), reqBlocktwitter.getMessage(), reqBlocktwitter.getContract(), reqBlocktwitter.getFee());
                });
            }
        }
    }

    @RequestMapping("/batchTweet")
    public String batchTweet(@RequestBody ReqBatchtwitter reqBatchtwitter) {
        singleService.execute(() -> {
            if (!reqBatchtwitter.isAsync()) {
                lLogger.info("reqBatchtwitter isAsync : false");
                if (reqBatchtwitter.isGoOn()) {
                    while (true) {
                        try {
                            transactionApi.packedTransactionList(reqBatchtwitter.getPrivacyKey(), reqBatchtwitter.getMessage(), reqBatchtwitter.getFrom(), reqBatchtwitter.getCount(), reqBatchtwitter.isSuffix());
                        } catch (Exception e) {
                            lLogger.error("batch is error", e);
                        } catch (Throwable e) {
                            lLogger.error("batch is error", e);
                        }
                    }
                } else {
                    for (int i = 0; i < reqBatchtwitter.getLoop(); i++) {
                        try {
                            transactionApi.packedTransactionList(reqBatchtwitter.getPrivacyKey(), reqBatchtwitter.getMessage(), reqBatchtwitter.getFrom(), reqBatchtwitter.getCount(), reqBatchtwitter.isSuffix());
                        } catch (Exception e) {
                            lLogger.error("batch is error", e);
                        } catch (Throwable e) {
                            lLogger.error("batch is error", e);
                        }
                    }
                }
            } else {
                lLogger.info("reqBatchtwitter isAsync : true");
                if (reqBatchtwitter.isGoOn()) {
                    while (true) {
                        ExecutorsService.execute(() -> {
                            try {
                                transactionApi.packedTransactionList(reqBatchtwitter.getPrivacyKey(), reqBatchtwitter.getMessage(), reqBatchtwitter.getFrom(), reqBatchtwitter.getCount(), reqBatchtwitter.isSuffix());
                            } catch (Exception e) {
                                lLogger.error("batch is error", e);
                            } catch (Throwable e) {
                                lLogger.error("batch is error", e);
                            }
                        });
                        Utils.sleepSeed(50, 10);
                    }
                } else {
                    for (int i = 0; i < reqBatchtwitter.getLoop(); i++) {
                        ExecutorsService.execute(() -> {
                            try {
                                transactionApi.packedTransactionList(reqBatchtwitter.getPrivacyKey(), reqBatchtwitter.getMessage(), reqBatchtwitter.getFrom(), reqBatchtwitter.getCount(), reqBatchtwitter.isSuffix());
                            } catch (Exception e) {
                                lLogger.error("batch is error", e);
                            } catch (Throwable e) {
                                lLogger.error("batch is error", e);
                            }
                        });
                    }
                }
            }
        });
        return "SUCCESS";
    }

    @RequestMapping("/vote")
    public void vote(@RequestBody ReqVote reqVote) {
        if (!reqVote.isAsync()) {
            for (int i = 0; i < reqVote.getLoop(); i++) {
                transactionApi.vote(reqVote.getPrivacyKey(), reqVote.getVoter(), reqVote.getBpname(), reqVote.getChange(), reqVote.getContract(), reqVote.getFee());
            }
        } else {
            for (int i = 0; i < reqVote.getLoop(); i++) {
                ExecutorsService.execute(() -> {
                    transactionApi.vote(reqVote.getPrivacyKey(), reqVote.getVoter(), reqVote.getBpname(), reqVote.getChange(), reqVote.getContract(), reqVote.getFee());
                });
            }
        }
    }
}
