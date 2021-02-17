package com.meidianyi.shop.service.pojo.shop.report;

import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.service.pojo.shop.order.report.MedicalOrderReportVo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2020/8/7 15:17
 */
@Data
public class MedicalSalesReportVo {
    public Page page;
    public List<MedicalOrderReportVo> dataList;
    /**
     * 开始时间
     */
    private Timestamp startTime;
    /**
     * 结束时间
     */
    private Timestamp endTime;
}
