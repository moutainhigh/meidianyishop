package com.meidianyi.shop.service.pojo.shop.market.integration;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationDefineEnums.QueryType;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationDefineEnums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月5日
 * 瓜分积分活动分页查视图
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupIntegrationDefineVo {

	/** 活动ID*/
	private Integer id;
	 /** 活动名称 */
	private String name;
	 /** 总抽奖积分 */
	private Integer inteTotal ;
	 /** 每一个团的总积分 */
	private Integer inteGroup ;
	/** 成团人数*/
	private Short limitAmount ;
	 /** 参团限制 */
	private Short joinLimit ;
	/** 瓜分方式 0：按邀请好友数量瓜分，1：好友均分，2：随机瓜分 */
	private Byte divideType;
	/** 开始时间*/
	private Timestamp startTime;
	 /** 结束时间 */
	private Timestamp endTime;
	/** 是否过期1进行中，2未开始，3已过期，4已停用 */
	private Byte expire;
	 /** 状态： 1：启用  0： 禁用 */
	private Byte status ;
	/** 剩余积分 */
	private Integer inteRemain;
	 /**  继续： 1：继续  0： 结束 */
	private Byte isContinue;
	 /** 活动宣传语 */
	private String advertise;
	/** 消耗积分 */
	private Integer useIntegration=0;
	/**参与人数 */
	private Integer inteUserSum=0;
	/** 	团数量 */
	private Integer inteGroupSum=0;
	
	
	public Byte isExpired() {
		if(status!=null&&status.equals(Status.STOPPED.value())) {
			expire = QueryType.STOPPED;
			return expire;
		}
		Timestamp now = Timestamp.valueOf(LocalDateTime.now());
		if(now.compareTo(startTime)<0) {
			expire = QueryType.UNSTARTED;
			return expire;
		}
		if(now.compareTo(endTime)>0) {
			expire =QueryType.OVERDUE;
			return expire;
		}
		expire = QueryType.UNDER_WAY;
		return expire;
	}
	
}

