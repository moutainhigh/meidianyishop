package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * 分销员审核操作入参类
 * @Author 常乐
 * @Date 2019-12-10
 */
@Data
public class DistributionApplyOptParam {
    /**
     * 申请记录ID
     */
    private Integer id;

    /**
     * 分组id
     */
    private Integer groupId;

    /**
     * 审核内容
     */
    private String msg;
    /**
     * 邀请码
     */
    private String invitationCode;
}
