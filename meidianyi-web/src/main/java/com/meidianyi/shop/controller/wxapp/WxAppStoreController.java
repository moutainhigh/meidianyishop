package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.RequestUtil;
import com.meidianyi.shop.config.SmsApiConfig;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderParam;
import com.meidianyi.shop.service.pojo.shop.order.store.StoreOrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.ShipParam;
import com.meidianyi.shop.service.pojo.shop.patient.PatientSmsCheckParam;
import com.meidianyi.shop.service.pojo.shop.sms.template.SmsTemplate;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountVo;
import com.meidianyi.shop.service.pojo.shop.store.comment.ServiceCommentVo;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.pojo.wxapp.store.*;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreClerkAuthParam;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreMainShowVo;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreOrderListParam;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreOrderListVo;
import com.meidianyi.shop.service.saas.shop.StoreAccountService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.meidianyi.shop.common.foundation.data.JsonResultCode.CODE_SUCCESS;
import static com.meidianyi.shop.common.foundation.excel.AbstractExcelDisposer.DEFAULT_LANGUAGE;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * @author 王兵兵
 *
 * 2019年7月24日
 */
@Slf4j
@RestController
@RequestMapping("/api/wxapp/store")
public class WxAppStoreController extends WxAppBaseController{
    @Autowired
    private SmsService smsService;
    @Autowired
    private StoreAccountService storeAccountService;
    /**
     * 门店列表
     */
    @PostMapping("/list")
    public JsonResult storeList(@RequestBody @Validated StoreListParam param) {
        return success(shop().store.wxService.getList(param));
    }

    /**
     * 门店信息
     */
    @PostMapping("/info")
    public JsonResult storeInfo(@RequestBody @Validated StoreInfoParam param) {
        return this.success(shop().store.wxService.getWxappStoreDetail(param));
    }

    /**
     * 医院信息
     * @return
     */
    @PostMapping("/hospital/info")
    public JsonResult hospitalInfo(@RequestBody StoreInfoParam storeInfoParam) {
        StoreInfoVo hospitalInfo = shop().store.wxService.getHospitalInfo(storeInfoParam.location);
        if (hospitalInfo != null) {
            return this.success(hospitalInfo);
        }
        return fail(JsonResultCode.CODE_IS_NOT_EXIST_HOSPITAL);
    }

    /**
     * 门店买单
     */
    @PostMapping("/payOrder")
    public JsonResult storePayOrder(@RequestBody @Validated({StorePayOrder.class}) StoreInfoParam param) {
        StorePayOrderVo vo = shop().store.wxService.storePayOrder(param);
        ShopRecord shopRecord = saas.shop.getShopById(shop().store.getShopId());
        // 店铺营业状态和logo
        vo.setShopBusinessState(shopRecord.getBusinessState());
        vo.setShopLogo(shopRecord.getShopAvatar());
        return this.success(vo);
    }

    /**
     * 门店买单支付
     */
    @PostMapping("/confirmPay")
    public JsonResult confirmPay(@RequestBody @Validated({StoreConfirmPay.class}) StoreInfoParam param, HttpServletRequest request) {
        param.setClientIp(RequestUtil.getIp(request));
        log.debug("客户端ip地址为：{}", param.getClientIp());
        return this.success(shop().store.wxService.storePay(param));
    }

    /**
     * 门店买单支付订单详情-小程序端
     */
    @PostMapping("/pay/orderDetail")
    public JsonResult payOrderDetail(@RequestBody @Valid OrderParam order) {
        StoreOrderInfoVo result = shop().readOrder.getStoreOrder(order.getOrderSn());
        String language = getLang();
        if (!DEFAULT_LANGUAGE.equals(language)) {
            result.setProvinceCode(saas.region.province.getProvincePinYinByCode(result.getProvinceCode()));
            result.setCityCode(saas.region.province.getCityPinYinByCode(result.getCityCode()));
            result.setDistrictCode(saas.region.province.getDistrictPinYinByCode(result.getDistrictCode()));
        } else {
            result.setProvinceCode(saas.region.province.getProvinceName(Integer.valueOf(result.getProvinceCode())).getName());
            result.setCityCode(saas.region.city.getCityName(Integer.valueOf(result.getCityCode())).getName());
            result.setDistrictCode(saas.region.district.getDistrictName(Integer.valueOf(result.getDistrictCode())).getName());
        }
        return success(result);
    }

    /**
     * 门店服务预约详情
     */
    @PostMapping("/service/reservation")
    public JsonResult reservation(@RequestBody @Validated ReservationParam param) {
        param.initScene();
        return this.success(shop().store.reservation.reservationDetail(param.getServiceId()));
    }

    /**
     * 门店服务预约订单确认
     */
    @PostMapping("/service/confirmReservation")
    public JsonResult confirmReservation(@RequestBody @Validated(ConfirmReservation.class) ReservationParam param) {
        return this.success(shop().store.reservation.createReservation(param.getServiceId(), param.getUserId()));
    }

    /**
     * 门店服务预约订单提交
     */
    @PostMapping("/service/submitReservation")
    public JsonResult submitReservation(@RequestBody @Validated SubmitReservationParam param, HttpServletRequest request) {
        param.setClientIp(RequestUtil.getIp(request));
        log.debug("客户端ip地址为：{}", param.getClientIp());
        return this.success(shop().store.reservation.submitReservation(param));
    }

    /**
     * 门店服务预约订单继续支付
     */
    @PostMapping("/service/reservationContinuePay")
    public JsonResult reservationContinuePay(@RequestBody @Validated OrderSnParam param, HttpServletRequest request) {
        param.setClientIp(RequestUtil.getIp(request));
        log.debug("客户端ip地址为：{}", param.getClientIp());
        return this.success(shop().store.reservation.continuePay(param.getOrderSn(), param.getClientIp()));
    }

    /**
     * 门店服务预约订单详情查询（根据订单编号）
     */
    @PostMapping("/service/reservationDetail")
    public JsonResult reservationDetail(@RequestBody @Validated(ValidCon.class) ReservationDetail param) {
        return this.success(shop().store.reservation.getReservationDetail(param));
    }

    /**
     * 门店服务预约订单确认完成
     */
    @PostMapping("/service/comfirmComplete")
    public JsonResult comfirmComplete(@RequestBody @Validated(ValidCon1.class) ReservationDetail param) {
        return this.success(shop().store.reservation.confirmComplete(param));
    }

    /**
     * 门店服务预约订单列表查询（获取全部）
     */
    @PostMapping("/service/reservationAllList")
    public JsonResult reservationAllList(@RequestBody @Validated(ValidCon.class) ReservationParam param) {
        return this.success(shop().store.reservation.reservationList(param.getUserId()));
    }

    /**
     * 门店服务预约订单列表查询（按照订单状态获取）
     */
    @PostMapping("/service/reservationList")
    public JsonResult reservationList(@RequestBody @Validated(ValidCon1.class) ReservationParam param) {
        return this.success(shop().store.reservation.reservationList(param.getUserId(), param.getOrderStatus()));
    }

    /**
     * 删除门店服务预约订单
     */
    @PostMapping("/service/reservationDel")
    public JsonResult reservationDel(@RequestBody @Validated(ValidCon3.class) ReservationDetail param) {
        shop().store.reservation.reservationDel(param.getOrderId());
        return this.success();
    }

    /**
     * 取消待付款预约订单
     */
    @PostMapping("/service/cancelReservation")
    public JsonResult cancelWaitToPayReservation(@RequestBody @Validated(ValidCon2.class) ReservationDetail param) {
        shop().store.reservation.cancelWaitToPayReservation(param);
        return this.success();
    }

    /**
     * 获取门店服务预约订单评价
     */
    @PostMapping("/service/reservationComment")
    public JsonResult reservationComment(@RequestBody @Validated(ValidCon.class) ReservationDetail param) {
        return this.success(shop().store.reservation.reservationComment(param.getOrderSn()));
    }

    /**
     * 添加门店服务预约订单评价
     */
    @PostMapping("/service/createComment")
    public JsonResult createComment(@RequestBody @Validated(ValidCon.class) ServiceCommentVo param) {
        shop().store.reservation.createComment(param);
        return this.success();
    }

    /**
     * 查看服务的全部评价
     */
    @PostMapping("/service/allComment")
    public JsonResult allComment(@RequestBody @Validated AllCommentParam param) {
        return this.success(shop().store.reservation.commentService.getAllComment(param));
    }

    /**
     * 取消预约/获取门店联系方式
     */
    @GetMapping("/service/getStoreMobile/{storeId}")
    public JsonResult cancelReservation(@PathVariable @NotEmpty Integer storeId) {
        return this.success(shop().store.reservation.getStoreMobile(storeId));
    }

    /**
     * 店员认证
     * @param param
     * @return
     */
    @PostMapping("/storeClerk/auth")
    public JsonResult salesclerkAuth(@RequestBody StoreClerkAuthParam param){
        WxAppSessionUser user=wxAppAuth.user();
        param.setUserId(user.getUserId());
        param.setShopId(user.getShopId());
        try {
            Integer accountId=shop().store.wxService.storeClerkAuth(param,wxAppAuth.user());
            if(accountId!=null){
                wxAppAuth.updateStoreClerkUserType(accountId);
            }
        } catch (MpException e) {
           return fail(e.getErrorCode());
        }
        return success();
    }

    /**
     * 发送验证码
     * @param param
     * @return
     */
    @PostMapping("/storeClerk/send/check/code")
    public JsonResult sendCheckSms(@RequestBody @Validated PatientSmsCheckParam param){
        param.setUserId(wxAppAuth.user().getUserId());
        try {
            JsonResultCode jsonResultCode = smsService.checkIsOutOfSmsNum(wxAppAuth.user().getUserId(), "");
            if (!jsonResultCode.equals(CODE_SUCCESS)){
                return fail(jsonResultCode);
            }
            smsService.sendCheckSms(param, SmsTemplate.SALESCLERK_CHECK_MOBILE, SmsApiConfig.REDIS_KEY_SMS_CHECK_SALESCLERK_MOBILE);
        } catch (MpException e) {
            return fail();
        }
        return success();
    }

    /**
     *校验当前用户身份
     * @return
     */
    @PostMapping("/storeClerk/auth/check")
    public JsonResult checkAuth(){
        return success(wxAppAuth.user());
    }

    /**
     *首页
     * @return
     */
    @PostMapping("/storeClerk/main")
    public JsonResult storeMainShow(){
        WxAppSessionUser user = wxAppAuth.user();
        StoreAccountVo storeAccountVo=storeAccountService.getOneInfo(user.getStoreAccountId());
        StoreMainShowVo storeMainShowVo=shop().store.wxService.storeMainShow(storeAccountVo);
        return success(storeMainShowVo);
    }

    /**
     * 订单列表
     * @param param
     * @return
     */
    @PostMapping("/storeClerk/order/list")
    public JsonResult getStoreClerkOrderList(@RequestBody StoreOrderListParam param){
        WxAppSessionUser user = wxAppAuth.user();
        param.setStoreAccountId(user.getStoreAccountId());
        PageResult<StoreOrderListVo> result = shop().store.wxService.getStoreClerkOrderList(param);
        return success(result);
    }

}
