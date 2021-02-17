package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.coupon.*;
import com.meidianyi.shop.service.pojo.shop.coupon.hold.CouponHoldListVo;
import com.meidianyi.shop.service.shop.ShopApplication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 优惠券管理
 * @author 常乐
 * 2019年7月16日
 */
@RestController
@RequestMapping("/api")
public class AdminCouponController extends AdminBaseController{
//
//	@Override
//	protected ShopApplication shop() {
//		return saas.getShopApp(471752);
//	}
	/**
	 * 创建优惠券
	 * @param couponInfo
	 * @return
	 */
	@PostMapping("/admin/coupon/add")
	public JsonResult couponAdd(@RequestBody CouponParam couponInfo) {
		Boolean result = shop().coupon.couponAdd(couponInfo);
		if(result) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}

	/**
	 * 优惠券分页列表
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/coupon/list")
	public JsonResult couponList(@RequestBody CouponListParam param) {
		PageResult<CouponListVo> couponList = shop().coupon.getCouponList(param);
		return this.success(couponList);
	}

	/**
	 *获得所有可发放优惠券 -下拉框
	 * @return
	 */
	@PostMapping("/admin/coupon/all")
	public JsonResult getCouponAll (@RequestBody CouponAllParam param){
		List<CouponAllVo> couponAll = shop().coupon.getCouponAll(param);
		return this.success(couponAll);
	}

	/**
	 * 单条优惠券信息
	 * @param couponId
	 * @return
	 */
	@GetMapping("/admin/coupon/update/info")
	public JsonResult oneCouponInfo(Integer couponId) {
		return this.success(shop().coupon.getOneCouponInfo(couponId));
	}

	/**
	 * 保存编辑信息
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/coupon/update/save")
	public JsonResult couponInfoSave(@RequestBody CouponParam param) {
		Boolean result = shop().coupon.saveCouponInfo(param);
		return this.success(result);
	}

	/**
	 * 停用优惠券
	 * @param couponId
	 * @return
	 */
	@GetMapping("/admin/coupon/pause")
	public JsonResult couponPause(@NotNull Integer couponId) {
		System.out.println(111);
		boolean result = shop().coupon.couponPause(couponId);
		if(result) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}

	/**
	 * 启用优惠券
	 * @param couponId
	 * @return
	 */
	@GetMapping("/admin/coupon/open")
	public JsonResult couponOpen(@NotNull Integer couponId) {
		boolean result = shop().coupon.couponOpen(couponId);
		if(result) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}

	/**
	 * 删除优惠券（假删除）
	 * @param couponId
	 * @return
	 */
	@GetMapping("/admin/coupon/delete")
	public JsonResult couponDel(Integer couponId) {
		boolean result = shop().coupon.couponDel(couponId);
		if(result) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}

	/**
	 * 优惠券领取明细
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/coupon/get/detail")
	public JsonResult couponGetDetail(@RequestBody CouponGetDetailParam param) {
		PageResult<CouponHoldListVo> detail = shop().coupon.getDetail(param);
		return this.success(detail);
	}

    /**
     * 领取分裂优惠券用户详情列表
     * @param param
     * @return
     */
	@PostMapping("/admin/coupon/get/user/detail")
	public JsonResult getSplitCouponUserDetail(@RequestBody CouponGetDetailParam param){
        PageResult<CouponHoldListVo> splitCoupinUserDetail = shop().coupon.getSplitCoupinUserDetail(param);
        return this.success(splitCoupinUserDetail);
    }

    /**
     * 删除用户优惠券
     * @param id
     * @return
     */
	@GetMapping("/admin/avail/coupon/del")
	public JsonResult availCouponDel(Integer id){
        boolean res = shop().coupon.availCouponDel(id);
        return this.success(res);
    }

    /**
     * 	取活动分享二维码
     */
    @GetMapping("/admin/coupon/share")
    public JsonResult getCouponShareCode(Integer couponId) {
        return this.success(shop().coupon.getMpQrCode(couponId));
    }
}
