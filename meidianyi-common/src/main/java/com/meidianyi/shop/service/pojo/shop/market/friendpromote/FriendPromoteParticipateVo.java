package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;
/**
  *  好友助力参与明细
 * @author liangchen
 * @date 2019年8月12日
 */
@Data
@ExcelSheet
public class FriendPromoteParticipateVo {
    /** 参与用户id */
    @ExcelIgnore
    private Integer joinUserId;
	/** 参与用户昵称 */
    @ExcelColumn(columnIndex = 0, columnName = JsonResultMessage.FRIEND_PROMOTE_JOIN_USER_NAME)
	private String username;
	/** 参与用户手机号 */
    @ExcelColumn(columnIndex = 1, columnName = JsonResultMessage.FRIEND_PROMOTE_JOIN_USER_MOBILE)
	private String mobile;
	/** 是否是新用户 promote:是  else：否 */
	@ExcelIgnore
	private String inviteSource;
    /** 是否是新用户 */
    @ExcelColumn(columnIndex = 2, columnName = JsonResultMessage.FRIEND_PROMOTE_IS_NEW)
    private String isNew;
	/** 助力活动Id（用户发起） */
    @ExcelColumn(columnIndex = 3, columnName = JsonResultMessage.FRIEND_PROMOTE_LAUNCH_ACT_ID)
	private Integer launchId;
    /** 发起用户id */
    @ExcelIgnore
    private Integer launchUserId;
	/** 活动发起人 */
    @ExcelColumn(columnIndex = 4, columnName = JsonResultMessage.FRIEND_PROMOTE_LAUNCH_USER_NAME)
	private String launchUsername;
	/** 助力次数 */
    @ExcelColumn(columnIndex = 5, columnName = JsonResultMessage.FRIEND_PROMOTE_PROMOTE_NUM)
	private Integer promoteTimes;
	/** 助力值 */
    @ExcelColumn(columnIndex = 6, columnName = JsonResultMessage.FRIEND_PROMOTE_PROMOTE_VALUE)
	private Integer promoteValue;
}
