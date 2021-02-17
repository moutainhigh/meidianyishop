package com.meidianyi.shop.service.pojo.shop.distribution;

import java.sql.Timestamp;
import com.meidianyi.shop.service.pojo.shop.decoration.DistributorApplyParam;
import lombok.Data;

/**
 * 分销员审核列表出参类
 * @author 常乐
 * 2019年9月20日
 */
@Data
public class DistributorCheckListVo {
    private Integer id;
    /**
     * 用户ID
     */
    private Integer userId;
    private String msg;
    private Byte isAutoPass;
    /**
     * 审核校验
     */
    private String activationFields;
    /**
     * 审核字段
     */
    private String configFields;
    /**
     * 后台审核字段
     */
    private DistributorApplyParam.InfoField checkField;
    /**
     * 用户昵称
     */
    private String username;
    /**
     *手机号
     */
    private String mobile;
    /**
     * 审核状态 0：审核中；1：审核通过；2：审核拒绝
     */
    private Integer status;
    /**
     * 是否删除 0：为删除；1：删除
     */
    private Integer delFlag;
    private Integer inviteId;
    private String inviteName;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 更新时间
     */
    private Timestamp updateTime;

}
