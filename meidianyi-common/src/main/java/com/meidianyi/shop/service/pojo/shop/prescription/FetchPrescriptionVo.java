package com.meidianyi.shop.service.pojo.shop.prescription;

import com.meidianyi.shop.common.pojo.shop.table.PrescriptionDo;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author zhaoxiaodong
 * @create 2020-07-16 14:31
 */

@Data
public class FetchPrescriptionVo extends PrescriptionDo {
    private List<FetchPrescriptionItemVo> dataList;
}
