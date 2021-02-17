package com.meidianyi.shop.service.pojo.wxapp.goods.search;

import lombok.Data;

import java.util.List;

/**
 * 商品搜索-外部跳入时限制条件
 * @author 李晓冰
 * @date 2020年05月09日
 */
@Data
public class GoodsSearchMpOuterParam {

    /**商品分组使用部分（分组全部跳转时传入的都是集合）*/
    private List<Integer> labelIds;
    private List<Integer> brandIds;
    private List<Integer> sortIds;
    /**指定商品id集合*/
    private List<Integer> goodsIds;

    /**用于多商品活动从admin端扫码进入搜索页展示该活动下的商品时使用 activityId*/
    private Integer actId;
    /**会员卡卡号*/
    private String cardNo;
    /**处方码*/
    private String prescriptionCode;
}
