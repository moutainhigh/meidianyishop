package com.meidianyi.shop.service.pojo.shop.market.message;

import lombok.Data;

import java.sql.Timestamp;
/**
 * send record Vo
 * @author 卢光耀
 * @date 2019-09-09 15:09
 *
*/
@Data
public class MessageOutputVo {


    private String username;

    private Byte templatePlatform;

    private Byte sendStatus;

    private Byte isVisit;

    private Timestamp visitTime;

    private Timestamp createTime;
}
