package com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.audit;

import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockField;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.AbstractOrderOperateQueryParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 审核
 * @author 孔德成
 * @date 2020/7/29 10:24
 */
@Data
public class AuditOrderGoodsParam extends AbstractOrderOperateQueryParam {
    /**
     * 订单id
     */
    @NotNull
    @RedisLockField
    private Integer orderId;
    /**
     * 订单号
     */
    @NotNull
    private String orderSn;
    /**
     * 医师id
     */
    private Integer doctorId;
    /**
     * 处方号
     */
    @NotNull
    private String prescriptionOldCode;
    /**
     * 审核状态 2失败 1成功
     */
    @NotNull
    private Byte auditStatus;
    /**
     * 医嘱信息
     */
    private String doctorAdvice;
    /**
     * 驳回类型
     */
    private Byte reasonType;
    /**
     * 驳回原因
     */
    private String reasonDesc;

}
