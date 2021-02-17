package com.meidianyi.shop.service.pojo.shop.anchor;

import lombok.Data;

import java.util.List;

/**
 * @author 孔德成
 * @date 2020/9/14 9:19
 */
@Data
public class AnchorPointsPerReportVo {

    List<AnchorPointsReportVo> prescriptionReport;

    List<AnchorPointsReportVo> deviceReport;

}
