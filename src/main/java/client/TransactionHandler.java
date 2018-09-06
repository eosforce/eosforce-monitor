package client;

import client.crypto.digest.Sha256;
import client.crypto.ec.EcDsa;
import client.crypto.ec.EcSignature;
import client.crypto.ec.EosPrivateKey;
import client.crypto.ec.EosPublicKey;
import client.crypto.model.api.EosChainInfo;
import client.crypto.model.api.JsonToBinRequest;
import client.crypto.model.api.PushTxnResponse;
import client.crypto.model.chain.Action;
import client.crypto.model.chain.PackedTransaction;
import client.crypto.model.chain.SignedTransaction;
import client.crypto.model.types.EosTransfer;
import client.crypto.model.types.TypeChainId;
import client.domain.response.chain.AbiJsonToBin;
import client.domain.response.chain.Block;
import client.domain.response.chain.ChainInfo;
import client.domain.response.chain.transaction.PushedTransaction;
import client.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lxg
 * @create 2018-06-11 20:26
 * @desc
 */
@Service
public class TransactionHandler {

    public static short[] toShortArray(byte[] src) {

        int count = src.length >> 1;
        short[] dest = new short[count];
        for (int i = 0; i < count; i++) {
            dest[i] = (short) (src[i * 2] << 8 | src[2 * i + 1] & 0xff);
        }
        return dest;
    }

    public PackedTransaction packedTransaction(SignedTransaction stxn) {
        return new PackedTransaction(stxn);
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
            txn.setExpiration(chainInfo.getTimeAfterHeadBlockTime(30000));
        }
        return txn;
    }

    private SignedTransaction createTransaction(String contract, String actionName, String dataAsHex,
                                                String[] permissions, EosChainInfo chainInfo) {
        Action action = new Action(contract, actionName);
        action.setAuthorization(permissions);
        action.setData(dataAsHex);

        SignedTransaction txn = new SignedTransaction();
        txn.addAction(action);
        txn.putSignatures(new ArrayList<>());
        if (null != chainInfo) {
            txn.setReferenceBlock(chainInfo.getHeadBlockId());
            txn.setExpiration(chainInfo.getTimeAfterHeadBlockTime(30000));
        }
        return txn;
    }


    public void execVote() {
        EosPrivateKey eosPrivateKey = new EosPrivateKey("5JhASkJHqq75fSmpnkQNX3eeZNCS31p1sU2fPTP6GGASwvey8kv");
        TypeChainId chainId = new TypeChainId("434b74c872f5b9a23669822929811a010ad56b5c1fd288b938a992e3035b8f39");
        String from = "beijing333";
        String to = "biosbpj";
        double amount = 1;
        String memo = "";
        String contract = "eosio";
        String action = "vote";
        double feeVal = 0.1;
        String fee = Utils.getEosBalance(feeVal);
        Map<String, String> args = new HashMap<>(4);
        args.put("from", from);
        args.put("to", to);
        args.put("quantity", Utils.getEosBalance(amount));
        args.put("memo", memo);
        EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance("http://192.168.0.17:8888").newRestClient();
        AbiJsonToBin data = eosApiRestClient.abiJsonToBin(contract, action, args);

        ChainInfo chainInfo = eosApiRestClient.getChainInfo();


        EosChainInfo eosChainInfo = new EosChainInfo();
        eosChainInfo.setHeadBlockTime(chainInfo.getHeadBlockTime());
        eosChainInfo.setHeadBlockId(chainInfo.getHeadBlockId());
        SignedTransaction transaction = createTransaction(contract, action, data.getBinargs(), getActivePermission(from), eosChainInfo, fee);
        //System.out.println("before signedTransaction: "+Utils.toJson(transaction));
        transaction = signTransaction(transaction, eosPrivateKey, chainId);
        //System.out.println("after signedTransaction: "+Utils.toJson(transaction));
        PackedTransaction packedTransaction = packedTransaction(transaction);
        JsonNode pushTxnResponse = eosApiRestClient.pushTransaction(packedTransaction);
        //eosApiRestClient.pushTransaction()
        //System.out.println(pushTxnResponse.getTransactionId());
    }

    public void testSign() {
        EosPrivateKey eosPrivateKey = new EosPrivateKey("5JhASkJHqq75fSmpnkQNX3eeZNCS31p1sU2fPTP6GGASwvey8kv");
        TypeChainId chainId = new TypeChainId("434b74c872f5b9a23669822929811a010ad56b5c1fd288b938a992e3035b8f39");
        String from = "beijing333";
        String contract = "eosio";
        String actionName = "vote";
        String blocktTime = "2018-06-12T08:15:33";
        String dataAsHex = "00c018834df79c3a000075c025f53055102700000000000004454f5300000000";
        double feeVal = 0.01;
        String fee = Utils.getEosBalance(feeVal);
        EosChainInfo eosChainInfo = new EosChainInfo();
        eosChainInfo.setHeadBlockTime(blocktTime);
        Action action = new Action(contract, actionName);
        action.setAuthorization(getActivePermission(from));
        action.setData(dataAsHex);

        SignedTransaction txn = new SignedTransaction();
        txn.addAction(action);
        txn.putSignatures(new ArrayList<>());
        //txn.setFee(fee);
        if (null != eosChainInfo) {
            txn.setReferenceBlock(eosChainInfo.getHeadBlockId());
            //txn.setReferenceBlock(32489, 188296515L);
            txn.setExpiration(blocktTime);
        }
        SignedTransaction signedTransaction = new SignedTransaction(txn);
        signedTransaction.sign(eosPrivateKey, chainId);
        PackedTransaction packedTransaction = packedTransaction(signedTransaction);
        System.out.println(signedTransaction.getSignatures());
    }

    public void execTransfer() {
        EosPrivateKey eosPrivateKey = new EosPrivateKey("5HtNzWFYM8Tzt6oBGbmg8UcKFfAZapsna2MNat3ZQz7d9R8ZK3T");
        String from = "user1";
        String to = "eosio";
        double amount = 1;
        String memo = "";
        String contract = "eosio.token";
        String action = "transfer";
        Map<String, String> args = new HashMap<>(4);
        args.put("from", from);
        args.put("to", to);
        args.put("quantity", Utils.getEosBalance(amount));
        args.put("memo", memo);
        EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance("http://192.168.2.158:8888").newRestClient();
        AbiJsonToBin data = eosApiRestClient.abiJsonToBin(contract, action, args);
        ChainInfo chainInfo = eosApiRestClient.getChainInfo();
        System.out.println("data: " + data.getBinargs());
        System.out.println("chainInfo: " + Utils.toJson(chainInfo));
        System.out.println("chainId: " + chainInfo.getChainId());
        TypeChainId chainId = new TypeChainId(chainInfo.getChainId());
        EosChainInfo eosChainInfo = new EosChainInfo();
        eosChainInfo.setHeadBlockTime(chainInfo.getHeadBlockTime());
        eosChainInfo.setHeadBlockId(chainInfo.getHeadBlockId());
        SignedTransaction transaction = createTransaction(contract, action, data.getBinargs(), getActivePermission(from), eosChainInfo);
        transaction = signTransaction(transaction, eosPrivateKey, chainId);
        System.out.println("signed: " + transaction.getSignatures());
        PackedTransaction packedTransaction = new PackedTransaction(transaction);
        JsonNode pushTxnResponse = eosApiRestClient.pushTransaction(packedTransaction);
        System.out.println(Utils.toJson(pushTxnResponse));
    }

    public void execNoFeeTransfer() {
        EosPrivateKey eosPrivateKey = new EosPrivateKey("5KGfxrfAkxWt5o34Ev5p6C72Yy2jpNkWkYYUYuRDu1zjkoApV7K");
        String from = "user1";
        String to = "user2";
        double amount = 1;
        String memo = "";
        String contract = "eosio.token";
        String action = "transfer";
        double feeVal = 0.01;
        String fee = Utils.getEosBalance(feeVal);
        Map<String, String> args = new HashMap<>(4);
        args.put("from", from);
        args.put("to", to);
        args.put("quantity", Utils.getEosBalance(amount));
        args.put("memo", memo);
        EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance("http://192.168.1.8:8888").newRestClient();
        AbiJsonToBin data = eosApiRestClient.abiJsonToBin(contract, action, args);
        ChainInfo chainInfo = eosApiRestClient.getChainInfo();
        System.out.println("chainInfo: " + Utils.toJson(chainInfo));
        System.out.println("chainId: " + chainInfo.getChainId());
        TypeChainId chainId = new TypeChainId(chainInfo.getChainId());
        EosChainInfo eosChainInfo = new EosChainInfo();
        eosChainInfo.setHeadBlockTime(chainInfo.getHeadBlockTime());
        eosChainInfo.setHeadBlockId(chainInfo.getHeadBlockId());
        SignedTransaction transaction = createTransaction(contract, action, data.getBinargs(), getActivePermission(from), eosChainInfo, fee);
        transaction = signTransaction(transaction, eosPrivateKey, chainId);
        System.out.println("signed: " + transaction.getSignatures());
        PackedTransaction packedTransaction = packedTransaction(transaction);
        JsonNode pushTxnResponse = eosApiRestClient.pushTransaction(packedTransaction);
        System.out.println(Utils.toJson(packedTransaction));
    }

    private String[] getActivePermission(String accountName) {
        return new String[]{accountName + "@active"};
    }
}
