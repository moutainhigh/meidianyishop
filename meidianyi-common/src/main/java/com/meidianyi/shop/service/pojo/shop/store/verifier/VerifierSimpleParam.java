package com.meidianyi.shop.service.pojo.shop.store.verifier;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2019-10-12 10:43
 **/
@Data
public class VerifierSimpleParam {
    @NotNull
    private Integer storeId;
    @NotNull
    private Integer userId;
}
