package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;
/**
  *  好友助力发起明细
 * @author liangchen
 * @date 2019年8月12日
 */
@Data
@ExcelSheet
public class FriendPromoteLaunchVo {
    /** 发起用户id */
    @ExcelIgnore
    private Integer userId;
	/** 发起用户昵称 */
    @ExcelColumn(columnIndex = 0, columnName = JsonResultMessage.FRIEND_PROMOTE_LAUNCH_USER_NAME)
	private String username;
	/** 发起用户手机号 */
    @ExcelColumn(columnIndex = 1, columnName = JsonResultMessage.FRIEND_PROMOTE_LAUNCH_USER_MOBILE)
	private String mobile;
	/** 助力活动Id（用户发起） */
    @ExcelColumn(columnIndex = 2, columnName = JsonResultMessage.FRIEND_PROMOTE_LAUNCH_ACT_ID)
	private Integer id;
	/** 参与人数 */
    @ExcelColumn(columnIndex = 3, columnName = JsonResultMessage.FRIEND_PROMOTE_JOIN_NUM)
	private Integer joinNum;
	/** 助力次数 */
    @ExcelColumn(columnIndex = 4, columnName = JsonResultMessage.FRIEND_PROMOTE_PROMOTE_NUM)
	private Integer promoteTimes;
	/** 助力值 */
    @ExcelColumn(columnIndex = 5, columnName = JsonResultMessage.FRIEND_PROMOTE_PROMOTE_VALUE)
	private Integer promoteValue;
	/** 是否助力成功 0:未成功 1、2:成功 */
    @ExcelIgnore
	private Integer promoteStatus;
    /** 是否助力成功 0:未成功 1、2:成功 */
    @ExcelColumn(columnIndex = 6, columnName = JsonResultMessage.FRIEND_PROMOTE_IS_SUCCESS)
    private String isSuccess;
}
