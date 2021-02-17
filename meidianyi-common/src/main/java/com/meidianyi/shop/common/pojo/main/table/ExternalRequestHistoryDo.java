package com.meidianyi.shop.common.pojo.main.table;

import java.sql.Timestamp;

/**
 * 外部接口请求记录
 * @author 李晓冰
 * @date 2020年07月15日
 */
public class ExternalRequestHistoryDo {
    private Integer   id;
    private String    appId;
    private Integer   shopId;
    private String    serviceName;
    private String    requestParam;
    private Timestamp createTime;
}
