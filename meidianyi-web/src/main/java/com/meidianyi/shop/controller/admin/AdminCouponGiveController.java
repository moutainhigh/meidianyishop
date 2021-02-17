package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.coupon.give.*;
import com.meidianyi.shop.service.pojo.shop.coupon.hold.CouponHoldListVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 发放优惠券控制器
 * @author liangchen
 * @date 2019年7月29日
 */
@RestController
@RequestMapping("/api/admin/coupon/give")
public class AdminCouponGiveController extends AdminBaseController{
	/**
	 * 定向发放优惠券列表
	 *
	 * @param param 选填项：活动名称
	 * @return 对应的发券活动信息
	 */
	@PostMapping("/list")
	public JsonResult getPageList(@RequestBody CouponGiveListParam param) {

		PageResult<CouponGiveListVo> pageResult = shop().coupon.couponGiveService.getCouponGiveList(param);

		return success(pageResult);
	}
    /**
     * 定向发放预计发放人数
     *
     * @param param 筛选条件
     * @return 人数
     */
    @PostMapping("/userNum")
    public JsonResult getUserNum(@RequestBody CouponGiveGrantParam param) {
        CouponUserNum userNum = shop().coupon.couponGiveService.getGrantUserNum(param);
        return success(userNum);
    }
	/**
	 * 优惠券明细
	 *
	 * @param param 定向发券活动基础信息
	 * @return 定向发券活动明细
	 */
	@PostMapping("/detail")
	public JsonResult getDetail(@RequestBody CouponGiveDetailParam param) {

		PageResult<CouponHoldListVo> pageResult = shop().coupon.couponGiveService.getDetail(param);

		return success(pageResult);
	}

	/**
	 * 发优惠券
	 *
	 * @param param 定向发券活动的筛选条件
	 */
	@PostMapping("/grant")
	public JsonResult insertGrant(@RequestBody CouponGiveGrantParam param) {

		shop().coupon.couponGiveService.insertGrant(param);

		return success();
	}
	/**
	 * 优惠券弹窗
	 *
	 * @param param 选填：优惠券类型和名称
	 * @return 优惠券信息
	 */
	@PostMapping("/pop")
	public JsonResult getPopWindow(@RequestBody CouponGivePopParam param) {
		List<CouponGivePopVo> pageResult = shop().coupon.couponGiveService.popWindows(param);
		return success(pageResult);
	}
	/**
	 * 废除优惠券
	 *
	 * @param param 优惠券id
	 */
	@PostMapping("/delete")
	public JsonResult deleteCoupon(@RequestBody CouponGiveDeleteParam param) {
		shop().coupon.couponGiveService.deleteCoupon(param);
		return success();
	}

}
