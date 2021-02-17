package com.meidianyi.shop.service.pojo.shop.member.builder;

import java.sql.Timestamp;

import com.meidianyi.shop.service.pojo.shop.member.score.UserScoreVo;

/**
* @author 黄壮壮
* @Date: 2019年11月4日
* @Description:
*/

public class UserScoreVoBuilder {
	private UserScoreVo userScoreVo;
	
	private UserScoreVoBuilder(){
		userScoreVo = new UserScoreVo();
	}
	private UserScoreVoBuilder(UserScoreVo userScoreVo) {
		this.userScoreVo = userScoreVo;
	}
	
	
	public static UserScoreVoBuilder create() {
		return new UserScoreVoBuilder();
	}
	
	
	public static UserScoreVoBuilder create(UserScoreVo userScoreVo) {
		return new UserScoreVoBuilder(userScoreVo);
	}

	public UserScoreVoBuilder id (Integer id) {
		userScoreVo.setId(id);
		return this;
	}

	public UserScoreVoBuilder userId (Integer userId) {
		userScoreVo.setUserId(userId);
		return this;
	}

	public UserScoreVoBuilder score (Integer score) {
		userScoreVo.setScore(score);
		return this;
	}

	public UserScoreVoBuilder status (Byte status) {
		userScoreVo.setStatus(status);
		return this;
	}

	public UserScoreVoBuilder flowNo (String flowNo) {
		userScoreVo.setFlowNo(flowNo);
		return this;
	}

	public UserScoreVoBuilder usableScore (Integer usableScore) {
		userScoreVo.setUsableScore(usableScore);
		return this;
	}

	public UserScoreVoBuilder identityId (String identityId) {
		userScoreVo.setIdentityId(identityId);
		return this;
	}

	public UserScoreVoBuilder goodsId (Integer goodsId) {
		userScoreVo.setGoodsId(goodsId);
		return this;
	}

	public UserScoreVoBuilder orderSn (String orderSn) {
		userScoreVo.setOrderSn(orderSn);
		return this;
	}

	public UserScoreVoBuilder shopId (Integer shopId) {
		userScoreVo.setShopId(shopId);
		return this;
	}

	public UserScoreVoBuilder desc (String desc) {
		userScoreVo.setDesc(desc);
		return this;
	}

	public UserScoreVoBuilder remarkCode (Integer remarkCode) {
		userScoreVo.setRemarkCode(remarkCode);
		return this;
	}
	
	public UserScoreVoBuilder remarkData(String remarkData) {
		userScoreVo.setRemarkData(remarkData);
		return this;
	}

	public UserScoreVoBuilder createTime (Timestamp createTime) {
		userScoreVo.setCreateTime(createTime);
		return this;
	}

	public UserScoreVoBuilder expireTime (Timestamp expireTime) {
		userScoreVo.setExpireTime(expireTime);
		return this;
	}

	public UserScoreVoBuilder adminUser (String adminUser) {
		userScoreVo.setAdminUser(adminUser);
		return this;
	}

	public UserScoreVoBuilder isFromCrm (Boolean isFromCrm) {
		userScoreVo.setIsFromCrm(isFromCrm);
		return this;
	}

	public UserScoreVoBuilder isFromOverdue (Boolean isFromOverdue) {
		userScoreVo.setIsFromOverdue(isFromOverdue);
		return this;
	}

	public UserScoreVoBuilder isFromRefund(Boolean isFromRefund) {
		userScoreVo.setIsFromRefund(isFromRefund);
		return this;
	}

	public UserScoreVoBuilder scoreDis (Integer scoreDis) {
		userScoreVo.setScoreDis(scoreDis);
		return this;
	}

	public UserScoreVo build() {
		return userScoreVo;
	}
}
