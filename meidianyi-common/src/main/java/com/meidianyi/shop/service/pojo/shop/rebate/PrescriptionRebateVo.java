package com.meidianyi.shop.service.pojo.shop.rebate;

import com.meidianyi.shop.common.pojo.shop.table.PrescriptionItemDo;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionRebateDo;
import lombok.Data;

import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/8/26
 **/
@Data
public class PrescriptionRebateVo extends PrescriptionRebateDo {
    private String doctorName;
    private String mobile;
    private String userName;
    private List<PrescriptionItemDo> medicalList;
}
