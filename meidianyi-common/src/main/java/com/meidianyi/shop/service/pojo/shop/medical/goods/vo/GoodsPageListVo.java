package com.meidianyi.shop.service.pojo.shop.medical.goods.vo;

import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.shop.medical.goods.entity.GoodsEntity;
import com.meidianyi.shop.service.pojo.shop.medical.goods.entity.GoodsMedicalInfoEntity;
import com.meidianyi.shop.service.pojo.shop.medical.label.vo.GoodsLabelVo;
import com.meidianyi.shop.service.pojo.shop.medical.sku.vo.GoodsSpecProductGoodsPageListVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品分页返回体
 * @author 李晓冰
 * @date 2020年07月08日
 */
@Data
@NoArgsConstructor
public class GoodsPageListVo {
    private Integer goodsId;
    private String goodsName;
    private String goodsImg;
    private String goodsSn;
    private Integer goodsNumber;
    private Integer goodsSaleNum;
    private Integer brandId;
    private String brandName;
    private Integer sortId;
    private String sortName;
    private BigDecimal shopPrice;
    private Byte source;
    private Byte isMedical;
    private Byte isDefaultProduct;
    private String goodsCommonName;
    private String goodsAliasName;
    private String goodsQualityRatio;
    private Byte isRx;
    private String storeCode;
    private Byte hisStatus;
    private Byte storeStatus;
    private BigDecimal hisPrice;
    private BigDecimal storePrice;
    private Integer fromHisId;
    private Integer fromStoreId;
    private String goodsProductionEnterprise;

    private List<GoodsSpecProductGoodsPageListVo> goodsSpecProducts;

    private List<GoodsLabelVo> goodsPointLabels = new ArrayList<>(0);
    private List<GoodsLabelVo> goodsNormalLabels = new ArrayList<>(0);

    public GoodsPageListVo(GoodsEntity goodsEntity) {
        goodsId = goodsEntity.getGoodsId();
        goodsName = goodsEntity.getGoodsName();
        goodsImg = goodsEntity.getGoodsImg();
        goodsSn = goodsEntity.getGoodsSn();
        goodsNumber = goodsEntity.getGoodsNumber();
        goodsSaleNum = goodsEntity.getGoodsSaleNum();
        brandId = goodsEntity.getBrandId();
        sortId = goodsEntity.getSortId();
        isMedical = goodsEntity.getIsMedical();
        isDefaultProduct = goodsEntity.getIsDefaultProduct();
        shopPrice=goodsEntity.getShopPrice();
        storeCode = goodsEntity.getStoreCode();
        hisStatus = goodsEntity.getHisStatus();
        storeStatus = goodsEntity.getStoreStatus();
        fromHisId = goodsEntity.getFromHisId();
        fromStoreId = goodsEntity.getFromStoreId();
        source =goodsEntity.getSource();
        if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(isMedical)) {
            GoodsMedicalInfoEntity goodsMedicalInfo = goodsEntity.getGoodsMedicalInfo();
            goodsCommonName = goodsMedicalInfo.getGoodsCommonName();
            goodsAliasName = goodsMedicalInfo.getGoodsAliasName();
            goodsQualityRatio = goodsMedicalInfo.getGoodsQualityRatio();
            isRx = goodsMedicalInfo.getIsRx();
            goodsProductionEnterprise = goodsMedicalInfo.getGoodsProductionEnterprise();
        }
    }
}
