package com.meidianyi.shop.service.pojo.shop.operation;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 
 * @author: 卢光耀
 * @date: 2019-07-16 14:44
 *
*/
@Builder
@Data
public class RecordAdminActionInfo {
    private String userName;

    private String actionTypeName;

    private Timestamp createTime;

    private String content;
    
    private Byte accountType;
    
    private Byte actionType;
}
