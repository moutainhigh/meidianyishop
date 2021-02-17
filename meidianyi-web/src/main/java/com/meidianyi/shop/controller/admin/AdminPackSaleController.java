package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.packagesale.*;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author huangronggang
 * @date 2019年8月12日 营销活动 一口价
 */
@RestController
@RequestMapping("/api/admin/market/packsale")
public class AdminPackSaleController extends AdminBaseController {
	/**
	 * 一口价活动分页查询
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/list")
	public JsonResult getList(@RequestBody PackSalePageParam param) {
		return success(shop().packSale.getPageList(param));
	}

	/**
	 * 添加打包一口价活动
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/add")
	public JsonResult insert(@RequestBody @Valid PackSaleParam param) {
		int result = shop().packSale.insert(param);
		return result(result);
	}

	/**
	 * 编辑打包一口价活动
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/update")
	public JsonResult update(@RequestBody @Valid PackSaleParam param) {
		int result = shop().packSale.update(param);
		return result(result);

	}

	/**
	 * 删除 打包一口价活动
	 *
	 * @param id
	 * @return
	 */
	@PostMapping("/delete/{id}")
	public JsonResult delete(@PathVariable Integer id) {
		if (id == null) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		int result = shop().packSale.delete(id);
		return result(result);
	}

	/**
	 * 分享打包一口价活动
	 *
	 * @param id
	 * @return
	 */
	@PostMapping("/qrcode/{id}")
	public JsonResult getPackageCode(@PathVariable Integer id) {
		PackSaleShareVo mpQrCode = shop().packSale.getMpQrCode(id);
		return success(mpQrCode);
	}

	/**
	 * 启用活动
	 *
	 * @param id
	 * @return
	 */
	@PostMapping("/enable/{id}")
	public JsonResult enableStatus(@PathVariable Integer id) {
		int result = shop().packSale.enableStatus(id);
		return result(result);
	}

	/**
	 * 停用活动
	 *
	 * @param id
	 * @return
	 */
	@PostMapping("/disable/{id}")
	public JsonResult disableStatus(@PathVariable Integer id) {
		int result = shop().packSale.disableStatus(id);
		return result(result);
	}
	/**
	 * 查指定一口价活动详细
	 * @param id
	 * @return
	 */
	@GetMapping("/select/{id}")
	public JsonResult select(@PathVariable Integer id) {
		PackSaleDefineVo defineVo = shop().packSale.selectDefine(id);
		return success(defineVo);
	}
	/**
	 * 活动订单
	 * @param param
	 * @return
	 */
	@PostMapping("/order")
	public JsonResult getOrderList(@RequestBody @Valid PackSaleOrderPageParam param) {
		PageResult<?> orderList = shop().packSale.getOrderList(param);
		return success(orderList);
	}
	/**
	 * 活动明细
	 * @param param
	 * @return
	 */
	@PostMapping("/detail")
    public JsonResult detail(@RequestBody @Valid PackSaleDetailParam param) {
        PageResult<PackSaleDetailVo> detail = shop().packSale.getPackSaleDetail(param);
        return success(detail);
    }

    private JsonResult result(int result) {
        if (result != 0) {
            return success(JsonResultCode.CODE_SUCCESS);
        }
        return fail(JsonResultCode.CODE_FAIL);
    }

    /**
     * 活动订单
     * 订单导出
     */
    @PostMapping("/order/export")
    public void activityOrderExport(@RequestBody @Valid MarketOrderListParam param, HttpServletResponse response) {
        Workbook workbook = shop().packSale.exportPackSaleOrderList(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.PACKAGE_SALE_ORDER_LIST_FILENAME, OrderConstant.LANGUAGE_TYPE_EXCEL, OrderConstant.LANGUAGE_TYPE_EXCEL) + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook, fileName, response);
    }

}
