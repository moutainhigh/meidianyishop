package com.meidianyi.shop.service.pojo.wxapp.goods.sort;

import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年10月17日
 */
@Data
public class SortGroupByParentParam {
    private Byte isRecommend;
    private List<Integer> parentIds;
    private List<Integer> sortIds;
}
