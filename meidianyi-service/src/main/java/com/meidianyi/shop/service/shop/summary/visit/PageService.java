package com.meidianyi.shop.service.shop.summary.visit;

import com.meidianyi.shop.common.foundation.util.PropertiesUtil;
import com.meidianyi.shop.db.shop.tables.records.MpVisitPageRecord;
import com.meidianyi.shop.service.pojo.shop.summary.visit.PageVisitVo;
import com.meidianyi.shop.service.pojo.shop.summary.visit.PageVisitVoItem;
import com.meidianyi.shop.service.pojo.shop.summary.visit.VisitPageParam;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.search.DocValueFormat;
import org.jooq.Result;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.meidianyi.shop.db.shop.tables.MpVisitPage.MP_VISIT_PAGE;

/**
 * 访问页面统计
 *
 * @author 郑保乐
 */
@Service
public class PageService extends BaseVisitService {

    private static final String PAGE_OTHER = "page.other";
    /** 日期标识符 */
    private static final Integer CUSTOM_DAYS = 0;
    /**
     *得到之前的某一天(字符串类型)
     *@param days N天前
     *@return preDay(String)
     */
    public String getDate(Integer days) {
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        //获取当前时间
        Calendar c = Calendar.getInstance();
        //计算指定日期
        c.add(Calendar.DATE, - days);
        Date time = c.getTime();
        //返回格式化后的String日期
        return sdf.format(time);
    }

    /**
     * 计算退出率
     * @param exitNum 退出次数
     * @param visitNum 访问次数
     * @return 退出率保留两位小数
     */
    public Double getExitRate(Integer exitNum,Integer visitNum){
        if (exitNum==null||visitNum==null||visitNum==0){
            return null;
        }else {
            Double exitRate = (exitNum*100.00/(double)visitNum);
            BigDecimal tempAverageNum = BigDecimal.valueOf(exitRate);
            exitRate = tempAverageNum.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            return exitRate;
        }
    }
    public PageVisitVo getPageVisit(VisitPageParam param) {
        //得到时间
        if (!param.getType().equals(CUSTOM_DAYS)){
            param.setStartDate(getDate(param.getType()));
            param.setEndDate(getDate(NumberUtils.INTEGER_ZERO));
        }
        String startDate = param.getStartDate();
        String endDate = param.getEndDate();
        SortField<?> sortField = param.getSortField();
        Result<MpVisitPageRecord> result = getPageVisitResult(startDate, endDate, sortField);
        Result<MpVisitPageRecord> records = result.into(MP_VISIT_PAGE);
        PageVisitVo vo = new PageVisitVo();
        List<PageVisitVoItem> items = records.map(r -> {
            PageVisitVoItem item = new PageVisitVoItem();
            item.setPagePath(r.getPagePath());
            item.setEntryPagePv(String.valueOf(r.getEntrypagePv()));
            item.setExitPagePv(String.valueOf(r.getExitpagePv()));
            item.setPageSharePv(String.valueOf(r.getPageSharePv()));
            item.setPageShareUv(String.valueOf(r.getPageShareUv()));
            item.setPageVisitPv(String.valueOf(r.getPageVisitPv()));
            item.setPageVisitUv(String.valueOf(r.getPageVisitUv()));
            item.setPageStayTimePv(doubleFormat(r.getPageStaytimePv()));
            item.setPageName(pageNameOf(r.getPagePath()));
            item.setExitRate(getExitRate(r.getExitpagePv(),r.getPageVisitPv()));
            return item;
        });
        vo.setList(items);
        vo.setStartDate(startDate);
        vo.setEndDate(endDate);
        return vo;
    }

    private Result<MpVisitPageRecord> getPageVisitResult(
            String startDate, String endDate, SortField<?> orderBy) {
        return db().select(
                MP_VISIT_PAGE.PAGE_PATH,
                DSL.sum(MP_VISIT_PAGE.PAGE_VISIT_PV).as(MP_VISIT_PAGE.PAGE_VISIT_PV),
                DSL.sum(MP_VISIT_PAGE.PAGE_VISIT_UV).as(MP_VISIT_PAGE.PAGE_VISIT_UV),
                DSL.sum(MP_VISIT_PAGE.ENTRYPAGE_PV).as(MP_VISIT_PAGE.ENTRYPAGE_PV),
                DSL.sum(MP_VISIT_PAGE.EXITPAGE_PV).as(MP_VISIT_PAGE.EXITPAGE_PV),
                DSL.sum(MP_VISIT_PAGE.PAGE_SHARE_PV).as(MP_VISIT_PAGE.PAGE_SHARE_PV),
                DSL.sum(MP_VISIT_PAGE.PAGE_SHARE_UV).as(MP_VISIT_PAGE.PAGE_SHARE_UV),
                DSL.sum(MP_VISIT_PAGE.PAGE_STAYTIME_PV).as(MP_VISIT_PAGE.PAGE_STAYTIME_PV))
                .from(MP_VISIT_PAGE)
                .where(MP_VISIT_PAGE.REF_DATE.between(startDate).and(endDate))
                .groupBy(MP_VISIT_PAGE.PAGE_PATH)
                .orderBy(orderBy)
                .fetch().into(MP_VISIT_PAGE);
    }

    /**
     * 获取路径对应的页面名称
     */
    private String pageNameOf(String pagePath) {
        return Optional.ofNullable(pageMap().get(pagePath)).orElse(PAGE_OTHER);
    }

    /**
     * 路径和页面名称对应关系
     */
    private Map<String, String> pageMap() {
        return PropertiesUtil.toMap("visit/pages.properties");
    }
    private Double doubleFormat(Double d){
        BigDecimal b = BigDecimal.valueOf(d);
        return b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
