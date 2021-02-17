package com.meidianyi.shop.common.pojo.shop.table;

import com.meidianyi.shop.common.foundation.data.ImSessionConstant;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 问诊会话
 * @author 李晓冰
 * @date 2020年07月21日
 */
@Data
public class ImSessionDo {
    private Integer   id;
    private Integer departmentId;
    private Integer   doctorId;
    private Integer   userId;
    private Integer   patientId;
    private Byte      sessionStatus;
    private Integer continueSessionCount;
    private Byte weightFactor;
    private Byte evaluateStatus;
    private String    orderSn;
    private Timestamp receiveStartTime;
    private Timestamp limitTime;
    private Integer readyToOnAkcTime;
    private Byte      isDelete;
    private Timestamp createTime;
    private Timestamp updateTime;

    public void calculateReadyToOnAckTime(){
        if (!ImSessionConstant.SESSION_READY_TO_START.equals(sessionStatus)) {
            return;
        }
        long now = System.currentTimeMillis();
        long readyTime = createTime.getTime();
        readyToOnAkcTime = Math.toIntExact((now - readyTime) / 1000);
    }
}
