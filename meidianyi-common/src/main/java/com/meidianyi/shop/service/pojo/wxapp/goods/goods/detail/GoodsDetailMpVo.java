package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail;

import com.meidianyi.shop.db.shop.tables.records.MrkingVoucherRecord;
import com.meidianyi.shop.service.pojo.shop.config.pledge.PledgeInfo;
import com.meidianyi.shop.service.pojo.wxapp.distribution.GoodsDistributionVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsActivityBaseMp;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsBaseMp;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.gift.GoodsGiftMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.live.RoomDetailMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion.PromotionBase;
import com.meidianyi.shop.service.pojo.wxapp.order.record.GoodsOrderRecordSmallVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 李晓冰
 * @date 2019年11月08日
 */
@Setter
@Getter
public class GoodsDetailMpVo extends GoodsBaseMp {

    //************ElasticSearch中的数据**************start
    /**图片集合*/
    private List<String> goodsImgs = new ArrayList<>();
    private String goodsVideo;
    private String goodsVideoImg;
    private Double goodsVideoSize;
    private Integer videoWidth;
    private Integer videoHeight;
    /**商品最小购买数量*/
    private Integer limitBuyNum;
    /**商品最大购买数量*/
    private Integer limitMaxNum;
    /**是否专享商品*/
    private Byte isExclusive;
    /**商品自定义内容是否在商品详情上方*/
    private Byte isPageUp;
    /**-商品装修模板id*/
    private Integer goodsPageId;
    /**商品详情描述*/
    private String goodsDesc;
    /**商品广告词*/
    private String goodsAd;
    /**品牌id，平台分类id，商家分类id*/
    private Integer brandId;
    private String brandName;
    private Integer catId;
    private Integer sortId;
    /**商品使用的运费模板id*/
    private Integer deliverTemplateId;
    /**商品发货地*/
    private String deliverPlace;
    /**商品重量*/
    private BigDecimal goodsWeight;

    /**商品规格信息*/
    List<GoodsPrdMpVo> products;
    /**是否在售，0否1是*/
    private Byte isOnSale;
    //************ElasticSearch中的数据**************end

    /**用户是否可以购买本商品*/
    private Boolean userCanBuy;
    /**商品所关联的标签（包含通过catId,sortId，allId关联的）*/
    List<String> labels;
    /**商品购买时需要的运费，详情展示时的默认运费*/
    private BigDecimal deliverPrice;
    /**是否已删除，当搜索的商品已删除时，需要前端判断并进行处理*/
    private Byte delFlag;
    /**商品关联的直播间信息*/
    private RoomDetailMpVo roomDetailMpInfo;
    /**是否收藏*/
    private Boolean isCollected;
    /**商品评价信息*/
    private CommentDetailVo comment;
    /**商品赠品*/
    private List<GoodsGiftMpVo> goodsGifts;
    /**分销商品信息*/
    private GoodsDistributionVo goodsDistribution;
    /**相关优惠券*/
    List<CouponDetailMpVo> coupons;
    /**商品专享会员卡*/
    List<MemberCardDetailMpVo> memberCards;
    /** 详情页所指定的营销活动 */
    private GoodsActivityBaseMp activity;
    /**商品最近的五条购买记录 */
    private List<GoodsOrderRecordSmallVo> goodsRecord;

    /**商品活动预告信息*/
    private GoodsActivityAnnounceMpVo activityAnnounceMpVo;

    /**商品促销活动列表*/
    Map<Byte,List<? extends PromotionBase>> promotions = new HashMap<>();
    /**运费地址信息*/
    DeliverFeeAddressDetailVo deliverFeeAddressVo;
    //**********服务承诺
    /**
     * 服务承诺是否开启
     */
    private Integer pledgeSwitch;
    /**
     * 服务承诺信息
     */
    private List<PledgeInfo> pledgeList;

    /**
     * 销量展示开关
     */
    private Byte showSalesNumber;
    /**
     * 客服按钮展示开关
     */
    private Byte customService;

    /**
     * 该商品可以参加的分享有礼活动ID
     */
    private Integer shareAwardId;
    /**商品单位*/
    private String unit;
    @Override
    public String toString() {
        return "GoodsDetailMpVo{" +
            "goodsImgs=" + goodsImgs +
            ", goodsVideo='" + goodsVideo + '\'' +
            ", goodsVideoImg='" + goodsVideoImg + '\'' +
            ", goodsVideoSize=" + goodsVideoSize +
            ", videoWidth=" + videoWidth +
            ", videoHeight=" + videoHeight +
            ", limitBuyNum=" + limitBuyNum +
            ", limitMaxNum=" + limitMaxNum +
            ", isExclusive=" + isExclusive +
            ", isPageUp=" + isPageUp +
            ", goodsPageId=" + goodsPageId +
            ", goodsDesc='" + goodsDesc + '\'' +
            ", goodsAd='" + goodsAd + '\'' +
            ", brandId=" + brandId +
            ", brandName='" + brandName + '\'' +
            ", catId=" + catId +
            ", sortId=" + sortId +
            ", deliverTemplateId=" + deliverTemplateId +
            ", deliverPlace='" + deliverPlace + '\'' +
            ", goodsWeight=" + goodsWeight +
            ", products=" + products +
            ", userCanBuy=" + userCanBuy +
            ", labels=" + labels +
            ", deliverPrice=" + deliverPrice +
            ", delFlag=" + delFlag +
            ", isCollected=" + isCollected +
            ", comment=" + comment +
            ", goodsGifts=" + goodsGifts +
            ", coupons=" + coupons +
            ", memberCards=" + memberCards +
            ", activity=" + activity +
            ", promotions=" + promotions +
            ", pledgeSwitch=" + pledgeSwitch +
            ", pledgeList=" + pledgeList +
            ", unit="+unit+
            '}'+super.toString();
    }
}
