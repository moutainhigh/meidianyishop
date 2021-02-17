package com.meidianyi.shop.service.pojo.shop.market.packagesale;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.db.shop.tables.records.PackageSaleRecord;
import com.meidianyi.shop.service.pojo.shop.market.packagesale.PackSaleConstant.ActivityStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author huangronggang
 * @date 2019年8月12日
 * 打包一口价活动 分页查询视图
 */
@Data
@NoArgsConstructor
public class PackSalePageVo {
	/** 活动ID */
	private Integer id;
	/**活动类型**/
	private Byte packageType;
	/** 活动名称 */
	private String packageName;
	/** 活动开始时间 */
	private Timestamp startTime;
	/** 活动结束时间 */
	private Timestamp endTime;
	/** 结算总价格 */
	private BigDecimal totalMoney;
	/**折扣比例**/
	private BigDecimal totalRatio;
	/** 分组1 是否启用 */
	private Byte goodsGroup1;
	/** 分组1 分组名称*/
	private String groupName1;
	/** 分组1 分组商品数 */
	private Integer goodsNumber1;
	
	/** 分组2 是否启用 */
	private Byte goodsGroup2;
	/** 分组2 分组名称*/
	private String groupName2;
	/** 分组2 分组商品数 */
	private Integer goodsNumber2;
	
	/** 分组3 是否启用 */
	private Byte goodsGroup3;
	/** 分组3 分组名称*/
	private String groupName3;
	/** 分组3 分组商品数 */
	private Integer goodsNumber3;
	
	/** 已购商品数量 */
	private Integer purchasedNum;
	/** 订单数量 */
	private Integer orderNum;
	/** 下单用户数量 */
	private Integer userNum;
	/** 活动状态 进行中:1,未开始:2，已过期:3,已停用:4 */
	private Byte activityStatus;
	
	/** 是否停用 */
	private Byte status;
	
	
	/**
	 * 返回本活动当前状态（未开始/正在进行中/过期/已停用）
	 * @return
	 */
	public Byte activityStatus() {
		if(BaseConstant.ACTIVITY_STATUS_NORMAL.equals(this.getStatus())) {
			Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
			if(currentTime.before(this.getStartTime())){
				return ActivityStatus.UNSTARTED;
			}
			if(currentTime.after(this.getEndTime())) {
				return ActivityStatus.OVERDUE;
			}
			return ActivityStatus.UNDER_WAY;
		}
		return ActivityStatus.STOPPED;
	}

	
	public static PackSalePageVo from(PackageSaleRecord record) {
		if(record == null) {
			return null;
		}
		PackSalePageVo vo = new PackSalePageVo();
		vo.setId(record.getId());
		vo.setPackageType(record.getPackageType());
		vo.setPackageName(record.getPackageName());
		vo.setStartTime(record.getStartTime());
		vo.setEndTime(record.getEndTime());
		vo.setTotalMoney(record.getTotalMoney());
		vo.setTotalRatio(record.getTotalRatio());
		vo.setGoodsGroup1(record.getGoodsGroup_1());
		vo.setGroupName1(record.getGroupName_1());
		vo.setGoodsNumber1(record.getGoodsNumber_1());
		vo.setGoodsGroup2(record.getGoodsGroup_2());
		vo.setGroupName2(record.getGroupName_2());
		vo.setGoodsNumber2(record.getGoodsNumber_2());
		vo.setGoodsGroup3(record.getGoodsGroup_3());
		vo.setGroupName3(record.getGroupName_3());
		vo.setGoodsNumber3(record.getGoodsNumber_3());
		vo.setStatus(record.getStatus());
		vo.setActivityStatus(vo.activityStatus());
		return vo;
	}
}

