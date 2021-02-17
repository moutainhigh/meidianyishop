package com.meidianyi.shop.service.pojo.shop.operation;

import java.sql.Timestamp;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

import lombok.Getter;
import lombok.Setter;

/**
 * RecordAdminAction入参
 * @author: 卢光耀
 * @date: 2019-07-16 14:19
 *
*/
@Getter
@Setter
public class RecordAdminActionParam extends BasePageParam {
    private String    userName;
    private Byte      actionType;
    private Timestamp startCreateTime;
    private Timestamp endCreateTime;

}
