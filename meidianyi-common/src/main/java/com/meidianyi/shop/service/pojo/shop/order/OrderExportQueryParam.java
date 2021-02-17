package com.meidianyi.shop.service.pojo.shop.order;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2019-10-15 15:17
 **/
public class OrderExportQueryParam extends OrderPageListQueryParam {
    @NotNull
    @Setter
    @Getter
    private Integer exportRowStart;
    @NotNull
    @Setter
    @Getter
    private Integer exportRowEnd;
}
