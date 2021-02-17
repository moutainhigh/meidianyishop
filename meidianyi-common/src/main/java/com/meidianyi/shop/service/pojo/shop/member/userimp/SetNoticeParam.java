package com.meidianyi.shop.service.pojo.shop.member.userimp;

import lombok.Data;

/**
 * 设置用户导入通知 入参
 * @author zhaojianqiang
 * @time   下午2:06:11
 */
@Data
public class SetNoticeParam {
	/**通知说明 */
	private String explain;
	/**积分 */
	private String score;
	/** 优惠券Id */
	private int[] couponIds;
}
