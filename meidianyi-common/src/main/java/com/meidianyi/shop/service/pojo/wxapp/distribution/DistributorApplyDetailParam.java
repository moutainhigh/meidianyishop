package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

/**
 * @author  常乐
 * @Date 2019-12-10
 */
@Data
public class DistributorApplyDetailParam {
    private Integer id;
    /**用户ID*/
    private Integer userId;
    /**审核状态 0：审核中；1：通过；2：拒绝；3：没申请过或被删除分销员*/
    private Integer status;
}
