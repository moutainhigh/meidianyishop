package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author changle
 * @date 2020/7/15 2:12 下午
 */
@Data
public class RebateGoodsVo {
    /** 是否适用全部商品 0：全部商品；1：部分商品 */
    int allGoods = 1;
    /** 参与返利商品IDs  */
    List<Integer> rebateGoods = new ArrayList<Integer>();
    /** 参与返利的商家分类sortIds */
    List<Integer> rebateSort = new ArrayList<Integer>();
}
