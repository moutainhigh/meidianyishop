package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.RequestUtil;
import com.meidianyi.shop.service.pojo.shop.coupon.*;
import com.meidianyi.shop.service.pojo.wxapp.coupon.*;
import com.meidianyi.shop.service.pojo.wxapp.coupon.pack.CouponPackIdParam;
import com.meidianyi.shop.service.pojo.wxapp.coupon.pack.CouponPackOrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.coupon.pack.CouponPackOrderParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * 用户优惠券
 * @author 常乐
 * @date 2019年9月24日
 */
@RestController
@RequestMapping("/api/wxapp/coupon")
public class WxAppCouponController extends WxAppBaseController {

	/**
	 * 用户优惠券列表
	 * @param param
	 * @return
	 */
	@PostMapping("/list")
	public JsonResult availCoupon(@RequestBody AvailCouponParam param) throws ParseException {
		AvailCouponListVo list = new AvailCouponListVo();
		Integer userId = wxAppAuth.user().getUserId();
		param.setUserId(userId);
		PageResult<AvailCouponVo> couponList = shop().coupon.getCouponByUser(param);
		list = shop().coupon.getEachStatusNum(userId,list);
		list.setCouponList(couponList);
		return this.success(list);
	}

	/**
	 * 优惠券详情
	 * @param param
	 * @return
	 */
	@PostMapping("/detail")
	public JsonResult availCouponDetail(@RequestBody AvailCouponDetailParam param) {
        param.initScene();
		AvailCouponDetailVo couponDetail = shop().coupon.getCouponDetail(param);
		return this.success(couponDetail);
	}

    /**
     * 积分兑换优惠券，兑换详情页
     * @param param
     * @return
     */
    @PostMapping("/detail/byScore")
    public JsonResult couponDetailByScore(@RequestBody AvailCouponDetailParam param) {
        Integer userId = wxAppAuth.user().getUserId();
        Integer canUseScore = shop().member.score.getTotalAvailableScoreById(userId);
        param.setUserId(userId);
        AvailCouponDetailVo couponDetail = shop().coupon.getCouponDetailByScore(param);
        couponDetail.setCanUseScore(canUseScore);
        return this.success(couponDetail);
    }

	/**
	 * 用户立即领取优惠券
	 * @return
	 */
	@PostMapping("/get")
	public JsonResult getCoupon(@RequestBody MpGetCouponParam param) {
		Integer userId = wxAppAuth.user().getUserId();
        param.setUserId(userId);
		Byte fetchStatus = shop().mpCoupon.fetchCoupon(param);
        return this.success(fetchStatus);
	}

	/**
	 * 优惠券详情
	 * @param param
	 * @return
	 */
	@PostMapping("/split/detail")
	public JsonResult getSplitCouponDetail(@RequestBody @Validated MpGetSplitCouponParam param) {
		Integer userId = wxAppAuth.user().getUserId();
		param.setUserId(userId);
		MpGetCouponVo couponDetail = shop().mpCoupon.getSplitCouponDetail(param);
		return this.success(couponDetail);
	}

    /**
     * 分享优惠卷
     */
    @PostMapping("/split/share")
	public JsonResult shareSplitCouPon(@RequestBody @Validated MpCouponSnParam param){
        Integer userId = wxAppAuth.user().getUserId();
        param.setUserId(userId);
        shop().mpCoupon.shareSplitCoupon(param);
        return success();
    }

	/**
	 * 获取分享分裂优惠券
	 * @return
	 */
	@PostMapping("/split/get")
	public JsonResult getSplitCoupon(@RequestBody MpGetSplitCouponParam param){
		Integer userId = wxAppAuth.user().getUserId();
		param.setUserId(userId);
		MpGetSplitCouponVo splitCoupon = shop().mpCoupon.getSplitCoupon(param);
		return success(splitCoupon);
	}
    /**
     * 删除优惠券
     * @param param
     * @return
     */
	@PostMapping("/del")
	public JsonResult delCoupon(@RequestBody CouponDelParam param){
	    Integer res = shop().mpCoupon.delCoupon(param);
	    return this.success(res);
    }

    /**
     * 优惠券礼包活动落地页
     * @param param
     * @return
     */
    @PostMapping("/pack")
    public JsonResult couponPack(@RequestBody CouponPackIdParam param){
        return this.success(shop().couponPack.getCouponPackActInfo(param.getPackId(),wxAppAuth.user().getUserId()));
    }

    /**
     * 优惠券礼包结算页跳转
     * @param param
     * @return
     */
    @PostMapping("/pack/tobuy")
    public JsonResult couponPackToBuy(@RequestBody CouponPackIdParam param, HttpServletRequest request){
        return this.success(shop().couponPack.couponPackToBuy(param.getPackId(),wxAppAuth.user().getUserId(),RequestUtil.getIp(request)));
    }

    /**
     * 优惠券礼包结算页跳转
     * @param param
     * @return
     */
    @PostMapping("/pack/order")
    public JsonResult createOrderBefore(@RequestBody @Validated CouponPackOrderBeforeParam param){
        return this.success(shop().couponPack.couponPackOrderConfirm(param,wxAppAuth.user().getUserId()));
    }

    /**
     * 优惠券礼包结算
     *
     * @param param
     * @return
     */
    @PostMapping("/pack/checkout")
    public JsonResult createOrder(@RequestBody @Validated CouponPackOrderParam param, HttpServletRequest request){
        return this.success(shop().couponPackOrder.createOrder(param,wxAppAuth.user().getUserId(), RequestUtil.getIp(request)));
    }
}
