package com.meidianyi.shop.service.pojo.shop.member.score;

import java.sql.Timestamp;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;

import lombok.Data;

/**
 * @author huangzhuangzhuang
 */
@Data
public class ScoreSetParam {
	/** 备注Id  {@link com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate}*/
	private Integer remarkCode;
	
	/** 备注数据 */
	@JsonAlias("remark")
	private String remarkData;
	/** -备注 */
	//private String remark;
	/** -描述 */
	private String desc;
	/** 关联其他属性 */
	private String identityId;
	/** 商品Id */
	private Integer goodsId;
	/** -需要更新的用户id */
	@NotNull(message=JsonResultMessage.MSG_MEMBER_SCORE_NOT_NULL)
	private Integer[] userId;
	
	/** -积分变动数额 */
	@NotNull(message=JsonResultMessage.MSG_MEMBER_SCORE_NOT_NULL)
	private Integer score;
	
	/** -当前积分 -当批量操作时，为所选用户中的最低积分*/
	@Min(message=JsonResultMessage.MSG_MEMBER_SCORE_NOT_BE_NEGATIVE, value = 0)
	@NotNull(message=JsonResultMessage.MSG_MEMBER_SCORE_NOT_NULL)
	private Integer scoreDis;
	
	/** -订单编号 */
	private String orderSn; 
	
	/** -积分状态 {@link com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant} */
	private Byte scoreStatus;
	
	/** -是否退款 }*/
	private Byte isFromRefund;
	
	/** 过期时间 */
	private Timestamp expiredTime;
	
}
