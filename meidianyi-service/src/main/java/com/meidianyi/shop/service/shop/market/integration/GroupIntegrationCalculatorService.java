package com.meidianyi.shop.service.shop.market.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.db.shop.tables.records.GroupIntegrationDefineRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationDefineEnums.DivideTypeEnum;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListEnums.IsGrouper;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListEnums.IsNew;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupIntegrationListParticipationVo;

/**
 * @author huangronggang
 * @date 2019年8月6日
 */
@Service
public class GroupIntegrationCalculatorService extends ShopBaseService{
	
	/**
	 * 专业计算用户参加某一瓜分积分活动获得多少积分。
	 * @param defineRecord 参加的活动
	 * @param participationList	参加该活动的某一个团 成员详情
	 * @return
	 */
	public int handle(GroupIntegrationDefineRecord defineRecord,List<GroupIntegrationListParticipationVo> participationList) {
		Byte divideType = defineRecord.getDivideType();
		if(DivideTypeEnum.NUM_INVITED_FRIENDS.vlaue().equals(divideType)) {
			return handInvitedFriendsType(defineRecord,participationList);
		}
		if(DivideTypeEnum.FRIENDS_SHARE.vlaue().equals(divideType)) {
			return handFriendShareType(defineRecord,participationList);
		}
		return handRandomPartitionType(defineRecord,participationList);
	}
	/**
	 * 随机分配奖池
	 * @param defineRecord
	 * @param participationList
	 * @return
	 */
	private int handRandomPartitionType(GroupIntegrationDefineRecord defineRecord,
			List<GroupIntegrationListParticipationVo> participationList) {
		Integer canIntegration = participationList.get(0).getCanIntegration();
		List<Integer> range = range(1, canIntegration -1);
		Collections.shuffle(range);
		List<Integer> scorePool = range.subList(0, participationList.size()-1);
		Collections.sort(scorePool);
		for (int i = 0; i < participationList.size(); i++) {
			int integration = 0;
			GroupIntegrationListParticipationVo vo = participationList.get(i);
			if(i == 0) {
				integration = scorePool.get(i);
			}else if(i == participationList.size()-1){
				integration = canIntegration - scorePool.get(i-1);
			}else {
				integration = scorePool.get(i)-scorePool.get(i-1);
			}
			saas.getShopApp(getShopId()).groupIntegration.groupIntegrationList.updateIntegration(vo.getId(), integration);
		}
		return 0;
	}
	/**
	 * 平均分配奖池
	 * @param defineRecord
	 * @param participationList
	 * @return
	 */
	private int handFriendShareType(GroupIntegrationDefineRecord defineRecord,
			List<GroupIntegrationListParticipationVo> participationList) {
		GroupIntegrationListParticipationVo vo = participationList.get(0);
		int groupId = vo.getGroupId();
		int actId = defineRecord.getId();
		int canIntegration = vo.getCanIntegration();
		int partcipationNum = participationList.size();
		int avgScore = canIntegration/partcipationNum;
		int grouperScore =  canIntegration - avgScore * (partcipationNum-1);
		saas.getShopApp(getShopId()).groupIntegration.groupIntegrationList.batchUpdateIntegeration(actId, groupId, avgScore);
		saas.getShopApp(getShopId()).groupIntegration.groupIntegrationList.updateGroupperIntegration(actId, groupId, grouperScore);
		return 0;
	}
	/**
	 * 按邀请人数分配奖池
	 * @param defineRecord
	 * @param participationList
	 * @return
	 */
	private int handInvitedFriendsType(GroupIntegrationDefineRecord defineRecord,
			List<GroupIntegrationListParticipationVo> participationList) {
		Integer canIntegration = participationList.get(0).getCanIntegration();
		Integer groupId = participationList.get(0).getGroupId();
		//记录总权重
		int totalWeight =0;
		//记录每个人的权重
		Map<GroupIntegrationListParticipationVo,Integer> weightMap = new HashMap<GroupIntegrationListParticipationVo, Integer>(participationList.size());
		//算出每个人的权重
		for (GroupIntegrationListParticipationVo vo : participationList) {
			Integer userId = vo.getUserId();
			int inviteNum = saas.getShopApp(getShopId()).groupIntegration.groupIntegrationList.getInviteNum(groupId, userId,defineRecord.getId());
			int inviteNewNum = saas.getShopApp(getShopId()).groupIntegration.groupIntegrationList.getInviteNewNum(groupId, userId,defineRecord.getId());
			int selfNum = vo.getIsNew().equals(IsNew.YES.value())?2:1;
			int weight = inviteNewNum+ inviteNum + selfNum;
			weightMap.put(vo, weight);
			totalWeight += weight;
		}
//		每个权重所能获得的积分
		int weightScore = canIntegration/totalWeight;
//		给每个人分积分 ，并保存到数据库
		for (GroupIntegrationListParticipationVo participation : participationList) {
			if(participation.getIsGrouper().equals(IsGrouper.FALSE.value())) {
				int integration = weightScore * weightMap.get(participation);
				canIntegration-=integration;
				saas.getShopApp(getShopId()).groupIntegration.groupIntegrationList.updateIntegration(participation.getId(), integration);
			}
		}
//		给组长分积分
		saas.getShopApp(getShopId()).groupIntegration.groupIntegrationList.updateGroupperIntegration(defineRecord.getId(), groupId, canIntegration);
		return 0;
	}
	/**
	 * 用于生成 start 开始 到 end 结束的列表
	 * @param start
	 * @param end
	 * @return
	 */
	private List<Integer> range(int start ,int end){
		if(end-start <= 0) {
			return null;
		}
		List<Integer> list = new ArrayList<Integer>();
		for (int i = start; i <=end; i++) {
			list.add(i);
		}
		return list;
	}
}

