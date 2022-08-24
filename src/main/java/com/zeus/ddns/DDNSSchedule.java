package com.zeus.ddns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wx
 * @since 2022/8/25 10:28
 */
@Component
@EnableScheduling
public class DDNSSchedule {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    private final DDNSClient ddnsClient;

    public DDNSSchedule(DDNSClient ddnsClient) {
        this.ddnsClient = ddnsClient;
    }

    /**
     * 刷新阿里巴巴DNS解析记录 每半小时检测刷新一次
     */
    @Scheduled(fixedRate=1800000)
    public void refreshDDNS() throws Exception {
        ddnsClient.refreshDDNS();
    }
}



