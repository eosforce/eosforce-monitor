package client.domain;

import java.math.BigDecimal;

/**
 * @author lxg
 * @create 2018-07-06 17:55
 * @desc
 */
public class NewAccount {
    private long num;
    private BigDecimal totalBalance = new BigDecimal("0");

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }
}
