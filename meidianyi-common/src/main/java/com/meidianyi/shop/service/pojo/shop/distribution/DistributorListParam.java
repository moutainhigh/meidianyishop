package com.meidianyi.shop.service.pojo.shop.distribution;

import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * 分销员列表入参
 * @author 常乐
 * 2019年8月5日
 */
@Data
public class DistributorListParam {
    /**
     * 分销员Id
     */
    private Integer distributorId;
    /**
     *分销员手机号
     */
	private String mobile;
    /**
     * 分销员微信昵称
     */
	private String username;
    /**
     * 真实姓名
     */
	private String realName;
    /**
     * 邀请人
     */
	private String inviteName;
    /**
     * 邀请码
     */
	private String invitationCode;
    /**
     * 被邀请人手机号
     */
	private String invitedMobile;
    /**
     * 被邀请人姓名
     */
	private String invitedUserName;
    /**
     * 注册开始时间
     */
	private Timestamp startCreateTime;
    /**
     * 注册结束时间
     */
	private Timestamp endCreateTime;
    /**
     * 分销等级
     */
	private Byte distributorLevel;
    /**
     * 分销分组
     */
	private Integer distributorGroup;
    /**
     * 有下级用户 0：无；1：有；
     */
	private Byte haveNextUser;
    /**
     * 有手机号 0：无；1：有；
     */
	private Byte haveMobile;
    /**
     * 有真实姓名 0：无；1：有；
     */
	private Byte haveRealName;

    /**
     * 当前操作分组ID
     */
	private Integer optGroupId;
    /**
     * 根据表头排序字段：1；下级用户数；2：间接邀请用户数；3：累计返利商品总额；4：累计获得佣金总额；5：待返利佣金总额：
     */
	private Byte sortField = 1;
    /**
     * 排序方式：asc:升序；desc：降序
     */
	private String sortWay = "desc";
    /**
     * 导出起始值
     */
    private Integer startNum;
    /**
     * 导出结束值
     */
    private Integer endNum;

	private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
	private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
