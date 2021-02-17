package com.meidianyi.shop.service.pojo.shop.prescription;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 孔德成
 * @date 2020/7/7 18:03
 */
@Data
public class PrescriptionNoParam {
    /**
     * 处方号
     */
    @NotNull
    private String prescriptionCode;

}
