package com.meidianyi.shop.service.pojo.wxapp.goods.brand;

import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年10月17日
 */
public class GoodsBrandMpPinYinVo {
    private String character;
    private List<GoodsBrandMpVo> goodsBrands;


    public GoodsBrandMpPinYinVo(){}

    public GoodsBrandMpPinYinVo(String character,List<GoodsBrandMpVo> goodsBrands){
        this.character = character;
        this.goodsBrands = goodsBrands;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public List<GoodsBrandMpVo> getGoodsBrands() {
        return goodsBrands;
    }

    public void setGoodsBrands(List<GoodsBrandMpVo> goodsBrands) {
        this.goodsBrands = goodsBrands;
    }
}
