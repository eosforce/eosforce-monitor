package client;

import client.crypto.ec.EosPrivateKey;
import client.crypto.model.api.EosChainInfo;
import client.crypto.model.chain.Action;
import client.crypto.model.chain.SignedFeeTransaction;
import client.crypto.model.chain.SignedTransaction;
import client.crypto.model.types.TypeChainId;
import client.domain.common.transaction.*;
import client.domain.request.BlockRequest;
import client.domain.request.TransactionRequest;
import client.domain.request.chain.transaction.PushBlockRequest;
import client.domain.response.chain.AbiJsonToBin;
import client.domain.response.chain.Block;
import client.domain.response.chain.ChainInfo;
import client.domain.response.chain.transaction.PushedTransaction;
import client.helper.AccountMonitorHelper;
import client.util.ExecutorsService;
import client.util.LLogger;
import client.util.LLoggerFactory;
import client.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lxg
 * @create 2018-06-07 11:21
 * @desc 交易信息
 */
@Service
public class TransactionApiImpl implements TransactionApi {

    private static LLogger lLogger = LLoggerFactory.getLogger(TransactionApiImpl.class);
    @Autowired
    private EosConfig eosConfig;

    @Override
    public String transfer(String privacyKey, String from, String to, double amount, String contract, double fee) {
        String action = "transfer";
        Map<String, String> params = new HashMap<>(4);
        params.put("from", from);
        params.put("to", to);
        params.put("quantity", Utils.getEosBalance(amount));
        params.put("memo", "");
        return packedTransaction(privacyKey, params, from, action, fee, contract);
    }

    @Override
    public String vote(String privacyKey, String voter, String bpname, double change, String contract, double feeVal) {
        String action = "vote";
        Map<String, String> params = new HashMap<>(4);
        params.put("voter", voter);
        params.put("bpname", bpname);
        params.put("change", Utils.getEosBalance(change));
        //params.put("memo", "");
        return packedTransaction(privacyKey, params, voter, action, feeVal, contract);
    }

    @Override
    public String pushBlock(String from, String to, double amount) {
//        EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance(eosConfig.getNodeUrl()).newRestClient();
//        ChainInfo chainInfo = eosApiRestClient.getChainInfo();
//
//        BlockRequest blockRequest = new BlockRequest();
//        TransactionRequest transactionRequest = new TransactionRequest();
//        blockRequest.getTransactions().add(transactionRequest);
//        eosApiRestClient.pushBlock();
        buildPushBlockRequest();
        return null;
    }


    public void buildPushBlockRequest() {
        PushBlockRequest pushBlockRequest = new PushBlockRequest();
        /* Create the rest client */
        EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance(eosConfig.getNodeUrl()).newRestClient();
        ChainInfo chainInfo = eosApiRestClient.getChainInfo();
        Date chainHeadDate = Utils.parse(Utils.fullDateFormat_no_xxx, chainInfo.getHeadBlockTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(chainHeadDate);
        calendar.add(Calendar.SECOND, 5);
        pushBlockRequest.setTimestamp(Utils.fullDateFormat_no_xxx.format(calendar.getTime()));
        pushBlockRequest.setConfirmed(0);
        pushBlockRequest.setPrevious("0000001662df4cf662860a79ae9fc4cf50c0ade349cb909b332289e6379bf167");
        pushBlockRequest.setTransaction_mroot("546fb834087bdae23c9f86f7aea1e78f48756025f496db5344e0e349f9125b99");
        pushBlockRequest.setAction_mroot("fefc044aecf6ebf7cd4624b9700af2e2a0ea99175f181b8426052d6093892b7e");
        pushBlockRequest.setSchedule_version(0);
        pushBlockRequest.setProducer_signature("SIG_K1_K91gjrFHqNmhmuZt5bMRnEME5uhmm3iQzoJ6Y7nhCd9cHZYqpTPDPEbVyy23tEBqBL3cDpymws4hSBDK9zCuH16UQFXMvq");
        pushBlockRequest.setProducer("eosio");
        //TODO producer_signature
        BlockTransaction blockTransaction = new BlockTransaction();
        pushBlockRequest.getTransactions().add(blockTransaction);
        blockTransaction.setCpu_usage_us(1406);
        blockTransaction.setNet_usage_words(18);
        blockTransaction.setStatus("executed");
//
//        BlockTrx blockTrx = new BlockTrx();
//        blockTransaction.setTrx(blockTrx);
//        blockTrx.setCompression("none");
//        blockTrx.setPacked_context_free_data("");
//        List<Double> context_free_data = new ArrayList<>();
//        blockTrx.setContext_free_data(context_free_data);
//        blockTrx.setPacked_trx("54d2175b97012b202fb100000000010000000000ea30550000000000a032dd010000000000e0baeb00000000a8ed3232200000000000e0baeb00000060d683a93b40420f000000000004454f530000000000640000000000000004454f5300000000");
//
//        BlockTransactionInfo blockTransactionInfo = new BlockTransactionInfo();
//        blockTrx.setTransaction(blockTransactionInfo);
//        blockTransactionInfo.setExpiration(pushBlockRequest.getTimestamp());
//        blockTransactionInfo.setRef_block_num(407);
//        blockTransactionInfo.setRef_block_prefix(2972655659L);
//        blockTransactionInfo.setFee("0.0100 EOS");
//
//        BlockTransactionAction blockTransactionAction = new BlockTransactionAction();
//        blockTransactionInfo.getActions().add(blockTransactionAction);
//        blockTransactionAction.setAccount("eosio");
//        blockTransactionAction.setName("vote");
//
//        BlockTransactionAuthorization blockTransactionAuthorization = new BlockTransactionAuthorization();
//        blockTransactionAuthorization.setActor(blockTransactionAction.getName());
//        blockTransactionAuthorization.setPermission("active");
//
//        blockTransactionAction.getData().put("voter", blockTransactionAction.getName());
//        blockTransactionAction.getData().put("bpname", "biosbpn");
//        blockTransactionAction.getData().put("stake", "100.0000 EOS");
//        blockTransactionAction.setHex_data("0000000000e0baeb00000060d683a93b40420f000000000004454f5300000000");
        eosApiRestClient.pushBlock(pushBlockRequest);
    }

    public String blocktwitter(String privacyKey, String from, String message, String contract, double fee) {
        String action = "tweet";
        Map<String, String> params = new HashMap<>(2);
        params.put("message", message);
        return packedTransaction(privacyKey, params, from, action, fee, contract);
    }

    private String packedTransaction(String privacyKey, Map<String, String> params, String activer, String action, double feeVal, String contract) {
        String fee = null;
        if (feeVal > 0) {
            fee = Utils.getEosBalance(feeVal);
        }
        EosApiRestClient eosApiRestClient = AccountMonitorHelper.getEosApi(AccountMonitorHelper.getEosApiRestClientList(eosConfig.getAccountUrl()));
        ChainInfo chainInfo = eosApiRestClient.getChainInfo();
        EosPrivateKey eosPrivateKey = new EosPrivateKey(privacyKey);
        TypeChainId chainId = new TypeChainId(chainInfo.getChainId());
        AbiJsonToBin data = eosApiRestClient.abiJsonToBin(contract, action, params);

        EosChainInfo eosChainInfo = new EosChainInfo();
        eosChainInfo.setHeadBlockTime(chainInfo.getHeadBlockTime());
        eosChainInfo.setHeadBlockId(chainInfo.getHeadBlockId());

        JsonNode pushTxnResponse = null;
        if (fee == null) {
            SignedTransaction transaction = createTransaction(contract, action, data.getBinargs(), getActivePermission(activer), eosChainInfo, fee);
            transaction = signTransaction(transaction, eosPrivateKey, chainId);
            client.crypto.model.chain.PackedTransaction packedTransaction = buildTransaction(transaction);
            lLogger.info("signData: {}", Utils.toJson(packedTransaction));
            pushTxnResponse = eosApiRestClient.pushTransaction(packedTransaction);
        } else {
            SignedFeeTransaction transaction = createFeeTransaction(contract, action, data.getBinargs(), getActivePermission(activer), eosChainInfo, fee);
            transaction = new SignedFeeTransaction(transaction);
            transaction.sign(eosPrivateKey, chainId);
            client.crypto.model.chain.PackedTransaction packedTransaction = buildTransaction(transaction);
            lLogger.info("packedTransaction: {}", Utils.toJson(packedTransaction));
            pushTxnResponse = eosApiRestClient.pushTransaction(packedTransaction);
        }
        if (pushTxnResponse != null) {
            JsonNode transaction_id = pushTxnResponse.findValue("transaction_id");
            JsonNode result = transaction_id.findValue("_value");
            if (result != null) {
                lLogger.info("txId: {}", result.asText());
                return result.asText();
            } else {
                lLogger.info("txId: {}", transaction_id.asText());
                return transaction_id.asText();
            }
        }
        return null;
    }

    public String packedTransactionList(String privacyKey, String message, String activer, int count, boolean isSuffix) {
        String action = "tweet";
        String contract = "blocktwitter";
        String fee = null;
        ChainInfo chainInfo = getNode().getChainInfo();
        EosPrivateKey eosPrivateKey = new EosPrivateKey(privacyKey);
        TypeChainId chainId = new TypeChainId(chainInfo.getChainId());

        EosChainInfo eosChainInfo = new EosChainInfo();
        eosChainInfo.setHeadBlockTime(chainInfo.getHeadBlockTime());
        eosChainInfo.setHeadBlockId(chainInfo.getHeadBlockId());
        List<client.crypto.model.chain.PackedTransaction> packedTransactionList = new ArrayList<>(count);
        CountDownLatch latch = new CountDownLatch(count);
        AtomicInteger counterSuffix = new AtomicInteger(0);
        if (isSuffix) {
            for (int i = 0; i < count; i++) {
                ExecutorsService.execute(() -> {
                    Map<String, String> params = new HashMap<>(2);
                    params.put("message", message + counterSuffix.getAndIncrement());
                    AbiJsonToBin data = getNode().abiJsonToBin(contract, action, params);
                    SignedTransaction transaction = createTransaction(contract, action, data.getBinargs(), getActivePermission(activer), eosChainInfo, fee);
                    lLogger.info("sign before");
                    transaction = signTransaction(transaction, eosPrivateKey, chainId);
                    lLogger.info("sign after");
                    client.crypto.model.chain.PackedTransaction packedTransaction = buildTransaction(transaction);
                    synchronized (packedTransactionList) {
                        lLogger.info("into sender queue send msg {}", params.get("message"));
                        packedTransactionList.add(packedTransaction);
                    }
                    latch.countDown();
                });
            }
        } else {
            Map<String, String> params = new HashMap<>(2);
            params.put("message", message);
            AbiJsonToBin data = getNode().abiJsonToBin(contract, action, params);
            for (int i = 0; i < count; i++) {
                ExecutorsService.execute(() -> {
                    SignedTransaction transaction = createTransaction(contract, action, data.getBinargs(), getActivePermission(activer), eosChainInfo, fee);
                    lLogger.info("sign before");
                    transaction = signTransaction(transaction, eosPrivateKey, chainId);
                    lLogger.info("sign after");
                    client.crypto.model.chain.PackedTransaction packedTransaction = buildTransaction(transaction);
                    synchronized (packedTransactionList) {
                        lLogger.info("into sender queue send msg {}", (message + counterSuffix.getAndIncrement()));
                        packedTransactionList.add(packedTransaction);
                    }
                    latch.countDown();
                });
            }
        }
        lLogger.info("batch is go go go");
        try {
            latch.await();
        } catch (InterruptedException e) {
            lLogger.error("error", e);
        }
        lLogger.info("sender tx before, packed queue size: {}", packedTransactionList.size());
        if (packedTransactionList.size() > 0) {
            List<JsonNode> pushTxnResponseList = getNode().pushTransactions(packedTransactionList);
            lLogger.info("sender tx after, pushTxnResult: {}", Utils.toJson(pushTxnResponseList));
        }
        return null;
    }

//    private void buildPackTransaction(String contract, String action, String activer, EosChainInfo eosChainInfo, String fee, String binargs,
//                                      TypeChainId chainId, EosPrivateKey eosPrivateKey, String message, ){
//        SignedTransaction transaction = createTransaction(contract, action, binargs, getActivePermission(activer), eosChainInfo, fee);
//        lLogger.info("sign before");
//        transaction = signTransaction(transaction, eosPrivateKey, chainId);
//        lLogger.info("sign after");
//        client.crypto.model.chain.PackedTransaction packedTransaction = buildTransaction(transaction);
//        synchronized (packedTransactionList) {
//            lLogger.info("into sender queue send msg {}", (message + counterSuffix.getAndIncrement()));
//            packedTransactionList.add(packedTransaction);
//        }
//    }

    public EosApiRestClient getNode() {
        return AccountMonitorHelper.getEosApi(AccountMonitorHelper.getEosApiRestClientList(eosConfig.getAccountUrl()));
    }

    public client.crypto.model.chain.PackedTransaction buildTransaction(SignedTransaction stxn) {
        return new client.crypto.model.chain.PackedTransaction(stxn);
    }

    public client.crypto.model.chain.PackedTransaction buildTransaction(SignedFeeTransaction stxn) {
        return new client.crypto.model.chain.PackedTransaction(stxn);
    }

    public SignedTransaction signTransaction(final SignedTransaction txn,
                                             EosPrivateKey privateKey, final TypeChainId id) throws IllegalStateException {
        SignedTransaction stxn = new SignedTransaction(txn);
        stxn.sign(privateKey, id);
        return stxn;
    }

    private SignedTransaction createTransaction(String contract, String actionName, String dataAsHex,
                                                String[] permissions, EosChainInfo chainInfo, String fee) {
        Action action = new Action(contract, actionName);
        action.setAuthorization(permissions);
        action.setData(dataAsHex);

        SignedTransaction txn = new SignedTransaction();
        txn.addAction(action);
        txn.putSignatures(new ArrayList<>());
        //txn.setFee(fee);
        if (null != chainInfo) {
            txn.setReferenceBlock(chainInfo.getHeadBlockId());
            txn.setExpiration(chainInfo.getTimeAfterHeadBlockTime(Utils.getRandNumber(1800000, 300000)));
        }
        return txn;
    }

    private SignedFeeTransaction createFeeTransaction(String contract, String actionName, String dataAsHex,
                                                      String[] permissions, EosChainInfo chainInfo, String fee) {
        Action action = new Action(contract, actionName);
        action.setAuthorization(permissions);
        action.setData(dataAsHex);

        SignedFeeTransaction txn = new SignedFeeTransaction();
        txn.addAction(action);
        txn.putSignatures(new ArrayList<>());
        txn.setFee(fee);
        if (null != chainInfo) {
            txn.setReferenceBlock(chainInfo.getHeadBlockId());
            txn.setExpiration(chainInfo.getTimeAfterHeadBlockTime(Utils.getRandNumber(30000, 300000)));
        }
        return txn;
    }

    private String[] getActivePermission(String accountName) {
        return new String[]{accountName + "@active"};
    }

    public static void main(String[] args) {
        String contract = "eosio.token";
        String actionName = "transfer";
        Action action = new Action(contract, actionName);
        action.setAuthorization(new String[]{"bihu@active"});
        action.setData("0000000000a09b3b00000000004f0febe80300000000000004454f530000000000");

        SignedFeeTransaction txn = new SignedFeeTransaction();
        txn.addAction(action);
        txn.putSignatures(new ArrayList<>());
        txn.setFee("0.0100 EOS");
        txn.setRef_block_num(21099);
        txn.setRef_block_prefix(3856964915L);
        txn.setExpiration("2018-07-11T02:57:00");
        EosPrivateKey eosPrivateKey = new EosPrivateKey("");
        TypeChainId chainId = new TypeChainId("bd61ae3a031e8ef2f97ee3b0e62776d6d30d4833c8f7c1645c657b149151004b");
        int loop = 10;
        long totolCostTime = 0;
        for (int j = 0; j < loop; j++) {
            long outter = System.currentTimeMillis();
            int count = 1500;
            CountDownLatch latch = new CountDownLatch(count);
            for (int i = 0; i < count; i++) {
                ExecutorsService.execute(() -> {
                    SignedFeeTransaction transaction = txn;
                    transaction = new SignedFeeTransaction(transaction);
                    transaction.sign(eosPrivateKey, chainId);
                    latch.countDown();
                });
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            totolCostTime += System.currentTimeMillis() - outter;
            lLogger.info("outter, diff: {}ms", (System.currentTimeMillis() - outter));
        }
        lLogger.info("loop: {}, avg: {}", loop, totolCostTime / loop);
        //client.crypto.model.chain.PackedTransaction packedTransaction = new client.crypto.model.chain.PackedTransaction(transaction);
        //lLogger.info("packedTransaction: {}", Utils.toJson(packedTransaction));
    }
}
