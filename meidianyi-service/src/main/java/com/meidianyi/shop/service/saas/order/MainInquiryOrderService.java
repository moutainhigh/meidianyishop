package com.meidianyi.shop.service.saas.order;

import cn.hutool.core.date.DateUtil;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.dao.main.order.MainInquiryOrderDao;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.order.MainInquiryOrderStatisticsParam;
import com.meidianyi.shop.service.pojo.saas.order.MainInquiryOrderStatisticsVo;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo.InquiryOrderTotalVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/8/14
 **/
@Service
@Slf4j
public class MainInquiryOrderService extends MainBaseService {
    @Autowired
    private MainInquiryOrderDao mainInquiryOrderDao;

    /**
     * 同步新增
     * @param list
     */
    public void inquiryOrderSynchronizeInsert(List<InquiryOrderDo> list){
        mainInquiryOrderDao.inquiryOrderSynchronizeInsert(list);
    }

    /**
     * 同步更新
     * @param list
     * @param shopId
     */
    public void inquiryOrderSynchronizeUpdate(List<InquiryOrderDo> list,Integer shopId){

        mainInquiryOrderDao.inquiryOrderSynchronizeUpdate(list,shopId);
    }

    /**
     * 问诊订单统计报表查询
     * @param param
     * @return
     */
    public PageResult<MainInquiryOrderStatisticsVo> orderStatistics(MainInquiryOrderStatisticsParam param){
        beginAndEndOfDay(param);
        PageResult<MainInquiryOrderStatisticsVo> result=mainInquiryOrderDao.orderStatisticsPage(param);
        return result;
    }

    /**
     * 报表导出
     * @param param
     * @param lang
     * @return
     */
    public Workbook orderStatisticsExport(MainInquiryOrderStatisticsParam param, String lang){
        beginAndEndOfDay(param);
        List<MainInquiryOrderStatisticsVo> list=mainInquiryOrderDao.orderStatistics(param);
        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(list,MainInquiryOrderStatisticsVo.class);
        return workbook;
    }

    /**
     * 报表总数total查询
     * @param param
     * @return
     */
    public InquiryOrderTotalVo orderStatisticsTotal(MainInquiryOrderStatisticsParam param){
        beginAndEndOfDay(param);
        InquiryOrderTotalVo inquiryOrderTotalVo=mainInquiryOrderDao.orderStatisticsTotal(param);
        return inquiryOrderTotalVo;
    }
    /**
     * 日期的时分秒开始和结束
     * @param param
     */
    public void beginAndEndOfDay(MainInquiryOrderStatisticsParam param){
        Timestamp startDate = param.getStartTime();
        Timestamp endDate = param.getEndTime();
        if (startDate != null ) {
            startDate = DateUtil.beginOfDay(startDate).toTimestamp();
            param.setStartTime(startDate);
        }if( endDate != null){
            endDate = DateUtil.endOfDay(endDate).toTimestamp();
            param.setEndTime(endDate);
        }
    }
}
