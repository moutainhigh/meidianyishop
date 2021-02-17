package com.meidianyi.shop.service.pojo.shop.decoration.module;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class ModuleIntegral extends ModuleBase {

    @JsonProperty("list_styles")
    private Byte listStyles=1;

    @JsonProperty("show_goods_price")
    private Boolean showGoodsPrice=true;

    @JsonProperty("integral_goods")
    private List<IntegralGoods> integralGoods;

    @Getter
    @Setter
    public static class IntegralGoods{
        @JsonProperty("goods_id")
        private Integer goodsId;
        @JsonProperty("integral_goods_id")
        private Integer integralGoodsId;
        @JsonProperty("stock_sum")
        private Integer stockSum;
        @JsonProperty("goods_name")
        private String goodsName;
        @JsonProperty("goods_img")
        private String goodsImg;
        @JsonProperty("prd_price")
        private BigDecimal prdPrice;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
        @JsonProperty("start_time")
        private Timestamp startTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
        @JsonProperty("end_time")
        private Timestamp endTime;
        @JsonProperty("is_on_sale")
        private Byte isOnSale;
        @JsonProperty("act_del_flag")
        private Byte actDelFlag;
        @JsonProperty("goods_is_delete")
        private Byte goodsIsDelete;

        /**
         * 实时状态。0正常可用，1商品已删除，2活动已删除，3活动已停用，4活动未开始，5活动已过期
         */
        private Byte tip;
        /**最小的积分 */
        private Integer score;
        /** 最小积分的对应金额 */
        private BigDecimal money;
        /**商品库存 */
        private Integer goodsNumber;
    }
}
