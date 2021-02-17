package com.meidianyi.shop.service.pojo.shop.medical.sku.entity;

import cn.hutool.core.util.StrUtil;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Data
public class GoodsSpecProductEntity {
    private Integer prdId;
    private Integer goodsId;
    private String prdSn;
    private BigDecimal prdPrice;
    private BigDecimal prdMarketPrice;
    private BigDecimal prdCostPrice;
    private Integer prdNumber;
    private String prdCodes;
    private String prdSpecs;
    private String prdDesc;
    private String prdImg;
    private BigDecimal prdWeight;
    private Byte delFlag;
    private Timestamp createTime;

    public void calculatePrdSpecs(Map<String, SpecEntity> specNameMap) {
        // 没有规格描述可以使用
        if (StrUtil.isBlank(prdDesc)) {
            return;
        }
        String[] specKvs = prdDesc.split(MedicalGoodsConstant.PRD_DESC_SPLITER);
        if (specKvs.length < 1) {
            throw new IllegalArgumentException("规格组描述信息错误：" + prdDesc);
        }

        StringBuilder idStrBuilder = new StringBuilder();

        for (String specKv : specKvs) {
            String[] split = specKv.split(MedicalGoodsConstant.SPEC_NAME_VAL_SPLITER);
            if (split.length != 2) {
                throw new IllegalArgumentException(String.format("规格组%s名值信息错误：%s", prdDesc, specKv));
            }
            String key = split[0];
            String val = split[1];
            SpecEntity specEntity = specNameMap.get(key);
            // 没有匹配的规格组
            if (specEntity == null) {
                throw new IllegalArgumentException("规格组描述信息错误：" + prdDesc);
            }
            if (idStrBuilder.length() != 0) {
                idStrBuilder.append(MedicalGoodsConstant.PRD_SPEC_SPLITER);
            }
            idStrBuilder.append(specEntity.getSpecId()).append(MedicalGoodsConstant.SPEC_NAME_VAL_SPLITER);
            // 没有填写对应规格值
            if (StrUtil.isBlank(val)) {
                throw new IllegalArgumentException(String.format("规格组%s名值信息错误：%s", prdDesc, specKv));
            }

            List<SpecValEntity> specValEntities = specEntity.getGoodsSpecVals();
            int i = 0;
            for (; i < specValEntities.size(); i++) {
                if (val.equals(specValEntities.get(i).getSpecValName())) {
                    idStrBuilder.append(specValEntities.get(i).getSpecValId());
                    break;
                }
            }
            // 规格值没法匹配
            if (i == specValEntities.size()) {
                throw new IllegalArgumentException(String.format("规格组%s名值信息错误：%s", prdDesc, specKv));
            }
        }

        prdSpecs = idStrBuilder.toString();
    }

    /**
     * 过滤出需要进行修改的集合，根据是否存在sku id
     * @param goodsSpecProductEntities 待处理集合
     * @return 需要跟新的集合
     */
    public static List<GoodsSpecProductEntity> filterSkuForUpdateOrInsert(List<GoodsSpecProductEntity> goodsSpecProductEntities, boolean forUpdate) {
        if (goodsSpecProductEntities == null) {
            return new ArrayList<>(0);
        }
        List<GoodsSpecProductEntity> retList;
        if (forUpdate) {
            retList = goodsSpecProductEntities.stream().filter(x -> x.getPrdId() != null).collect(Collectors.toList());
        } else {
            retList = goodsSpecProductEntities.stream().filter(x -> x.getPrdId() ==null).collect(Collectors.toList());
        }

        return retList;
    }
}
