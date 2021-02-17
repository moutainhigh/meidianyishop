package com.meidianyi.shop.service.pojo.shop.order.audit;

import com.meidianyi.shop.service.pojo.shop.order.write.operate.AbstractOrderOperateQueryParam;
import lombok.Data;

/**
 * @author 孔德成
 * @date 2020/7/17 15:49
 */
@Data
public class AuditExternalParam extends AbstractOrderOperateQueryParam {

    private String orderSn;
    private Byte isAudit;
    private String message;
    private String prescriptionCode;

}
