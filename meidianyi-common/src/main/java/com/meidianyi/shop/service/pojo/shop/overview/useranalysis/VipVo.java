package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

/**
 * 会员统计
 * @author liangchen
 * @date 2019年7月18日
 */
@Data
public class VipVo {
    /** 累计会员数 */
	private Integer userData;
    /** 新增会员数 */
	private Integer regUserData;
    /** 升级会员数 */
	private Integer upgradeUserData;
    /** 储值会员数 */
	private Integer chargeUserData;
    /** 累计会员数变化率 */
	private Double userDataRate;
    /** 新增会员数变化率 */
	private Double regUserDataRate;
    /** 升级会员数变化率 */
	private Double upgradeUserDataRate;
    /** 储值会员数变化率 */
	private Double chargeUserDataRate;
    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;
}
