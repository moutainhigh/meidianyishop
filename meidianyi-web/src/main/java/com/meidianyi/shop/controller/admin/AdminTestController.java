package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.department.StringParam;
import com.meidianyi.shop.service.pojo.shop.maptemplate.OrderDeliverParam;
import com.meidianyi.shop.service.pojo.shop.maptemplate.OrderNewParam;
import com.meidianyi.shop.service.pojo.shop.maptemplate.OrderSaleAfterParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.pojo.wxapp.subscribe.TemplateVo;
import com.meidianyi.shop.service.saas.shop.ThirdPartyMsgServices;
import com.meidianyi.shop.service.shop.maptemplatesend.MapTemplateSendService;
import com.meidianyi.shop.service.shop.task.wechat.WechatTaskService;
import com.meidianyi.shop.service.shop.user.message.SubscribeMessageService;
import com.meidianyi.shop.service.wechat.OpenPlatform;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGeKeywordResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGetCategoryResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGetTemplateListResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGetTemplateTitleResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubscribeAddTemplateResult;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lixinguo
 *
 */
@RestController
@Slf4j
public class AdminTestController extends AdminBaseController {

    @Autowired
    protected OpenPlatform open;
    @Autowired
    private SubscribeMessageService subservice;

    @Autowired
    private WechatTaskService wechatTaskService;

	@Autowired
	private ThirdPartyMsgServices thirdPartyMsgServices;
	@Autowired
    private MapTemplateSendService mapTemplateSendService;


    @RequestMapping(value = "/api/admin/test/updateOrder")
	public JsonResult updateOrder(){
        log.info("【同步任务】---订单数据同步到主库");
        Result<ShopRecord> result = saas.shop.getAll();
        //同步最近一天的订单
        result.parallelStream().forEach(shopRecord -> {
            saas.getShopApp(shopRecord.getShopId()).shopTaskService.tableTaskService.orderDeltaUpdates(shopRecord.getShopId());
            saas.getShopApp(shopRecord.getShopId()).shopTaskService.tableTaskService.ruturnOrderDeltaUpdates(shopRecord.getShopId());
        });
        return success();
    }


    @RequestMapping(value = "/api/admin/test/addtemplate")
    public JsonResult addtemplate() throws Exception {
        String appId = "wxbb38922409fdaa24";
        String tid = "1116";
        int[] kidList = { 1, 2, 3 };
        String sceneDesc = "模板描述";
        WxOpenMaSubscribeAddTemplateResult result = open.getMaExtService().addTemplate(appId, tid, kidList, sceneDesc);
        return success(result);
    }

    @RequestMapping(value = "/api/admin/test/deltemplate")
    public JsonResult deltemplate() throws Exception {
        String appId = "wxbb38922409fdaa24";
        String priTmplId = this.request.getParameter("priTmplId");
        WxOpenResult result = open.getMaExtService().delTemplate(appId, priTmplId);
        return success(result);
    }

    @RequestMapping(value = "/api/admin/test/getCategory")
    public JsonResult getCategory() throws Exception {
        String appId = "wxfbe41e702f99277d";
        WxOpenMaSubScribeGetCategoryResult result = open.getMaExtService().getTemplateCategory(appId);
        return success(result);
    }

    @RequestMapping(value = "/api/admin/test/getKeywords")
    public JsonResult getPubTemplateKeyWordsById() throws Exception {
        String appId = "wxbb38922409fdaa24";
        String tid = this.request.getParameter("tid");
        WxOpenMaSubScribeGeKeywordResult result = open.getMaExtService().getPubTemplateKeyWordsById(appId, tid);
        return success(result);
    }

    @RequestMapping(value = "/api/admin/test/getTitleList")
    public JsonResult getPubTemplateTitleList() throws Exception {
        String appId = "wxbb38922409fdaa24";
        String ids = this.request.getParameter("ids");
        int start = 0;
        int limit =30;
        WxOpenMaSubScribeGetTemplateTitleResult result = open.getMaExtService().getPubTemplateTitleList(appId, ids, start, limit);
        return success(result);
    }

    @RequestMapping(value = "/api/admin/test/getTemplateList")
    public JsonResult getTemplateList() throws Exception {
        String appId = "wxbb38922409fdaa24";
        WxOpenMaSubScribeGetTemplateListResult result = open.getMaExtService().getTemplateList(appId);
        return success(result);
    }

//	@RequestMapping(value = "/api/admin/test/send")
//	public JsonResult send() throws Exception {
//		String appId = "wxbb38922409fdaa24";
//		String toUser =  this.request.getParameter("toUser");
//		String templateId =  this.request.getParameter("templateId");
//		String page= this.request.getParameter("page");
//
//		Map<String, Map<String, String>> data = new LinkedHashMap<>();
//		Map<String, String> v1 = new LinkedHashMap<>();
//		v1.put("value", "活动内容");
//		data.put("thing1", v1);
//
//		Map<String, String> v2 = new LinkedHashMap<>();
//		v2.put("value", "活动地址");
//		data.put("thing6", v2);
//
//		WxOpenResult result = open.getMaExtService().sendTemplate(appId, toUser, templateId, page, data);
//		return success(result);
//	}
//

    @RequestMapping(value = "/api/admin/test/sendTest")
    public JsonResult testSend() {
        String[][] data = new String[][] { { "金坷垃抽奖" }, { Util.getdate("yyyy-MM-dd HH:mm:ss") }, { "获得一车金坷垃" } };
        String[][] data2 = new String[][] { { "金色传说测试" }, { "传说" }, { Util.getdate("yyyy-MM-dd HH:mm:ss")}};
        Boolean sendMessage=false;
        MaSubscribeData build = MaSubscribeData.builder().data307(data).build();
        MaSubscribeData build2 = MaSubscribeData.builder().data307(data2).build();
        try {
            sendMessage = subservice.sendMessage(195, SubcribeTemplateCategory.DRAW_RESULT, build, null);
            sendMessage = subservice.sendMessage(195,  SubcribeTemplateCategory.INVITE_SUCCESS, build2, null);
        } catch (WxErrorException e) {

            e.printStackTrace();
        }
        return success(sendMessage);

    }

    @RequestMapping(value = "/api/admin/test/getNeedTemplateId")
    public JsonResult getNeedTemplateId() {
        String[] data = { SubcribeTemplateCategory.DRAW_RESULT, SubcribeTemplateCategory.INVITE_SUCCESS};
        TemplateVo[] templateId = {};
        try {
            templateId = subservice.getTemplateId(data);
        } catch (WxErrorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return success(templateId);

    }


    @RequestMapping(value = "/api/admin/test/sendTestByMq/{id}")
    public JsonResult testSendByMq(@PathVariable Integer id) {
        String[][] data2 = new String[][] { { "金坷垃抽奖" }, { Util.getdate("yyyy-MM-dd HH:mm:ss") }, { "获得一车金坷垃" } };
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(id);
        MaSubscribeData data=MaSubscribeData.builder().data307(data2).build();
        RabbitMessageParam param = RabbitMessageParam.builder()
            .maTemplateData(
                MaTemplateData.builder().config(SubcribeTemplateCategory.DRAW_RESULT).data(data).build())
            .page(null).shopId(adminAuth.user().getLoginShopId())
            .userIdList(arrayList)
            .type(RabbitParamConstant.Type.LOTTERY_TEAM).build();
        saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), adminAuth.user().getLoginShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
        String[][] data3 = new String[][] { { "金色传说测试" }, { "传说" }, { Util.getdate("yyyy-MM-dd HH:mm:ss")}};
        return success();

    }

    /**
     * 小程序公众号一起发。小程序失败的，让公众号发
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/admin/test/sendMaAndMpByMq/{id}")
    public JsonResult testRecord(@PathVariable Integer id) {
        logger().info("混合发送");
        String page="/page/test/test";
        //小程序数据
        String[][] maData = new String[][] { { "金坷垃抽奖" }, { Util.getdate("yyyy-MM-dd HH:mm:ss") }, { "获得一车金坷垃" } };
        //公众号数据
        String[][] mpData = new String[][] { { "尊敬的用户，您的优惠券", "#173177" }, { "", "#173177" }, { "测测测测名字", "#173177" },
            { Util.getdate("yyyy-MM-dd HH:mm:ss"), "#173177" }, { "", "#173177" } };
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(id);
        MaSubscribeData data = MaSubscribeData.builder().data307(maData).build();
        RabbitMessageParam param = RabbitMessageParam.builder()
            .maTemplateData(
                MaTemplateData.builder().config(SubcribeTemplateCategory.DRAW_RESULT).data(data).build())
            .mpTemplateData(MpTemplateData.builder().config(MpTemplateConfig.COUPON_EXPIRE).data(mpData).build())
            .page(page).shopId(adminAuth.user().getLoginShopId()).userIdList(arrayList)
            .type(RabbitParamConstant.Type.LOTTERY_TEAM).build();
        //想混合发RabbitParamConstant选小程序的
        saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(),
            adminAuth.user().getLoginShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
        logger().info("混合发送发出");
        return success();
    }
    @RequestMapping(value = "/api/admin/test/testDrew")
    public JsonResult testDrew() {
        saas.getShopApp(8984736).groupDraw.groupDrawUser.dealOpenGroupDraw();
        return null;

    }
    @RequestMapping(value = "/api/admin/test/testWxData")
    public JsonResult testWxData() {
        saas.getShopApp(245547).shopTaskService.wechatTaskService.test();
        return null;

    }

    @RequestMapping(value = "/api/admin/test/testPin")
    public JsonResult testGroup() {
        saas.getShopApp(8984736).groupIntegration.updateState();
        return null;

    }

    @RequestMapping(value = "/api/admin/test/live")
    public JsonResult testLive() {
        logger().info("直播列表导入测试");
        Result<ShopRecord> result = saas.shop.getAll();
		result.forEach((r) -> {
			boolean liveList = saas.getShopApp(r.getShopId()).liveService.getLiveList();
			logger().info("获取店铺：{}；的直播列表结果：{}",r.getShopId(),liveList);
		});
        logger().info("直播列表导入测试结束");
        return success();

    }

    @RequestMapping(value = "/api/admin/test/sendOrderMsg")
    public JsonResult testSendOrderMsg() {
    	OrderInfoRecord order = saas.getShopApp(245547).member.order.getOrderByOrderSn("P202004092146259689");
    	thirdPartyMsgServices.thirdPartService(order);
		return success();
    }

    @RequestMapping(value = "/api/admin/test/expiring")
    public JsonResult expiringCouponNotify() {
    	logger().info("卡券到期提醒测试");
    	saas.getShopApp(shopId()).shopTaskService.maMpScheduleTaskService.expiringCouponNotify();
		return success();
    }

    @RequestMapping(value = "/api/admin/test/testTask")
    public JsonResult testTask() {
    	saas.getShopApp(shopId()).shopTaskService.wechatTaskService.beginDailyTask();
		return success();

    }

    @RequestMapping(value = "/api/admin/test/fetch/title")
    public JsonResult fetchTitle() {
        String json = "[{\"positionCode\":\"1231\",\"name\":\"外部职称1\",\"state\":1},{\"positionCode\":\"1232\",\"name\":\"外部职称2\",\"state\":1}]";
        saas.getShopApp(shopId()).titleService.fetchTitles(json);
        return success();

    }

    @RequestMapping(value = "/api/admin/test/fetch/department")
    public JsonResult fetchDepartment(@RequestBody StringParam param) {
        String json = "[{\"departCode\":\"31243\",\"departName\":\"科室1231\",\"state\":1,\"pid\":\"d19876732\"},{\"departCode\":\"s231\",\"departName\":\"科室3333\",\"state\":1,\"pid\":\"d328932\"}]";
        saas.getShopApp(shopId()).departmentService.fetchDepartments(param.getJson());
        return success();


    }

    @RequestMapping(value = "/api/admin/test/fetch/doctor")
    public JsonResult fetchDoctor(@RequestBody StringParam param) {
        String json = "[{\"departCode\":\"31243\",\"departName\":\"科室1231\",\"state\":1,\"pid\":\"d19876732\"},{\"departCode\":\"s231\",\"departName\":\"科室3333\",\"state\":1,\"pid\":\"d328932\"}]";
        try {
            saas.getShopApp(shopId()).doctorService.fetchDoctor(param.getJson());
        } catch (MpException e) {
            e.printStackTrace();
        }
        return success();


    }
    @RequestMapping(value = "/api/admin/test/notify/send")
    public JsonResult sendNotify() {
        List<Integer> userIdList=new ArrayList<>();
        userIdList.add(8);
        OrderSaleAfterParam param=OrderSaleAfterParam.builder().orderSn("P202020220202020").createTime("2020-10-15 10:02:40")
            .orderSource("门店配送").refundMoney("0.01").refundReason("不想要了")
            .userIds(userIdList).build();
        mapTemplateSendService.sendWaitSaleAfterMessage(param);
        return success();
    }
        @RequestMapping(value = "/api/admin/test/department/statistic")
    public JsonResult statisticDepartmentTest() {
        saas.getShopApp(shopId()).departmentService.departmentStatistics();
        return success();
    }}
