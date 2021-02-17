package com.meidianyi.shop.service.pojo.shop.prescription.bo;

import com.meidianyi.shop.common.pojo.shop.table.PrescriptionItemDo;
import lombok.Data;

import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/9/8
 **/
@Data
public class PrescriptionItemBo  {
    private String prescriptionCode;
    private String doctorName;
    private Integer doctorId;
    private String doctorCode;
    private Byte settlementFlag;
    private List<PrescriptionItemDo> itemList;

}
