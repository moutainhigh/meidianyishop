package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketSourceUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.bargain.*;
import com.meidianyi.shop.service.pojo.shop.market.bargain.analysis.BargainAnalysisParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @author 王兵兵
 *
 * 2019年7月24日
 */
@RestController
public class AdminBargainController extends AdminBaseController {

    private static final String LANGUAGE_TYPE_EXCEL= "excel";

	/**
	 * 砍价活动分页查询列表
	 *
	 */
	@PostMapping(value = "/api/admin/market/bargain/list")
	public JsonResult getBargainPageList(@RequestBody @Valid BargainPageListQueryParam param) {
		return success(shop().bargain.getPageList(param));
	}

    /**
     * 砍价活动分页查询列表(装修页弹窗选择)
     *
     */
    @PostMapping(value = "/api/admin/decorate/bargain/list")
    public JsonResult getBargainPageListDialog(@RequestBody @Valid BargainPageListQueryParam param) {
        return success(shop().bargain.getDecoratePageList(param));
    }

	/**
	 *添加 砍价活动
	 *
	 */
	@PostMapping(value = "/api/admin/market/bargain/add")
	public JsonResult addBargain(@RequestBody @Valid BargainAddParam param){
		shop().bargain.addBargain(param);
        return success();
	}

	/**
	 *更新  砍价活动
	 *
	 */
	@PostMapping(value = "/api/admin/market/bargain/update")
	public JsonResult updateBargain(@RequestBody @Valid BargainUpdateParam param) {
		shop().bargain.updateBargain(param);
		return success();
	}

    /**
     *删除  砍价活动
     *
     */
    @PostMapping(value = "/api/admin/market/bargain/del")
    public JsonResult delBargain(@RequestBody @Valid SimpleBargainParam param) {
        shop().bargain.delBargain(param.getId());
        return success();
    }

	/**
	 *取单个砍价活动信息
	 *
	 */
	@PostMapping(value = "/api/admin/market/bargain/get")
	public JsonResult getBargainByIsd(@RequestBody @Valid SimpleBargainParam param) {
        BargainUpdateVo bargain = shop().bargain.getBargainByIsd(param.getId());
		if(bargain != null) {
			return success(bargain);
		}else {
			return fail();
		}
	}

	/**
	 *取砍价取单日可帮助砍价的次数
	 *
	 */
	@GetMapping(value = "/api/admin/market/bargain/cut/times/get")
	public JsonResult getDailyCutTimes() {
		return success(shop().config.bargainCfg.getDailyCutTimes());
	}

	/**
	 *设置砍价取单日可帮助砍价的次数
	 *
	 */
	@GetMapping(value = "/api/admin/market/bargain/cut/times/set")
	public JsonResult setDailyCutTimes(Integer dailyCutTimes) {
		if(shop().config.bargainCfg.setDailyCutTimes(dailyCutTimes) > 0) {
			return success();
		}else {
			return fail();
		}
	}

	/**
	 * 发起砍价的用户列表
	 *
	 */
	@PostMapping(value = "/api/admin/market/bargain/record/list")
	public JsonResult getRecordPageList(@RequestBody @Valid BargainRecordPageListQueryParam param) {
		PageResult<BargainRecordPageListQueryVo> res = shop().bargain.bargainRecord.getRecordPageList(param);
		for(BargainRecordPageListQueryVo vo : res.dataList) {
			vo.setSurplusMoney(shop().bargain.bargainRecord.getBargainRecordSurplusMoney(vo));
		}
		return success(res);
	}

	/**
	 * 导出发起砍价的用户列表
	 *
	 */
	@PostMapping(value = "/api/admin/market/bargain/record/list/export")
	public void exportBargainRecordList(@RequestBody @Valid BargainRecordPageListQueryParam param, HttpServletResponse response) throws IOException {
		Workbook workbook =shop().bargain.bargainRecord.exportBargainRecordList(param,getLang());
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.BARGAIN_RECORD_LIST_FILENAME,LANGUAGE_TYPE_EXCEL) + DateUtils.getLocalDateTime().toString();
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
		workbook.write(response.getOutputStream());
	}

    /**
     * 导出帮忙砍价的用户列表
     *
     */
    @PostMapping(value = "/api/admin/market/bargain/user/list/export")
    public void exportBargainUserList(@RequestBody @Valid BargainUserListQueryParam param, HttpServletResponse response) throws IOException {
        Workbook workbook =shop().bargain.bargainRecord.bargainUser.exportBargainUserList(param,getLang());
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.BARGAIN_USER_LIST_FILENAME,LANGUAGE_TYPE_EXCEL) + DateUtils.getLocalDateTime().toString();
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
        workbook.write(response.getOutputStream());
    }

	/**
	 * 帮忙砍价的用户列表
	 *
	 */
	@PostMapping(value = "/api/admin/market/bargain/record/detail")
	public JsonResult getBargainUserPageList(@RequestBody @Valid BargainUserListQueryParam param) {
		return success(shop().bargain.bargainRecord.bargainUser.getPageList(param));
	}

	/**
	 * 砍价效果分析
	 *
	 */
	@PostMapping(value = "/api/admin/market/bargain/analysis")
	public JsonResult getRecordAnalysisData(@RequestBody @Validated BargainAnalysisParam param) {
		return success(shop().bargain.getBargainAnalysisData(param));
	}

    /**
     * 砍价拉新用明细
     *
     */
    @PostMapping(value = "/api/admin/market/bargain/source")
    public JsonResult getBargainSourceUserList(@RequestBody @Validated MarketSourceUserListParam param) {
        return success(shop().bargain.getBargainSourceUserList(param));
    }

    /**
     * 砍价订单列表
     *
     */
    @PostMapping(value = "/api/admin/market/bargain/order")
    public JsonResult getBargainOrderList(@RequestBody @Validated MarketOrderListParam param) {
        return success(shop().bargain.getBargainOrderList(param));
    }

    /**
     * 取活动分享二维码
     */
    @GetMapping("/api/admin/market/bargain/share")
    public JsonResult getBargainShareCode(Integer id) {
        return success(shop().bargain.getMpQrCode(id));
    }

    /**
     * 活动订单
     * 取将要导出的行数
     */
    @PostMapping("/api/admin/market/bargain/order/export/rows")
    public JsonResult getActivityOrderExportTotalRows(@RequestBody @Valid MarketOrderListParam param) {
        return success(shop().readOrder.marketOrderInfo.getMarketOrderListSize(param, BaseConstant.ACTIVITY_TYPE_BARGAIN));
    }

    /**
     * 活动订单
     * 订单导出
     */
    @PostMapping("/api/admin/market/bargain/order/export")
    public void activityOrderExport(@RequestBody @Valid MarketOrderListParam param, HttpServletResponse response) {
        Workbook workbook =shop().bargain.exportBargainOrderList(param,getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.BARGAIN_ORDER_LIST_FILENAME , OrderConstant.LANGUAGE_TYPE_EXCEL,OrderConstant.LANGUAGE_TYPE_EXCEL) + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook,fileName,response);
    }
}
