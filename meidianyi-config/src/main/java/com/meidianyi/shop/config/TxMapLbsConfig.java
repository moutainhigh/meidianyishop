package com.meidianyi.shop.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯地址解析配置
 * @author 孔德成
 * @date 2019/11/15 18:06
 */
@Configuration
@Data
public class TxMapLbsConfig {

    @Value(value = "${qq.lbs.webService.key}")
    private String key;
}
