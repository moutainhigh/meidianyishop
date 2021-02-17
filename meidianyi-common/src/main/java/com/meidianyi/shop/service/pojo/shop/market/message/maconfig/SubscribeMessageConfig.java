package com.meidianyi.shop.service.pojo.shop.market.message.maconfig;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author zhaojianqiang
 *
 *         2019年12月4日 上午11:08:39
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SubscribeMessageConfig {
	/** 服装/鞋/箱包 307开始 **/
	draw_result_307(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.DRAW_RESULT, 307, 1116, "抽奖结果通知",
			"活动名称:{{thing1.DATA}}抽奖时间:{{date2.DATA}}抽奖结果:{{thing3.DATA}}", new int[] { 1, 2, 3 }),

	user_grade_307(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.USER_GRADE, 307, 861, "会员等级变更通知",
			"会员等级{{phrase1.DATA}}变更时间{{date2.DATA}}备注说明{{thing3.DATA}}", new int[] { 1, 2, 3 }),

	audit_307(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.AUDIT, 307, 818, "审核结果通知",
			"审核时间{{time3.DATA}}审核结果{{phrase2.DATA}}审核说明{{thing4.DATA}}", new int[] { 3, 2, 4 }),

	score_change_307(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.SCORE_CHANGE, 307, 310, "积分变更提醒",
			"变更数量{{character_string1.DATA}}积分余额{{character_string2.DATA}}变更原因{{thing3.DATA}}", new int[] { 1, 2, 3 }),

	order_deliver_307(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.ORDER_DELIVER, 307, 855, "订单发货通知",
			"商品名称:{{thing2.DATA}}订单号:{{character_string1.DATA}}快递类型:{{phrase3.DATA}}快递单号:{{character_string4.DATA}}",
			new int[] { 2, 1, 3, 4 }),

	invite_success_307(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.INVITE_SUCCESS, 307, 817, "邀请成功通知",
			"活动名称{{name1.DATA}}奖品名称{{name2.DATA}}完成时间{{time3.DATA}}", new int[] { 1, 2, 3 }),

	refund_result_307(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.REFUND_RESULT, 307, 1435, "退款通知",
			"退款金额{{amount2.DATA}}订单号{{character_string4.DATA}}申请时间{{date3.DATA}}商品名称{{thing5.DATA}}退款状态{{thing6.DATA}}",
			new int[] { 2, 4, 3, 5, 6 }),

	balance_change_307(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.BALANCE_CHANGE, 307, 1972, "余额变动提醒",
			"变动金额{{amount1.DATA}}账户余额{{amount2.DATA}}变动时间{{time3.DATA}}变动原因{{thing4.DATA}}", new int[] { 1, 2, 3, 4 }),
	/** 服装/鞋/箱包 307结束 **/


	/** 食品 321开始 **/
	audit_321(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.AUDIT, 321, 1492, "审核结果通知",
			"审核结果{{phrase1.DATA}}审核内容{{thing3.DATA}}审核时间{{date5.DATA}}", new int[] { 1, 3, 5 }),

	order_deliver_321(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.ORDER_DELIVER, 321, 1368, "订单发货通知",
			"订单编号{{character_string1.DATA}}物流服务{{phrase4.DATA}}快递单号{{character_string5.DATA}}收货地址{{thing7.DATA}}发货时间{{time9.DATA}}",
			new int[] { 1, 4, 5, 7, 9 }),

	refund_result_321(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.REFUND_RESULT, 321, 1480, "退款通知",
			"退款名称{{thing2.DATA}}退款金额{{amount3.DATA}}退款时间{{time4.DATA}}退款状态{{thing9.DATA}}", new int[] { 2, 3, 4, 9 }),
	/** 食品 321结束 **/


	/** 美妆/洗护 786开始 */
	order_deliver_786(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.ORDER_DELIVER, 786, 1856, "订单发货通知",
			"商品名称{{thing5.DATA}}快递单号{{character_string3.DATA}}", new int[] { 5, 3 }),
	/** 美妆/洗护 786结束 */

    /** 私立医疗机构 47开始 */
    consultation_order_pay_47(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.CONSULTATION_ORDER_PAY, 47, 3199, "新问诊单通知",
        "患者信息{{thing1.DATA}}病情描述{{thing2.DATA}}填写时间{{date3.DATA}}温馨提示{{thing4.DATA}}", new int[] { 1, 2, 3, 4 }),

    consultation_order_expire_47(com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory.CONSULTATION_ORDER_EXPIRE, 47, 4137, "咨询已超时通知",
        "咨询状态{{thing1.DATA}}温馨提示{{thing2.DATA}}", new int[] { 1, 2 }),

    consultation_answer_47(SubcribeTemplateCategory.CONSULTATION_ANSWER, 47, 3608, "咨询回复通知",
        "温馨提示{{thing2.DATA}}医生姓名{{name3.DATA}}居民姓名{{thing9.DATA}}咨询内容{{thing1.DATA}}", new int[] { 2, 3, 9, 1}),

    consultation_success_47(SubcribeTemplateCategory.CONSULTATION_SUCCESS, 47, 7233, "医生已接诊提醒",
        "患者姓名{{name1.DATA}}病情描述{{thing3.DATA}}接诊医生{{name2.DATA}}备注说明{{thing5.DATA}}科室{{thing7.DATA}}", new int[] { 1, 3, 2, 5, 7}),

    order_deliver_47(SubcribeTemplateCategory.ORDER_DELIVER, 47, 10127, "发货成功提醒",
        "订单编号{{character_string1.DATA}}发货状态{{phrase2.DATA}}发货日期{{time3.DATA}}", new int[] { 1, 2, 3 }),

    order_refund_success_47(SubcribeTemplateCategory.REFUND_RESULT, 47, 4728, "退款成功通知",
        "订单号{{character_string1.DATA}}退款时间{{date2.DATA}}退款金额{{amount3.DATA}退款理由{{phrase4.DATA}}", new int[] { 1, 2, 3, 4 }),

    order_new_47(SubcribeTemplateCategory.ORDER_NEW, 47, 5208, "新订单提醒",
        "订单编号{{character_string1.DATA}}客户昵称{{name2.DATA}}客户备注{{thing3.DATA}}", new int[] { 1, 2, 3 }),
    /** 私立医疗机构 47结束 */
    /** 公立医疗机构 411开始*/
    consultation_success_411(SubcribeTemplateCategory.CONSULTATION_SUCCESS, 411, 1181, "医生接诊提醒",
        "服务项目{{thing1.DATA}}患者姓名{{name2.DATA}}接诊医生{{name3.DATA}}接诊时间{{time4.DATA}}备注说明{{thing5.DATA}}", new int[] { 1, 2, 3, 4, 5 }),

    order_deliver_411(SubcribeTemplateCategory.ORDER_DELIVER, 411, 9521, "药品发货提醒",
        "收货人{{thing1.DATA}}收货手机{{phone_number2.DATA}}收货地址{{thing3.DATA}}", new int[] { 1, 2, 3 }),

    order_refund_success_411(SubcribeTemplateCategory.REFUND_RESULT, 411, 11851, "退费成功通知",
        "温馨提示{{thing1.DATA}}缴费时间{{time4.DATA}}退费金额{{amount5.DATA}}", new int[] { 1, 4,5 });

    /**公立医疗机构 411结束*/
	/** 模板名称，小程序端发送名称找对应要用那个 */
	private String templeName;
	/** 账号的类目id */
	private Integer id;
	/** 模板标题 tid */
	private Integer tid;
	/** 模板标题 */
	private String title;
	/** 模版内容 */
	private String content;
	/** 开发者自行组合好的模板关键词列表 */
	private int[] kidList;

	@JsonCreator
	public static SubscribeMessageConfig getConfig(String id) {
		for (SubscribeMessageConfig item : values()) {
			if (item.getId().equals(id)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 根据类目和模板名称找到需要的模板
	 *
	 * @param id
	 * @param templeName
	 * @return
	 */
	public static SubscribeMessageConfig getByTempleName(Integer id, String templeName) {
		SubscribeMessageConfig[] subscribeMessageConfigs = SubscribeMessageConfig.values();
		for (int i = 0; i < subscribeMessageConfigs.length; i++) {
			SubscribeMessageConfig subscribeMessageConfig = subscribeMessageConfigs[i];
			if (subscribeMessageConfig.getId().equals(id)
					&& subscribeMessageConfig.getTempleName().equals(templeName)) {
				return subscribeMessageConfig;
			}
		}
		return null;
	}

	/**
	 * 获取所有的二级类目Id
	 *
	 * @return
	 */
	public static Set<Integer> getSecondIdList() {
		Set<Integer> sets = new LinkedHashSet<Integer>();
		SubscribeMessageConfig[] subscribeMessageConfigs = SubscribeMessageConfig.values();
		for (int i = 0; i < subscribeMessageConfigs.length; i++) {
			sets.add(subscribeMessageConfigs[i].id);
		}
		return sets;
	}

	/**
	 * 根据templeName找tid，因为所有templeName相同的tid都一样。匹配到一个就行
	 *
	 * @param templeName
	 * @return
	 */
	public static Integer getTid(String templeName) {
		SubscribeMessageConfig[] subscribeMessageConfigs = SubscribeMessageConfig.values();
		for (int i = 0; i < subscribeMessageConfigs.length; i++) {
			SubscribeMessageConfig subscribeMessageConfig = subscribeMessageConfigs[i];
			if (subscribeMessageConfig.getTempleName().equals(templeName)) {
				return subscribeMessageConfig.getTid();
			}
		}
		return null;
	}

	/**
	 * 类目下是否有匹配的templeName
	 * @param getcategoryList
	 * @param templeName
	 * @return
	 */
	public static SubscribeMessageConfig getIsExeit(List<Integer> getcategoryList, String templeName) {
		SubscribeMessageConfig[] subscribeMessageConfigs = SubscribeMessageConfig.values();
		for (SubscribeMessageConfig subscribeMessageConfig : subscribeMessageConfigs) {
			if (subscribeMessageConfig.getTempleName().equals(templeName)) {
				for (Integer id : getcategoryList) {
					if(subscribeMessageConfig.getId().equals(id)) {
						return subscribeMessageConfig;
					}
				}
			}
		}
		return null;
	}
}
