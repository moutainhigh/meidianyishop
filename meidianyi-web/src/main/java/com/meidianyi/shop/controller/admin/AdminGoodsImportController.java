package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelUtil;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.ImportResultCodeWrap;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuExcelImportModel;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuExcelImportParam;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuImportListParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Collections;

/**
 * @author 李晓冰
 * @date 2020年03月19日
 */
@RestController
@Slf4j
public class AdminGoodsImportController extends AdminBaseController{

    @PostMapping("/api/admin/goods/vpu/excel/import")
    public JsonResult goodsVpuExcelImport(GoodsVpuExcelImportParam param){
        MultipartFile file = param.getFile();

        if (file == null) {
            return fail();
        }

        logger().debug("微铺宝excel商品导入："+file.getName());
        ExcelTypeEnum excelTypeEnum = ExcelUtil.checkFile(file);
        if (excelTypeEnum == null) {
            logger().debug("导入格式错误!");
            return fail(JsonResultCode.CODE_EXCEL_ERRO);
        }
        param.setLang(getLang());
        param.setExcelTypeEnum(excelTypeEnum);

        ImportResultCodeWrap importResultCodeWrap = shop().goodsImportService.goodsVpuExcelImport(param);

        if (JsonResultCode.CODE_SUCCESS.equals(importResultCodeWrap.getResultCode())) {
            return success(importResultCodeWrap.getBatchId());
        } else {
            return fail(importResultCodeWrap.getResultCode());
        }
    }

    @GetMapping("/api/admin/goods/vpu/excel/download/finish/{batchId}")
    public JsonResult isBatchFinish(@PathVariable Integer batchId){
        boolean finish = shop().goodsImportRecordService.isFinish(batchId);
        return success(finish);
    }

    @PostMapping("/api/admin/goods/vpu/excel/operate/list")
    public JsonResult getOperateList(@RequestBody GoodsVpuImportListParam param){
        return success(shop().goodsImportRecordService.getGoodsVpuImportList(param));
    }

    /**
     * 模板下载
     * @return
     */
    @GetMapping("/api/admin/goods/vpu/excel/download/module")
    public void downloadExcelModule(HttpServletResponse response){
        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(getLang(), workbook);
        GoodsVpuExcelImportModel modelExample = GoodsVpuExcelImportModel.createModelExample();
        excelWriter.writeModelList(Collections.singletonList(modelExample),GoodsVpuExcelImportModel.class);
        export2Excel(workbook, "goods", response);
    }

    @GetMapping("/api/admin/goods/vpu/excel/download/fail/data/{batchId}")
    public void downloadExcelImportFailData(@PathVariable Integer batchId, HttpServletResponse response){
        Workbook workbook = shop().goodsImportRecordService.downloadFailData(batchId, getLang());
        Timestamp operateTime = shop().goodsImportRecordService.getOperateTime(batchId);
        String time = DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE, operateTime);
        String fileName = time + "_" + batchId + "_goods";
        export2Excel(workbook, fileName, response);
    }
}
