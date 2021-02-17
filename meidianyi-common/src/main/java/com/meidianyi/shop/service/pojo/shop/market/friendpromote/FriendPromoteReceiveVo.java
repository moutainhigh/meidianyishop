package com.meidianyi.shop.service.pojo.shop.market.friendpromote;
import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;
/**
 * 好友助力领取明细
 * @author liangchen
 * @date 2019年8月8日
 */
@Data
@ExcelSheet
public class FriendPromoteReceiveVo {
	
	/** 领取用户昵称 */
    @ExcelColumn(columnIndex = 0, columnName = JsonResultMessage.FRIEND_PROMOTE_RECEIVE_USER_NAME)
	private String username;
	/** 领取用户手机号 */
    @ExcelColumn(columnIndex = 1, columnName = JsonResultMessage.FRIEND_PROMOTE_RECEIVE_USER_MOBILE)
	private String mobile;
	/** 助力活动Id（用户发起） */
    @ExcelColumn(columnIndex = 2, columnName = JsonResultMessage.FRIEND_PROMOTE_LAUNCH_ACT_ID)
	private Integer id;
	/** 是否已领取 */
    @ExcelIgnore
	private Integer promoteStatus;
    /** 是否已领取 */
    @ExcelColumn(columnIndex = 3, columnName = JsonResultMessage.FRIEND_PROMOTE_IS_RECEIVE)
    private String isReceive;
	/** 领取时间 */
    @ExcelColumn(columnIndex = 4, columnName = JsonResultMessage.FRIEND_PROMOTE_RECEIVE_TIME)
	private Timestamp recTime;
	/** 订单号 */
    @ExcelColumn(columnIndex = 5, columnName = JsonResultMessage.FRIEND_PROMOTE_ORDER_SN)
	private String orderSn;
}
