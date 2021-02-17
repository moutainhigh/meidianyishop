package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-10-21 18:24
 **/
@Setter
@Getter
public class GoodsExportParam extends GoodsPageListParam{
    @NotNull
    private Integer exportRowStart;
    @NotNull
    private Integer exportRowEnd;

    @NotNull
    private List<String> columns;

}
