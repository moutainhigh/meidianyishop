package com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription;

import com.meidianyi.shop.service.pojo.shop.order.write.operate.AbstractOrderOperateQueryParam;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 审核订单商品和处方
 * @author 孔德成
 * @date 2020/7/10 15:41
 */
@Data
public class PrescriptionAuditParam  extends AbstractOrderOperateQueryParam {
    /**
     * 处方号
     */
    @NotNull
    private String prescriptionCode;
    /**
     * 商品id
     */
    @NotNull
    private List<Integer> recIdList;
    /**
     * 医师id
     */
    @NotNull
    private Integer doctorId;
    /**
     * 是否通过
     */
    @NotNull
    @Max(1)
    @Min(0)
    private Byte isPass;
}
