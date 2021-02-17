package com.meidianyi.shop.service.pojo.shop.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.meidianyi.shop.common.foundation.util.RegexUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公众号模版消息
 * @author 卢光耀
 * @date 2019-08-22 10:22
 *
*/
public enum MpTemplateConfig {
    /**
     * 消息推送
     */
    ACTIVITY_CONFIG(
        "OPENTM415477060",
        "业务处理结果通知",
        "{{first.DATA}}业务类型：{{keyword1.DATA}}业务内容：{{keyword2.DATA}}处理结果：" +
            "{{keyword3.DATA}}操作时间：{{keyword4.DATA}}{{remark.DATA}}"),

    PUSHMSG(
            "OPENTM411665252",
            "反馈结果通知",
            "{{first.DATA}}回复者：{{keyword1.DATA}}回复时间：{{keyword2.DATA}}回复内容：{{keyword3.DATA}}{{remark.DATA}}"),

    COUPON_EXPIRE(
            "OPENTM408237933",
            "服务到期提醒",
            "{{first.DATA}}业务号码：{{keyword1.DATA}}业务类型：{{keyword2.DATA}}到期时间：{{keyword3.DATA}}{{remark.DATA}}"),

    APPOINTMENT_REMINDER(
            "OPENTM414204481",
            "预约到期提醒",
            "{{first.DATA}}单据号：{{keyword1.DATA}}开始时间：{{keyword2.DATA}}{{remark.DATA}}"),

    APPOINTMENT_SUCCESS(
        "OPENTM410419150",
        "预约成功通知",
        "{{first.DATA}}服务名称：{{keyword1.DATA}}预约日期：{{keyword2.DATA}}预约人：{{keyword3.DATA}}联系电话：{{keyword4.DATA}}预约机构：{{keyword5.DATA}}{{remark.DATA}}"),

    PAYMENT_REMINDER(
            "OPENTM401751289",
            "订单待付款提醒",
            "{{first.DATA}}订单编号：{{keyword1.DATA}}支付金额：{{keyword2.DATA}}{{remark.DATA}}"),

    LOTTERY_RESULTS(
            "OPENTM412181311",
            "抽奖结果通知",
            "{{first.DATA}}奖品名称：{{keyword1.DATA}}中奖时间：{{keyword2.DATA}}{{remark.DATA}}"),
    
    SERVICE_ORDER_CANCEL(
            "OPENTM207847150",
                "预约取消通知",
                "{{first.DATA}}预约项目：{{keyword1.DATA}}预约时间：{{keyword2.DATA}}取消原因：{{keyword3.DATA}}{{remark.DATA}}"),
    GROUP_SUCCESS(
            "OPENTM400932513",
            "拼团成功通知",
            "{{first.DATA}}商品名称：{{keyword1.DATA}}团长：{{keyword2.DATA}}成团人数：{{keyword3.DATA}}{{remark.DATA}}"),
    BARGAIN_SUCCESS(
        "OPENTM410292733",
        "砍价成功提醒",
        "{{first.DATA}}商品名称：{{keyword1.DATA}}底价：{{keyword2.DATA}}{{remark.DATA}}"),
    ORDER_DELIVER(
        "OPENTM200565259",
        "订单发货提醒",
        "{{first.DATA}}订单编号：{{keyword1.DATA}}物流公司：{{keyword2.DATA}}物流单号：{{keyword3.DATA}}{{remark.DATA}}"
    ),
    ORDER_REFUND (
        "TM00004",
        "退款通知",
        "{{first.DATA}}退款原因：{{reason.DATA}}退款金额：{{refund.DATA}}{{remark.DATA}}"
    ),
    ORDER_REFUND_FAIL (
        "OPENTM412546294",
        "退款失败通知",
        "{{first.DATA}}订单编号：{{keyword1.DATA}}退款时间：{{keyword2.DATA}}退款金额：{{keyword3.DATA}}失败原因：{{keyword4.DATA}}{{remark.DATA}}"
    ),
    ORDER_WXPAY_SUCCESS (
        "OPENTM416836000",
        "订单支付成功提醒",
        "{{first.DATA}}订单编号：{{keyword1.DATA}}商品名称：{{keyword2.DATA}}订单总价：{{keyword3.DATA}}订单状态：{{keyword4.DATA}}下单时间：{{keyword5.DATA}}{{remark.DATA}}"
    ),
    ORDER_NOPAY_NOTIFY (
        "OPENTM401717201",
        "待支付订单提醒",
        "{{first.DATA}}商品详情：{{keyword1.DATA}} 待付金额：{{keyword2.DATA}} 交易单号：{{keyword3.DATA}} 交易时间：{{keyword4.DATA}}交易有效期：{{keyword5.DATA}}{{remark.DATA}}"
    ),
    ORDER_SELFPICKUP_SUCCESS (
        "OPENTM412465579",
        "取货成功通知",
        "{{first.DATA}}订单号：{{keyword1.DATA}}取货时间：{{keyword2.DATA}}{{remark.DATA}}"
    ),
    ORDER_RECEIVED (
        "OPENTM202314085",
        "订单确认收货通知",
        "{{first.DATA}}订单号：{{keyword1.DATA}}商品名称：{{keyword2.DATA}}下单时间：{{keyword3.DATA}}发货时间：{{keyword4.DATA}}确认收货时间：{{keyword5.DATA}}{{remark.DATA}}"
    ),
    GROUP_FAIL (
            "OPENTM401113750",
            "拼团失败通知",
            "{{first.DATA}}拼团商品：{{keyword1.DATA}}商品金额：{{keyword2.DATA}}退款金额：{{keyword3.DATA}}{{remark.DATA}}"
        ),
    SCORE_CHANGE(
    		"OPENTM207681011",
    		"积分消费提醒",
    		"{{first.DATA}}服务内容：{{keyword1.DATA}}积分变化：{{keyword2.DATA}}商户名称：{{keyword3.DATA}}日期时间：{{keyword4.DATA}}{{remark.DATA}}"
    	),
    MONEY_CHANGE(
    		"OPENTM402190178",
    		"账户资金变动提醒",
    		"{{first.DATA}}变动时间：{{keyword1.DATA}}变动金额：{{keyword2.DATA}}账户余额：{{keyword3.DATA}}{{remark.DATA}}"
    	),
    MEMBER_LEVEL_UP(
    		"OPENTM401075189",
    		"会员升级通知",
    		"{{first.DATA}}会员等级：{{keyword1.DATA}}审核状态：{{keyword2.DATA}}{{remark.DATA}}"
    	),
    AUDIT_FAIL(
    	"AT0442",
    	"审核未通过提醒",
    	"审核结果{{keyword1.DATA}}审核时间{{keyword2.DATA}}未通过原因{{keyword3.DATA}}申请时间{{keyword4.DATA}}申请内容{{keyword5.DATA}}"
    ),
    AUDIT_SUCCESS(
        	"AT0168",
        	"审核通过提醒",
        	"审核结果{{keyword1.DATA}}审核时间{{keyword2.DATA}}申请时间{{keyword3.DATA}}申请内容{{keyword4.DATA}}"
        ),
    REBATE_USER_UP_GRADE(
        "OPENTM403179330",
        "分销商等级变动通知",
        "{{first.DATA}}当前等级：{{keyword1.DATA}}原始等级：{{keyword2.DATA}}升级时间：{{keyword3.DATA}}{{remark.DATA}}"
    ),
    NEW_ORDER_REMIND(
            "OPENTM204958750",
            "店铺新订单成交通知",
            "{{first.DATA}}订单金额：{{keyword1.DATA}}订单详情：{{keyword2.DATA}}订单号：{{keyword3.DATA}}买家会员：{{keyword4.DATA}}{{remark.DATA}}"
        ),
	GET_CARD(
		"OPENTM405766398",
		"会员卡领取成功通知",
		"{{first.DATA}}会员卡：{{keyword1.DATA}}会员类型：{{keyword2.DATA}}关联手机：{{keyword3.DATA}}有效期至：{{keyword4.DATA}}{{remark.DATA}}"
	),
    GET_COUPON(
		"OPENTM409797795",
        "办理成功通知",
        "{{first.DATA}}商户名称：{{keyword1.DATA}}卡券名称：{{keyword2.DATA}}有效期：{{keyword3.DATA}}办理时间：{{keyword4.DATA}}{{remark.DATA}}"
    ),
    GET_MONEY(
        "OPENTM410103702",
        "提现申请通知",
        "{{first.DATA}}申请时间：{{keyword1.DATA}}提现金额：{{keyword2.DATA}}{{remark.DATA}}"
    ),
    NEW_CONSULTATION_ORDER(
        "OPENTM418215361",
        "订单接诊提醒",
        "{{first.DATA}}患者姓名：{{keyword1.DATA}}订单类型：{{keyword2.DATA}}订单编号：{{keyword3.DATA}}支付时间：{{keyword4.DATA}}病情描述：{{keyword5.DATA}}{{remark.DATA}}"
    ),
    WAIT_HANDLE_ORDER(
        "OPENTM208008351",
        "新订单提醒",
        "{{first.DATA}}客户姓名：{{keyword1.DATA}}客户电话：{{keyword2.DATA}}取货方式：{{keyword3.DATA}}预约取货时间：{{keyword4.DATA}}{{remark.DATA}}"
    ),
    SALE_AFTER_ORDER(
        "OPENTM418226364",
        "退款申请通知",
        "{{first.DATA}}订单编号：{{keyword1.DATA}}下单时间：{{keyword2.DATA}}订单来源：{{keyword3.DATA}}退款金额：{{keyword4.DATA}}退款原因：{{keyword5.DATA}}{{remark.DATA}}"
    )
    ;
	/**
	 * 模板编号
	 */
	private String templateNo;

	/**
	 * 标题
	 */
    private String title;

	/**
	 * 内容，样例： 您好，您已购买成功。商品信息：{{name.DATA}}{{remark.DATA}}
	 */
    private String content;
    /**
     * 颜色设置
     */
    private Map<String,String> colors;



    @JsonCreator
    public static MpTemplateConfig getConfig(String templateNo){
        for(MpTemplateConfig item : values()){
            if(item.getTemplateNo().equals(templateNo) ){
                return item;
            }
        }
        return null;
    }

    MpTemplateConfig(String templateNo,String title,String content,String[][] color){
        final Map<String, String> map = new HashMap<>((int) (color.length * 1.5));
        this.templateNo = templateNo;
        this.title = title;
        this.content = content;
        List<String> list = RegexUtil.getSubStrList("{{",".",content);
        for(String s: list){
            map.put(s,"#173177");
        }
        for( int i =0, len = color.length; i<len;i++ ){
            String[] object = color[i];
            map.put(object[0],object[1]);
        }
        this.colors = map;
    }
    MpTemplateConfig(String templateNo,String title,String content){
        List<String> list = RegexUtil.getSubStrList("{{",".",content);
        final Map<String, String> map = new HashMap<>((int) (list.size() * 1.5));
        this.templateNo = templateNo;
        this.title = title;
        this.content = content;
        for(String s: list){
            map.put(s,"#173177");
        }
        this.colors = map;
    }
    @JsonValue
    public String getTemplateNo() {
        return templateNo;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Map<String,String> getColors() {
        return colors;
    }
}
