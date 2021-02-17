package com.meidianyi.shop.service.pojo.wxapp.goods.goods;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * 小程序返回商品基类
 * @author 李晓冰
 * @date 2019年11月08日
 */
@Getter
@Setter
public class  GoodsBaseMp {
    /**是否从ES获取数据*/
    @JsonIgnore
    private Boolean isDisposedByEs = false;
    //************ElasticSearch中的数据**************start
    /**商品ID*/
    protected Integer goodsId;
    protected String goodsName;
    /**商品销售数量*/
    protected Integer goodsSaleNum;
    /**是否是使用默认规格*/
    protected Boolean defaultPrd;
    /**商品数量*/
    protected Integer goodsNumber;
    private Byte isMedical;
    private Byte isRx;
    private String goodsMedicalInstruction;
    //************ElasticSearch中的数据**************end


    @Override
    public String toString() {
        return "GoodsBaseMp{" +
            "isDisposedByEs=" + isDisposedByEs +
            ", goodsId=" + goodsId +
            ", goodsName='" + goodsName + '\'' +
            ", goodsSaleNum=" + goodsSaleNum +
            ", defaultPrd=" + defaultPrd +
            ", goodsNumber=" + goodsNumber +
            ", isMedical=" + isMedical +
            ", isRx=" + isRx +
            ", goodsMedicalInstruction='" + goodsMedicalInstruction + '\'' +
            '}';
    }
}
