package client.controller;

import client.MonitorApi;
import client.TransactionApi;
import client.job.BlockAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lxg
 * @create 2018-06-22 16:31
 * @desc
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController {
    @Autowired
    private MonitorApi monitorApi;

    @RequestMapping("/chainDetail")
    public BlockAccount accountsDetail(){
        return monitorApi.getBlockAccount();
    }
}
