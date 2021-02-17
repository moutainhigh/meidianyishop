package com.meidianyi.shop.service.pojo.wxapp.market.bargain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2019-12-30 18:07
 **/
@Getter
@Setter
public class BargainRecordSimpleParam {
    @NotNull
    private Integer recordId;
}
