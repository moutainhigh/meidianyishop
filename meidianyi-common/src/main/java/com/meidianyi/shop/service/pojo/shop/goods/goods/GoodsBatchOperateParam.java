package com.meidianyi.shop.service.pojo.shop.goods.goods;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 李晓冰
 * @date 2019年07月10日
 */
@Data
public class GoodsBatchOperateParam {
    private List<Integer> goodsIds;

    /**
     * goodsId 和价格 key商品价格 value对应商品的规格信息集合
     */
    private Map<Integer, List<PrdPriceNumberParam>> goodsPriceNumbers;

    /**
     * 商家分类id
     */
    private Integer sortId;
    /**
     * 运费模板
     */
    private Integer deliverTemplateId;
    /**
     * 限购数量最小值，最大值
     */
    private Integer limitBuyNum;
    private Integer limitMaxNum;

    /**
     * 销售状态
     */
    private Byte isOnSale;
    /**当指定时间上架时该字段是1，否则是0*/
    private Byte saleType;
    /**指定的上架时间*/
    private Timestamp saleTime;

    /**
     * 商品详情模板信息，自定义内容在上面0,商品详情在上1
     */
    private Byte isPageUp;
    /**商品详情模板*/
    private Integer goodsPageId;
    /**
     * 标签
     */
    private List<Integer> goodsLabels;

    private Integer brandId;
    /**
     * 是否会员专享
     */
    private Byte isCardExclusive;
    /**会员专享卡id集合*/
    private List<Integer> cardIds;

    /**发货地址*/
    private String deliverPlace;

    /**
     * 未处理goodsLabel,和shopPrice,goodsNumber，仅处理商品表内数据的变化
     * @return
     */
    public List<GoodsRecord> toUpdateGoodsRecord(){
        if (goodsIds == null || goodsIds.size() == 0) {
            return new ArrayList<>(0);
        }
        List<GoodsRecord> list=new ArrayList<>(goodsIds.size());

        Optional<Byte> isOnSaleOptional = Optional.ofNullable(isOnSale);
        Optional<Byte> saleTypeOptional = Optional.ofNullable(saleType);
        Optional<Timestamp> saleTimeOptional = Optional.ofNullable(saleTime);
        Optional<Integer> sortIdIdOptional = Optional.ofNullable(sortId);
        Optional<Integer> deliverTemplateIdOptional = Optional.ofNullable(deliverTemplateId);
        Optional<Integer> limitBuyNumOptional = Optional.ofNullable(limitBuyNum);
        Optional<Integer> limitMaxNumOptional = Optional.ofNullable(limitMaxNum);
        Optional<Byte> isPageUpOptional = Optional.ofNullable(isPageUp);
        Optional<Integer> goodsPageIdOptional = Optional.ofNullable(goodsPageId);
        Optional<Integer> brandIdOptional = Optional.ofNullable(brandId);
        Optional<Byte> isCardExclusiveOptional = Optional.ofNullable(this.isCardExclusive);
        Optional<String> deliverPlaceOptional = Optional.ofNullable(this.deliverPlace);

        goodsIds.forEach(goodsId->{
            GoodsRecord goodsRecord=new GoodsRecord();
            goodsRecord.setGoodsId(goodsId);

            sortIdIdOptional.ifPresent(goodsRecord::setSortId);
            deliverTemplateIdOptional.ifPresent(goodsRecord::setDeliverTemplateId);
            limitBuyNumOptional.ifPresent(goodsRecord::setLimitBuyNum);
            limitMaxNumOptional.ifPresent(goodsRecord::setLimitMaxNum);
            // 上架的时候将上架时间修改
            if (isOnSaleOptional.isPresent()) {
                goodsRecord.setIsOnSale(isOnSaleOptional.get());
                if (GoodsConstant.ON_SALE.equals(goodsRecord.getIsOnSale())) {
                    Timestamp localDateTime = DateUtils.getLocalDateTime();
                    goodsRecord.setSaleTime(localDateTime);
                    goodsRecord.setSaleType(GoodsConstant.SALE_TYPE_ON_SALE);
                }else {
                    goodsRecord.setSaleType(GoodsConstant.SALE_TYPE_NOT_ON_SALE);
                }

            }
            saleTypeOptional.ifPresent(goodsRecord::setSaleType);
            saleTimeOptional.ifPresent(goodsRecord::setSaleTime);
            isPageUpOptional.ifPresent(goodsRecord::setIsPageUp);
            goodsPageIdOptional.ifPresent(goodsRecord::setGoodsPageId);
            brandIdOptional.ifPresent(goodsRecord::setBrandId);
            isCardExclusiveOptional.ifPresent(goodsRecord::setIsCardExclusive);
            deliverPlaceOptional.ifPresent(goodsRecord::setDeliverPlace);

            list.add(goodsRecord);
        });
        return list;
    }
}
