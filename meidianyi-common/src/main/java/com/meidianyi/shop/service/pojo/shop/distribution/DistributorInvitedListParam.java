package com.meidianyi.shop.service.pojo.shop.distribution;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * 分销员已邀请用户列表
 * @author 常乐
 * 2019年8月7日
 */
@Data
public class DistributorInvitedListParam {
    /**用户ID*/
	private Integer userId;
	/**手机号*/
	private String mobile;
	/**微信昵称*/
	private String username;
	/**真实姓名*/
	private String realName;
	/**注册开始时间*/
	private Timestamp startCreateTime;
	/**注册结束时间*/
	private Timestamp endCreateTime;
	/**邀请开始时间*/
	private Timestamp startInviteTime;
	/**邀请结束时间*/
	private Timestamp endInviteTime;
	/**0:直接邀请；1：间接邀请*/
	private Byte inviteType = 0;

	private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
	private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
}
