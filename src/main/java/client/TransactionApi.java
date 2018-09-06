package client;

/**
 * @author lxg
 * @create 2018-06-07 11:10
 * @desc 交易
 */
public interface TransactionApi {
    String transfer(String privacyKey, String from, String to, double amount, String contract, double fee);

    String vote(String privacyKey, String voter, String bpname, double change, String contract, double fee);

    String pushBlock(String from, String to, double amount);

    String blocktwitter(String privacyKey, String from, String message, String contract, double fee);

    String packedTransactionList(String privacyKey, String message, String activer, int count, boolean isSuffix);
}
