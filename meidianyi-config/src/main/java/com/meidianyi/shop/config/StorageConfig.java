package com.meidianyi.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author 孔德成
 * @date 2019/8/5 17:09
 */
@Configuration
public class StorageConfig {

    @Value(value = "${web.storage-path}")
    private String storagePath;


    /**
     * 本地存储目录
     *
     * @param relativePath
     * @return
     */
    public String storagePath(String relativePath) {
        return String.format("%s/%s", storagePath, relativePath);
    }
}
