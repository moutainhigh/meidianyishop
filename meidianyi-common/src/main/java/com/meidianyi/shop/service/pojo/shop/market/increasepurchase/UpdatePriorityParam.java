package com.meidianyi.shop.service.pojo.shop.market.increasepurchase;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * The type Update priority param.
 *
 * @author liufei
 * @date 9 /5/2019
 */
@Data
public class UpdatePriorityParam {
    /**
     * The Id.活动id
     */
    @NotEmpty
    public Integer id;
    /**
     * The Priority.活动优先级
     */
    @NotEmpty
    public Short priority;
}
