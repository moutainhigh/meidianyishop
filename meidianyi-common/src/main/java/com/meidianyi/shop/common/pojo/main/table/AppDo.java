package com.meidianyi.shop.common.pojo.main.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 李晓冰
 * @date 2020年07月15日
 */
@Data
public class AppDo {
    String appId;
    String appName;
    String appSecret;
    Timestamp createTime;
}
