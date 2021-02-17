package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.PortraitParam;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.ProvinceParam;
import com.meidianyi.shop.service.pojo.shop.summary.visit.VisitDistributionParam;
import com.meidianyi.shop.service.pojo.shop.summary.visit.VisitExportParam;
import com.meidianyi.shop.service.pojo.shop.summary.visit.VisitPageParam;
import com.meidianyi.shop.service.pojo.shop.summary.visit.VisitStatisticsParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 概况
 *
 * @author 郑保乐
 * @date 2019-07-11
 */
@RestController
public class AdminSummaryController extends AdminBaseController {

    @GetMapping("/api/admin/summary/test")
    public void testAddDailyVisit() {
        shop().amount.addTestDailyVisit();
    }

    @PostMapping("/api/admin/summary/visit/amount")
    public JsonResult getVisitStatistics(@Valid @RequestBody VisitStatisticsParam param) {
        return success(shop().amount.getVisitStatistics(param));
    }

    @PostMapping("/api/admin/summary/visit/distribution")
    public JsonResult getVisitDistribution(@Valid @RequestBody VisitDistributionParam param) {
        return i18nSuccess(shop().distribution.getVisitDistribution(param));
    }

    @PostMapping("/api/admin/summary/source/distribution")
    public JsonResult getSourceAnalysis(@Valid @RequestBody VisitDistributionParam param) {
        return success(shop().distribution.querySourceAnalysis(param));
    }

    @GetMapping("/api/admin/summary/source/selectoption")
    public JsonResult getSelectOption() {
        return i18nSuccess(shop().distribution.getSelectOption());
    }

    @PostMapping("/api/admin/summary/visit/retain")
    public JsonResult getVisitDistribution(@Valid @RequestBody VisitStatisticsParam param) {
        return success(shop().retain.getAccessRetain(param));
    }

    @PostMapping("/api/admin/summary/visit/page")
    public JsonResult getVisitPage(@Valid @RequestBody VisitPageParam param) {
        return i18nSuccess(shop().page.getPageVisit(param));
    }

    @PostMapping("/api/admin/summary/portrait/portrait")
    public JsonResult getUserPortrait(@Valid @RequestBody PortraitParam param) {
        return success(shop().portrait.getPortrait(param));
    }

    @PostMapping("/api/admin/summary/portrait/province")
    public JsonResult getProvincePortrait(@Valid @RequestBody ProvinceParam param) {
        return success(shop().portrait.getProvincePortrait(param));
    }
    private static final String LANGUAGE_TYPE_EXCEL = "excel";
    /**
     * 统计数据导出表格
     * @param param
     * @return
     */
    @PostMapping("/api/admin/summary/visit/export")
    public void export(@Valid @RequestBody VisitExportParam param, HttpServletResponse response) {
        Workbook workbook = shop().amount.getVisitExportVo(param,getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.VIST_EXPORT_FILE_NAME, LANGUAGE_TYPE_EXCEL)+ DateUtils.getLocalDateTime().toString();
        export2Excel(workbook, fileName, response);
    }

    /**
     * 医药销售报表
     */
    @PostMapping("/api/admin/summary/medical/sell")
    public  void getMedicalPortrait(){

    }

}

