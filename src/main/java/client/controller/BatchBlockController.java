package client.controller;

import client.TransactionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxg
 * @create 2018-06-08 16:04
 * @desc 压测块
 */
@RequestMapping("/block")
@RestController
public class BatchBlockController {

    @Autowired
    private TransactionApi transactionApi;

    @RequestMapping("/push")
    public void pushBlock() {
        transactionApi.pushBlock(null, null, 0);
    }
}
