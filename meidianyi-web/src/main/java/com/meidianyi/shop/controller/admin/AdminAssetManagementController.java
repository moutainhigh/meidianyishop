package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.overview.asset.AssetDetailParam;
import com.meidianyi.shop.service.pojo.shop.overview.asset.RevenueProfileParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author liufei
 * @date 2019/8/2
 * @description 资产管理
 */
@RestController
public class AdminAssetManagementController extends AdminBaseController {
    /**
     * 营收概况
     */
    @PostMapping("/api/admin/assetmanagement/revenueprofile")
    public JsonResult revenueprofile(@RequestBody RevenueProfileParam param) {
        if (param.getTradeContent()==0) {
            return success(shop().assetService.revenueprofile(param));
        }
        if (param.getTradeContent()==1){
            return success(shop().assetService.revenueprofileScore(param));
        }
        return success();
    }

    /**
     * 查看明细
     */
    @PostMapping("/api/admin/assetmanagement/assetManageDetail")
    public JsonResult assetManageDetail(@RequestBody AssetDetailParam param) {
        return success(shop().assetService.assetManageDetail(param));
    }

    /**
     * 资产管理明细导出excel
     */
    @PostMapping("/api/admin/assetmanagement/export2Excel")
    public void export2Excel(@RequestBody @Validated AssetDetailParam param, HttpServletResponse response) {
        Workbook workbook = shop().assetService.export2Excel(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.ASSETS_EXPORT_FILE_NAME, "excel", "excel") + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook, fileName, response);
    }
}
