package com.meidianyi.shop.service.shop.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.card.SearchCardParam;
import com.meidianyi.shop.service.pojo.wxapp.card.CardUpgradeVo;
import com.meidianyi.shop.service.shop.member.dao.CardUpgradeDao;

/**
* @author 黄壮壮
* @Date: 2019年10月31日
* @Description:  会员卡升级过程
*/
@Service
public class CardUpgradeService extends ShopBaseService{
	@Autowired
	private CardUpgradeDao dao;
	public void recordCardUpdateGrade(Integer userId,MemberCardRecord oldCard, MemberCardRecord newCard,String option) {
		logger().info("保存会员卡升级信息");
		dao.insert(userId,oldCard,newCard,option);
	}
	
	
	
	/**
	 * 获取用户升降级记录
	 * @return 
	 */
	public PageResult<CardUpgradeVo> getGradeList(SearchCardParam param) {
		logger().info("查询会员卡升级信息");
		return dao.getGradeList(param);
	}

}
