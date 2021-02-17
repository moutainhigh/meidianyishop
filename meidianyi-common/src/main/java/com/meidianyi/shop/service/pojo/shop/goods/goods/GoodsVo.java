package com.meidianyi.shop.service.pojo.shop.goods.goods;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelSelectListVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年07月16日
 */
@Getter
@Setter
public class GoodsVo extends Goods {

    @JsonIgnore
    private List<Integer> goodsLabels;
    private List<GoodsLabelSelectListVo> goodsLabelPointListVos;
    private List<GoodsLabelSelectListVo> goodsLabelNormalListVos;
    private String brandName;
    private String sortName;
    private String goodsPageName;
    private String goodsImgPath;
    private List<String> goodsImgsPath;
    /**直播间名称*/
    private String roomName;
    /**
     * 商品视频全路径
     */
    private String goodsVideoUrl;

    /**
     * 商品视频快照全路径
     */
    private String goodsVideoImgUrl;

    @Override
    public List<Integer> getGoodsLabels() {
        return goodsLabels;
    }

    @Override
    public void setGoodsLabels(List<Integer> goodsLabels) {
        this.goodsLabels = goodsLabels;
    }
}
