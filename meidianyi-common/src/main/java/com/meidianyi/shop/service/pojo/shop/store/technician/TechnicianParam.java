package com.meidianyi.shop.service.pojo.shop.store.technician;

import lombok.Data;

import javax.validation.constraints.Positive;

/**
 * @author liufei
 * @date 12/5/19
 */
@Data
public class TechnicianParam {
    @Positive
    private Integer storeId;
    @Positive
    private Integer serviceId;
}
