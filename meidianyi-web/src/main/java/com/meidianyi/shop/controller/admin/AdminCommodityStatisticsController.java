package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.overview.Tuple2;
import com.meidianyi.shop.service.pojo.shop.overview.commodity.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

import static com.meidianyi.shop.service.pojo.shop.order.OrderExportVo.LANGUAGE_TYPE_EXCEL;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

/**
 * @author liufei
 * date 2019/7/19
 * 概览-商品统计
 */
@RestController
public class AdminCommodityStatisticsController extends AdminBaseController {

    /**
     * 商品概览
     *
     * @param param the param
     * @return json result
     */
    @PostMapping("/api/admin/commoditystatistics/productoverview")
    public JsonResult productOverview(@RequestBody @Validated ProductOverviewParam param) {
        ProductOverviewVo vo;
        if (param.getBrandId() > 0 || param.getSortId() > 0 || param.getLabelId() > 0) {
            if (param.getDynamicDate() > 0) {
                formatDate(param);
            }
            vo = shop().statisticsService.conditionOverview(param);
        } else if (param.getDynamicDate() > 0) {
            vo = shop().statisticsService.fixedDayOverview(param);
        } else {
            vo = shop().statisticsService.customizeDayOverview(param);
        }
        return success(vo);
    }

    /**
     * 将指定时间转化为自定义时间供后续统一处理
     */
    private void formatDate(ProductOverviewParam param) {
        param.setStartTime(Timestamp.valueOf(LocalDate.now().minusDays(param.getDynamicDate()).atStartOfDay()));
        param.setEndTime(DateUtils.getLocalDateTime());
        logger().info("统计开始时间：{}",param.getStartTime());
        logger().info("统计结束时间：{}",param.getEndTime());
    }

    /**
     * 商品效果
     *
     * @param param the param
     * @return json result
     */
    @PostMapping("/api/admin/commoditystatistics/producteffect")
    public JsonResult productEffect(@RequestBody @Validated ProductEffectParam param) {
        PageResult<ProductEffectVo> vo;
        if (param.getDynamicDate() > 0) {
            vo = shop().statisticsService.fixedDayEffect(param);
        } else {
            vo = shop().statisticsService.customizeDayEffect(param);
        }
        return success(vo);
    }

    /**
     * 商品统计综合接口
     *
     * @param param the param
     * @return json result
     */
    @PostMapping("/api/admin/commoditystatistics/defaultOverview")
    public JsonResult defaultOverview(@RequestBody @Validated ProductEffectParam param) {
        return success(new Tuple2<>(productOverview(param).getContent(), productEffect(param).getContent()));
    }

    /**
     * 商品效果导出excel
     *
     * @param param    the param
     * @param response the response
     * @return
     */
    @PostMapping("/api/admin/commoditystatistics/export2Excel")
    public void export2Excel(@RequestBody @Validated ProductEffectParam param, HttpServletResponse response) {
        Workbook workbook = shop().statisticsService.export2Excel(param);
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.GOODS_EFFECT_FILE_NAME, LANGUAGE_TYPE_EXCEL, "messages");
        export2Excel(workbook, fileName, response);
    }

    /**
     * Product ranking.商品排行
     *
     * @param param the param
     */
    @PostMapping("/api/admin/commoditystatistics/productRanking")
    public JsonResult productRanking(@RequestBody @Validated RankingParam param) {
        if (Objects.isNull(param.getStartTime()) || Objects.isNull(param.getEndTime())) {
            // 默认获取最近30天的数据
            param.defaultValue();
        }
        return success(shop().statisticsService.getGoodsRanking(param));
    }

    @GetMapping("/api/admin/commoditystatistics/getDate/{unit}")
    public JsonResult getDate(@PathVariable Byte unit) {
        RankingParam param = new RankingParam();
        LocalDate now = LocalDate.now();
        switch (unit) {
            case 7:
                param.setStartTime(Date.valueOf(now.minusDays(7)));
                break;
            case 30:
                param.setStartTime(Date.valueOf(now.minusDays(30)));
                break;
            default:
                param.setStartTime(Date.valueOf(now.minusDays(1)));
                break;
        }
        param.setEndTime(Date.valueOf(now));
        return success(param);
    }

    /**
     * 商品排行导出excel
     *
     * @param param    the param
     * @param response the response
     * @return
     */
    @PostMapping("/api/admin/commoditystatistics/rankExport")
    public void rankExport(@RequestBody @Validated RankingParam param, HttpServletResponse response) {
        if (Objects.isNull(param.getStartTime()) || Objects.isNull(param.getEndTime())) {
            // 默认获取最近30天的数据
            param.defaultValue();
        }
        if (Objects.isNull(param.getUnit())) {
            // 默认粒度为天
            param.setUnit(BYTE_ZERO);
        }
        String message;
        if (BYTE_ZERO.equals(param.getFlag())) {
            message = JsonResultMessage.GOODS_RANKING_SALES_TOP10;
        } else {
            message = JsonResultMessage.GOODS_RANKING_SALES_ORDER_TOP10;
        }
        Workbook workbook = shop().statisticsService.rankExport(param);
        String fileName = Util.translateMessage(getLang(), message, LANGUAGE_TYPE_EXCEL, "messages");
        export2Excel(workbook, fileName, response);
    }
}
