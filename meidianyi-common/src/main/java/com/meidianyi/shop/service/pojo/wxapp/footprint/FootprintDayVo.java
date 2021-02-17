package com.meidianyi.shop.service.pojo.wxapp.footprint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpVo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/11/8 10:40
 */
@Setter
@Getter
public class FootprintDayVo {

    /**
     * 商品id
     */
    @JsonIgnore
    private Integer goodsId;
    /**
     * 时间 yyyy-MM-dd
     */
    private String date;
    /**
     * 商品列表
     */
    private List<GoodsListMpVo> goodsList =new ArrayList<>();
}
