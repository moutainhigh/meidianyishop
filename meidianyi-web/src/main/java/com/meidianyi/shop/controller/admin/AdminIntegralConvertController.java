package com.meidianyi.shop.controller.admin;

import java.util.List;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.market.integralconvert.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketSourceUserListParam;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 积分兑换控制器
 * @author liangchen
 * @date 2019年8月14日
 */
@RestController
@RequestMapping("/api/admin/market/integral/convert")
public class AdminIntegralConvertController extends AdminBaseController{
    /**
     * 积分兑换弹窗
     * @param param 商品名称 是否上架
     * @return 活动商品信息
     */
    @PostMapping("/pop")
    public JsonResult getPopList(@RequestBody PopListParam param){
        PageResult<PopListVo> result = shop().integralConvertService.getPopList(param);
        return success(result);
    }
    /**
     * 更新已装修的积分兑换活动
     * @param param 已装修的积分兑换活动id集合
     * @return 活动商品信息
     */
    @PostMapping("/update/goods")
    public JsonResult updateGoodsInfo(@RequestBody PopListParam param){
        List<PopListVo> result = shop().integralConvertService.updateActInfo(param);
        return success(result);
    }
	/**
	 * 查询积分兑换活动分页列表
	 *
	 * @param param 活动状态 默认查进行中
	 * @return 活动分页信息
	 */
	@PostMapping("/list")
	public JsonResult getList(@RequestBody IntegralConvertListParam param) {

		IntegralConvertScoreVo pageResult = shop().integralConvertService.getList(param);
		return success(pageResult);

	}

	/**
	 * 停用或启用活动
	 *
	 * @param param 活动id
	 * @return void
	 */
	@PostMapping("/switch")
	public JsonResult startOrStop(@RequestBody IntegralConvertId param) {

		shop().integralConvertService.startOrStop(param);
		return success();

	}

	/**
	 * 删除单个活动
	 *
	 * @param param 活动id
	 * @return void
	 */
	@PostMapping("/delete")
	public JsonResult deleteAct(@RequestBody IntegralConvertId param) {
		shop().integralConvertService.deleteAct(param);
		return success();
	}

	/**
	 * 积分兑换用户列表
	 *
	 * @param param 用户名or手机号
	 * @return 用户参与信息
	 */
	@PostMapping("/user")
	public JsonResult userList(@RequestBody IntegralConvertUserParam param) {

		PageResult<IntegralConvertUserVo> pageResult = shop().integralConvertService.userList(param);
		return success(pageResult);
	}

	/**
	 * 添加积分兑换活动
	 *
	 * @param param 活动详情
	 * @return void
	 */
	@PostMapping("/add")
	public JsonResult addAction(@RequestBody IntegralConvertAddParam param) {

		shop().integralConvertService.addAction(param);
		return success();
	}

	/**
	 * 返回指定商品规格详情
	 *
	 * @param param 商品id
	 * @return 对应规格详情
	 */
	@PostMapping("/product")
	public JsonResult getProduct(@RequestBody IntegralConvertGoodsParam param) {

		List<IntegralConvertGoodsVo> vo = shop().integralConvertService.getProduct(param);
		return success(vo);
	}

	/**
	 * 查询指定积分兑换活动详情
	 *
	 * @param param 活动id
	 * @return 活动详情
	 */
	@PostMapping("/select")
	public JsonResult selectOne(@RequestBody IntegralConvertSelectParam param) {

		IntegralConvertSelectVo vo = shop().integralConvertService.selectOne(param);
		return success(vo);
	}
	/**
	 * 编辑积分兑换活动
	 *
	 * @param param 活动详情
	 * @return void
	 */
	@PostMapping("/update")
	public JsonResult updateAction(@RequestBody IntegralConvertAddParam param) {

		shop().integralConvertService.updateAction(param);
		return success();
	}
	 /**
     * 新增用户列表
     *
     * @param param
     * @return JsonResult
     */
    @PostMapping("/newuser")
    public JsonResult integralNewUserList(@RequestBody MarketSourceUserListParam param) {
        PageResult<IntegralNewUser> pageResult = shop().integralConvertService.integralNewUserList(param);
        return success(pageResult);
    }

    /**
     * 查看积分兑换订单
     *
     * @param param
     * @return JsonResult
     */
    @PostMapping("/order")
    public JsonResult integralOrderList(@RequestBody MarketOrderListParam param) {
        PageResult<IntegralConvertOrderVo> pageList = shop().integralConvertService.integralOrderList(param);
        return success(pageList);
    }

    private static final String LANGUAGE_TYPE_EXCEL = "excel";
    /**
     * 订单详情导出表格
     * @param param
     * @return
     */
    @PostMapping("/order/export")
    public void orderExport(@Valid @RequestBody MarketOrderListParam param, HttpServletResponse response) {
        Workbook workbook = shop().integralConvertService.orderExport(param,getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.INTEGRAL_MALL_EXPORT, LANGUAGE_TYPE_EXCEL,LANGUAGE_TYPE_EXCEL)+ DateUtils.getLocalDateTime().toString();
        export2Excel(workbook, fileName, response);
    }
    /**
     * 用户列表导出表格
     * @param param
     * @return
     */
    @PostMapping("/user/export")
    public void userExport(@Validated @RequestBody IntegralConvertUserParam param, HttpServletResponse response) {
        Workbook workbook = shop().integralConvertService.userExport(param,getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.INTEGRAL_MALL_EXPORT_USER, LANGUAGE_TYPE_EXCEL,LANGUAGE_TYPE_EXCEL)+ DateUtils.getLocalDateTime().toString();
        export2Excel(workbook, fileName, response);
    }
    /**
     * 分享
     * @param param 活动id
     * @return 二维码信息
     */
    @PostMapping("/share")
    public JsonResult share(@RequestBody @Valid IntegralConvertId param){
        return success(shop().integralConvertService.getMpQrCode(param));
    }
}
