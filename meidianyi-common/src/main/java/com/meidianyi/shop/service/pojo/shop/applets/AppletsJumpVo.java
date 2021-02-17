package com.meidianyi.shop.service.pojo.shop.applets;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2019/7/15 14:01
 */
@Data
@NoArgsConstructor
public class AppletsJumpVo {

    private String appName;
    private String appId;
    private Timestamp createTime;
    private Integer  usable;
    private Integer flag;

}
