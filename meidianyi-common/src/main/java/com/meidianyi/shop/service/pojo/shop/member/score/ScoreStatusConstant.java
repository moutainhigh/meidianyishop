package com.meidianyi.shop.service.pojo.shop.member.score;
/**
* @author 黄壮壮
* @Date: 2019年8月23日
* @Description: 积分记录条目的状态
*/
public class ScoreStatusConstant {
	/** -未使用  如新添加进去的可用积分  */
	public final static Byte NO_USE_SCORE_STATUS = 0;
	/** -已使用  如 消耗积分 */
	public final static Byte USED_SCORE_STATUS = 1;
	/** -已过期    */
	public final static Byte EXPIRE_SCORE_STATUS = 2;
	/** -已退款 通过退款来增加可用积分 */
	public final static Byte REFUND_SCORE_STATUS = 3;
}
