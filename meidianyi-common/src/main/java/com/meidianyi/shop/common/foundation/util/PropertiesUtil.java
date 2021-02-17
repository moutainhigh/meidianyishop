package com.meidianyi.shop.common.foundation.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Properties 工具
 *
 * @author 郑保乐
 */
@Slf4j
public class PropertiesUtil {

    /**
     * 将 properties 文件转换成 map
     *
     * @param filePath properties 文件的相对路径（ClassPath）
     */
    public static Map<String, String> toMap(String filePath) {
        Properties properties = new Properties();
        ClassPathResource resource = new ClassPathResource(filePath);
        try {
            InputStream inputStream = resource.getInputStream();
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> map = new HashMap<>(100);
        properties.forEach((k, v) -> map.put((String) k, (String) v));
        return map;
    }
}
