package com.mavis.config;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.mavis.entity.PaymentChannelConfig;
import com.mavis.mapper.PaymentChannelConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlipayClientHolder {

    @Autowired
    private PaymentChannelConfigMapper channelConfigMapper;

    private volatile AlipayClient client;
    private volatile JSONObject configJson;

    public AlipayClient getClient() {
        if (client == null) {
            refresh();
        }
        return client;
    }

    public JSONObject getConfig() {
        if (configJson == null) {
            refresh();
        }
        return configJson;
    }

    public synchronized void refresh() {
        PaymentChannelConfig config = channelConfigMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PaymentChannelConfig>()
                        .eq(PaymentChannelConfig::getChannelCode, "ALIPAY")
                        .eq(PaymentChannelConfig::getStatus, 1)
                        .eq(PaymentChannelConfig::getIsDefault, 1)
                        .last("LIMIT 1")
        );
        if (config == null || config.getStatus() != 1) {
            log.warn("支付宝通道未配置或已禁用");
            return;
        }

        JSONObject json = JSONObject.parseObject(config.getConfigData());
        this.configJson = json;
        this.client = new DefaultAlipayClient(
                json.getString("gatewayUrl"),
                json.getString("appId"),
                json.getString("privateKey"),
                json.getString("format"),
                json.getString("charset"),
                json.getString("publicKey"),
                json.getString("signType")
        );
        log.info("支付宝客户端已刷新: appId={}", json.getString("appId"));
    }
}
