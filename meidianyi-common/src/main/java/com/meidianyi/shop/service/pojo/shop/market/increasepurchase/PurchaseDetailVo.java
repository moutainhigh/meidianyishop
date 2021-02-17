package com.meidianyi.shop.service.pojo.shop.market.increasepurchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.shop.goods.spec.ProductSmallInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagVo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author liufei
 * @date 2019/8/14
 */
@Data
public class PurchaseDetailVo {
    private Integer id;
    /**
     * 活动名称
     */
    private String name;
    /**
     * 活动起始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp startTime;
    /**
     * 活动结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp endTime;
    /**
     * 活动优先级
     */
    private Short level;
    /**
     * 活动信息规则，主商品购满 [] 元可加 [] 元换购
     */
    private List<String> purchaseInfo;
    /**
     * 单笔最大换购数量
     */
    private Short maxChangePurchase;
    /**
     * 主商品id列表字符串
     */
    @JsonIgnore
    private String goodsId;

    /**
     * 主商品信息
     */
    private List<GoodsInfo> mainGoods;
    /**
     * 不同换购规则的加价购商品信息
     */
    private List<ProductSmallInfoVo>[] redemptionGoods;
    /**
     * 换购商品运费策略，0免运费，1使用原商品运费模板
     */
    private Byte redemptionFreight;
    /**
     * 是否给参加活动的用户打标签，1是
     */
    private Byte activityTag;
    /**
     * 参加活动打标签id列表
     */
    @JsonIgnore
    private String activityTagId;
    /**
     * 标签列表
     */
    private List<TagVo> tagList;
}
