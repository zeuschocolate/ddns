package com.zeus.ddns;

import com.alibaba.fastjson.JSON;
import com.aliyun.alidns20150109.Client;
import com.aliyun.alidns20150109.models.*;
import com.aliyun.teaopenapi.models.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wx
 * @since 2022/8/24 10:10
 */
@Component
public class DDNSClient {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Value("${ali.domain}")
    private String domain;
    @Value("${ali.keyword}")
    private String keyWord;
    @Value("${ali.access-key}")
    private String accessKey;
    @Value("${ali.access-key-secret}")
    private String accessSecret;
    @Value("${ali.endpoint}")
    private String endpoint;

    private String currentDdnsIp="";

    private final Client client;

    public DDNSClient(@Value("${ali.access-key}") String accessKey,@Value("${ali.access-key-secret}") String accessSecret,@Value("${ali.endpoint}") String endpoint) throws Exception {
        this.client = new Client(new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKey)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessSecret)
                .setEndpoint(endpoint)
        );
    }

    //刷新DDNS解析记录
    public void refreshDDNS() throws Exception {
        String currentIp = this.getCurrentIp();
        // 获取解析记录
        DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord domainDnsRecord = this.getDomainDnsRecord();
        //当前解析记录与当前IP不一致的情况下,重新解析当前IP到指定域名上
        if (!this.currentDdnsIp.equals(currentIp)){
            // 刷新解析记录
            this.updateDomainDnsRecord(domainDnsRecord,currentIp);
        }
        logger.info("无需修改解析记录，当前IP：{}",currentIp);
    }

    //获取当前IP地址
    private String getCurrentIp(){
        return WebToolUtils.getPublicIP();
    }

    //获取当前域名解析记录
    public DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord getDomainDnsRecord() throws Exception {
        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest()
                .setDomainName(domain);
        // 获取记录详情
        DescribeDomainRecordsResponse domainRecordsResponse = client.describeDomainRecords(describeDomainRecordsRequest);
        List<DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord> record = domainRecordsResponse.getBody().getDomainRecords().getRecord();
        DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord describeDomainRecordsResponseBodyDomainRecordsRecord = record.get(0);
        //给当前dns的IP赋值
        currentDdnsIp = describeDomainRecordsResponseBodyDomainRecordsRecord.getValue();
        logger.info("获取解析记录：{}", JSON.toJSONString(describeDomainRecordsResponseBodyDomainRecordsRecord));
        return describeDomainRecordsResponseBodyDomainRecordsRecord;
    }

    //更新新的解析记录
    public void updateDomainDnsRecord(DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord domainDnsRecord,String newIp) throws Exception {
        // 修改记录
        UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest()
                .setRecordId(domainDnsRecord.getRecordId())
                .setRR(domainDnsRecord.getRR())
                .setType(domainDnsRecord.getType())
                .setValue(newIp);
        // 修改记录
        UpdateDomainRecordResponse updateDomainRecordResponse = client.updateDomainRecord(updateDomainRecordRequest);
        logger.info("修改解析结果：{}",JSON.toJSONString(updateDomainRecordResponse));
    }
}

