package com.meidianyi.shop.service.shop.card.msg;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.CardExamineRecord;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.config.message.MessageTemplateConfigConstant;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.member.MemberBasicInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.ucard.ActivateCardParam;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;
/**
 * 	会员卡操作相关消息推送
 * @author 黄壮壮
 *
 */
@Service
public class CardMsgNoticeService extends ShopBaseService {
	@Autowired private UserCardService userCardSvc;

	/**
	 *  会员卡等级变动通知
	 * @param userId 用户Id
	 * @param oldCard 被替换的旧卡
	 * @param newCard 替换的新卡
	 * @param option 操作备注说明
	 */
	public void cardGradeChangeMsg(Integer userId, MemberCardRecord oldCard, MemberCardRecord newCard, String option) {
		String cardNo = userCardSvc.getCardNoByUserAndCardId(userId,newCard.getId());
		List<Integer> arrayList = Collections.<Integer>singletonList(userId);
		//	订阅消息
		String[][] maData=null;
		//	公众号消息
		String[][] mpData=null;
		if(newCard.getGrade().compareTo(oldCard.getGrade())>0) {
			//	升级数据
			maData = new String[][] {
				{newCard.getCardName()},
				{Util.getdate("yyyy-MM-dd HH:mm:ss")},
				{"升级成功"}
			};
			mpData = new String[][] {
				{"等级卡升级通知"},
				{newCard.getCardName()},
                {"通过"},
                {"升级成功"}
			};
		}else {
			//	降级数据
			maData = new String[][] {
				{newCard.getCardName()},
				{Util.getdate("yyyy-MM-dd HH:mm:ss")},
				{"降级成功"}
			};

			mpData = new String[][] {
				{"等级卡降级通知"},
				{newCard.getCardName()},
				{"通过"},
                {"降级成功"}
			};
		}

		MaSubscribeData data = MaSubscribeData.builder().data307(maData).build();
		RabbitMessageParam param2 = RabbitMessageParam.builder()
				.maTemplateData(
						MaTemplateData.builder().config(SubcribeTemplateCategory.USER_GRADE).data(data).build())
				.mpTemplateData(
						MpTemplateData.builder().config(MpTemplateConfig.MEMBER_LEVEL_UP).data(mpData).build())
				.page("pages/cardinfo/cardinfo?cardNo="+cardNo).shopId(getShopId())
				.userIdList(arrayList)
				.type(MessageTemplateConfigConstant.MEMBER_LEVEL_UP).build();
		saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
	}

	/**
	 * 	发卡通知
	 * @param cardNo 卡号
	 * @param memberCard 会员卡信息
	 * @param user	用户信息
	 * @param expireTime 过期时间
	 * @param cardTypeText 卡类型说明
	 */
	public void sendCardMsgNotice(String cardNo, MemberCardRecord memberCard, MemberBasicInfoVo user, String expireTime,
			String cardTypeText) {
		//	公众号消息
		String[][] mpData = new String[][] {
			{""},
			{memberCard.getCardName()},
			{cardTypeText},
			{user.getMobile()==null?"":user.getMobile()},
			{expireTime},
			{""}
		};
		List<Integer> arrayList = Collections.<Integer>singletonList(user.getUserId());
		RabbitMessageParam param2 = RabbitMessageParam.builder()
				.mpTemplateData(
						MpTemplateData.builder().config(MpTemplateConfig.GET_CARD).data(mpData).build())
				.page("pages/cardinfo/cardinfo?cardNo="+cardNo).shopId(getShopId())
				.userIdList(arrayList)
				.type(MessageTemplateConfigConstant.SUCCESS_MEMBER_CARD_GET).build();
		saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
	}
	
	/**
	 * 会员卡审核成功消息
	 * @param param
	 */
	public void sendAuditSuccessMsg(ActivateCardParam param) {
		List<Integer> arrayList = Collections.<Integer>singletonList(param.getUserId());
		// 公众号消息
		String[][] mpData = new String[][] {
			{"审核通过"},
			{Util.getdate("yyyy-MM-dd HH:mm:ss")},
			{Util.getdate("yyyy-MM-dd HH:mm:ss")},
			{"申请会员卡激活"}
		};
		
		RabbitMessageParam param2 = RabbitMessageParam.builder()
				.mpTemplateData(
						MpTemplateData.builder().config(MpTemplateConfig.AUDIT_SUCCESS).data(mpData).build())
				.page("pages/cardinfo/cardinfo?cardNo="+param.getCardNo()).shopId(getShopId())
				.userIdList(arrayList)
				.type(MessageTemplateConfigConstant.AUDIT_SUCCESS).build();
		saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
	}
	
	/**
	 * 会员卡审核失败消息
	 * @param re
	 */
	public void sendAuditSuccessMsg(CardExamineRecord re) {
		Integer userId = re.getUserId();
		String cardNo = re.getCardNo();
		List<Integer> arrayList = Collections.<Integer>singletonList(userId);
		// 订阅消息
		String[][] maData = new String[][] {
			{Util.getdate("yyyy-MM-dd HH:mm:ss")},
			{"审核不通过"},
			{"很遗憾，您提交的会员卡激活申请未通过审核"}
		};
		MaSubscribeData data = MaSubscribeData.builder().data307(maData).build();
		// 公众号消息
		String[][] mpData = new String[][] {
			{"审核未通过"},
			{Util.getdate("yyyy-MM-dd HH:mm:ss")},
			{re.getRefuseDesc()},
			{re.getCreateTime().toLocalDateTime().toString()},
			{"申请会员卡激活"}
		};
		RabbitMessageParam param2 = RabbitMessageParam.builder()
				.maTemplateData(
						MaTemplateData.builder().config(SubcribeTemplateCategory.AUDIT).data(data).build())
				.mpTemplateData(
						MpTemplateData.builder().config(MpTemplateConfig.AUDIT_FAIL).data(mpData).build())
				.page("pages/cardinfo/cardinfo?cardNo="+cardNo).shopId(getShopId())
				.userIdList(arrayList)
				.type(MessageTemplateConfigConstant.FAIL_REVIEW).build();
		saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
	}
}
