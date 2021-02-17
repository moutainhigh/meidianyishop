package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-03-31 13:49
 **/
@Getter
@Setter
public class GoodsExportColumnVo {
    /**  行数 */
    private Integer rows;

    /**  当前已选列 */
    private List<String> columns;
}
