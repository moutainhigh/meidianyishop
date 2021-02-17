package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.service.pojo.saas.article.ArticleListQueryParam;
import com.meidianyi.shop.service.pojo.saas.article.ArticleVo;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.overview.*;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;

/**
 * @author liufei
 * date 2019/7/15
 * 商城概览
 */
@RestController
public class AdminMallOverviewController extends AdminBaseController {


    @Value(value = "${official.appId}")
	private String bindAppId;

    /**
     * 数据展示
     *
     * @param param the param
     * @return json result
     */
    @PostMapping("/api/admin/malloverview/datademonstration")
    public JsonResult dataDemonstration(@RequestBody @Validated DataDemonstrationParam param) {
        DataDemonstrationVo vo = shop().mallOverview.dataDemonstration(param);
        return vo != null ? success(vo) : fail(JsonResultMessage.OVERVIEW_MALL_DATADEMONSTRATION_GET_FAILED);
    }

    /**
     * 绑定解绑
     *
     * @param param the param
     * @return json result
     */
    @PostMapping("/api/admin/survey/official/bind")
    public JsonResult bindUnBindOfficial(@RequestBody BindAndUnParam param){
    	boolean bindUnBindOfficial = saas.overviewService.bindUnBindOfficial(param.getAct(),adminAuth.user(),param.getAccountId());
        return bindUnBindOfficial?success():fail();
    }

    /**
     * 获取绑定/解绑状态
     *
     * @return json result
     */
    @GetMapping("/api/admin/malloverview/getbindUnBindStatus")
    public JsonResult getbindUnBindStatus(){
        AdminTokenAuthInfo adminTokenAuthInfo=adminAuth.user();
        MpAuthShopRecord wxapp = saas.shop.mp.getAuthShopByShopId(adminTokenAuthInfo.getLoginShopId());
    	BindofficialVo getbindUnBindStatusUseByOver = saas.overviewService.getbindUnBindStatusUseByOver(adminAuth.user(),wxapp.getLinkOfficialAppId());
    	return success(getbindUnBindStatusUseByOver);
    }

    /**
     * 代办事项
     *
     * @return json result
     */
    @PostMapping("/api/admin/malloverview/toDoItem")
    public JsonResult toDoItem(@RequestBody ToDoItemParam param){
        ToDoItemVo vo = shop().mallOverview.toDoItem(param);
        return vo!=null ? success(vo) : fail(JsonResultMessage.OVERVIEW_MALL_TODOITEM_GET_FAILED);
    }

    /**
     * 获取店铺基本信息,店铺到期时间，店铺版本名称
     *
     * @return the json result
     */
    @GetMapping("/api/admin/malloverview/getShopBaseInfo")
    public JsonResult getShopBaseInfo() {
        AdminTokenAuthInfo adminTokenAuthInfo=adminAuth.user();
        MpAuthShopRecord wxapp = saas.shop.mp.getAuthShopByShopId(adminTokenAuthInfo.getLoginShopId());
        ShopBaseInfoVo vo = saas.overviewService.getShopBaseInfo(shop().mallOverview.getShopId(), wxapp.getLinkOfficialAppId(), adminAuth.user());
        return success(vo);
    }

    /**
     * 获取指定数量的公告,用于首页展示，显示id, title和time
     *
     * @param param the param
     * @return the json result
     */
    @PostMapping("/api/admin/malloverview/getFixedAnnouncement")
    public JsonResult getFixedAnnouncement(@RequestBody @Validated FixedAnnouncementParam param){
        return success(saas.overviewService.getFixedAnnouncement(param));
    }

    /**
     * 获取分页公告列表
     *
     * @param param the param
     * @return json result
     */
    @PostMapping("/api/admin/malloverview/getAnnouncementList")
    public JsonResult getAnnouncementList(@RequestBody ArticleListQueryParam param) {
        param.setCategoryId(1);
        param.setStatus(BYTE_ONE);
        param.setSortName("update_time,desc");
        PageResult<ArticleVo> pageList = saas.article.getPageList(param);
        return success(pageList);
    }

    /**
     * 店铺助手
     *
     * @param param the param
     * @return the json result
     */
    @PostMapping("/api/admin/malloverview/shopAssistant")
    public JsonResult shopAssistant(@RequestBody @Validated ShopAssistantParam param){
        return success(shopAssistantInvoke(param));
    }

    private ShopAssistantVo shopAssistantInvoke(ShopAssistantParam param) {
        ShopAssistantVo vo = shop().mallOverview.shopAssistant(param);
        Integer shopId = shop().mallOverview.getShopId();
        Integer sysId = shop().mallOverview.getSysId();
        vo.setWxDataShop(saas.overviewService.shopAssistant(shopId, sysId));
        vo.ruleHandler();
        return vo;
    }

    /**
     * Share shop share qr code vo.店铺分享
     *
     * @return the share qr code vo
     */
    @GetMapping("/api/admin/malloverview/shareShop")
    public JsonResult shareShop() {
        ShareQrCodeVo qrCodeVo = shop().store.share(QrCodeTypeEnum.PAGE_BOTTOM, "shopId=" + shop().store.getShopId());
        if (StringUtils.isBlank(qrCodeVo.getImageUrl())) {
            return fail(JsonResultCode.CODE_APPLET_QR_CODE_GET_FAILED);
        }
        return success(qrCodeVo);
    }

    /**
     * 商城概览页面综合调用接口
     *
     * @param param the param
     * @return the json result
     */
    @PostMapping("/api/admin/malloverview/allOverview")
    public JsonResult allOverview(@RequestBody @Validated OverviewParam param){
        AdminTokenAuthInfo adminTokenAuthInfo=adminAuth.user();
        MpAuthShopRecord wxapp = saas.shop.mp.getAuthShopByShopId(adminTokenAuthInfo.getLoginShopId());
        return success(OverviewVo.builder()
            //基本信息
            .shopBaseInfoVo(saas.overviewService.getShopBaseInfo(shop().mallOverview.getShopId(), wxapp.getLinkOfficialAppId(), adminAuth.user()))
            //公告信息
            .announcementVoList(saas.overviewService.getFixedAnnouncement(param.getFixedAnnouncementParam()))
            //数据展示
            .dataDemonstrationVo(shop().mallOverview.dataDemonstration(param.getDataDemonstrationParam()))
            //代办事项
            .toDoItemVo(shop().mallOverview.toDoItem(param.getToDoItemParam()))
            //店铺助手
            .shopAssistantVo(shopAssistantInvoke(param.getShopAssistantParam()))
            .build());
    }
}
