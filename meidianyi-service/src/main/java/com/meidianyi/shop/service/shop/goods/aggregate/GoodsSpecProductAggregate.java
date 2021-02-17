package com.meidianyi.shop.service.shop.goods.aggregate;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.shop.table.GoodsSpecProductDo;
import com.meidianyi.shop.common.pojo.shop.table.SpecDo;
import com.meidianyi.shop.common.pojo.shop.table.SpecValDo;
import com.meidianyi.shop.dao.shop.goods.GoodsSpecProductDao;
import com.meidianyi.shop.dao.shop.goods.SpecDao;
import com.meidianyi.shop.dao.shop.goods.SpecValDao;
import com.meidianyi.shop.service.pojo.shop.medical.sku.entity.GoodsSpecProductEntity;
import com.meidianyi.shop.service.pojo.shop.medical.sku.entity.SpecEntity;
import com.meidianyi.shop.service.pojo.shop.medical.sku.entity.SpecValEntity;
import com.meidianyi.shop.service.pojo.shop.medical.sku.vo.GoodsSpecProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 写的层次太深
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Component
public class GoodsSpecProductAggregate {
    @Autowired
    GoodsSpecProductDao goodsSpecProductDao;
    @Autowired
    SpecDao specDao;
    @Autowired
    SpecValDao specValDao;

    /**
     * 获取GoodsSpecProduct转GoodsSpecProductDo时忽略的字段
     * @return 需要忽略的字段
     */
    private static Set<String> getSpecAssignIgnoreFields() {
        Set<String> assignIgnoreField = new HashSet<>(1);
        assignIgnoreField.add("specVals");
        return assignIgnoreField;
    }

    /**
     * GoodsSpecProduct Do转Entity
     * @param goodsSpecProductDos
     * @return
     */
    private static List<GoodsSpecProductEntity> convertGoodsSpecProductDoToEntity(List<GoodsSpecProductDo> goodsSpecProductDos) {
        return goodsSpecProductDos.stream().map(skuDo -> {
            GoodsSpecProductEntity goodsSpecProductEntity = new GoodsSpecProductEntity();
            FieldsUtil.assign(skuDo, goodsSpecProductEntity);
            return goodsSpecProductEntity;
        }).collect(Collectors.toList());
    }

    /**
     * GoodsSpecProduct entity 转 do
     * @param goodsSpecProductEntities
     * @return
     */
    private static GoodsSpecProductDo convertGoodsSpecProductEntityToDo(GoodsSpecProductEntity goodsSpecProductEntities) {
        GoodsSpecProductDo goodsSpecProductDo = new GoodsSpecProductDo();
        FieldsUtil.assign(goodsSpecProductEntities, goodsSpecProductDo);
        return goodsSpecProductDo;
    }


    /**
     * 批量插入sku
     * @param goodsSpecProductEntities
     */
    public void batchSkuInsert(List<GoodsSpecProductEntity> goodsSpecProductEntities) {
        List<GoodsSpecProductDo> goodsSpecProductDos = new ArrayList<>(goodsSpecProductEntities.size());
        for (GoodsSpecProductEntity goodsSpecProductEntity : goodsSpecProductEntities) {
            GoodsSpecProductDo goodsSpecProductDo = convertGoodsSpecProductEntityToDo(goodsSpecProductEntity);
            goodsSpecProductDos.add(goodsSpecProductDo);
        }
        goodsSpecProductDao.batchInsert(goodsSpecProductDos);
        for (int i = 0; i < goodsSpecProductEntities.size(); i++) {
            goodsSpecProductEntities.get(i).setPrdId(goodsSpecProductDos.get(i).getPrdId());
        }
    }

    /**
     * 批量更新sku
     * @param goodsSpecProductEntities
     */
    public void batchSkuUpdate(List<GoodsSpecProductEntity> goodsSpecProductEntities) {
        List<GoodsSpecProductDo> goodsSpecProductDos = new ArrayList<>(goodsSpecProductEntities.size());
        for (GoodsSpecProductEntity goodsSpecProductEntity : goodsSpecProductEntities) {
            GoodsSpecProductDo goodsSpecProductDo = convertGoodsSpecProductEntityToDo(goodsSpecProductEntity);
            goodsSpecProductDos.add(goodsSpecProductDo);
        }
        goodsSpecProductDao.batchUpdate(goodsSpecProductDos);
    }

    public Map<Integer, List<GoodsSpecProductDetailVo>> groupGoodsIdToSku(List<Integer> goodsIds) {
        return goodsSpecProductDao.groupGoodsIdToSku(goodsIds);
    }

    /**
     * 根据商品id查询对应sku集合
     * @param goodsId
     * @return
     */
    public List<GoodsSpecProductEntity> listSkuByGoodsId(Integer goodsId) {
        List<GoodsSpecProductDo> goodsSpecProductDos = goodsSpecProductDao.getSkuByGoodsId(goodsId);
        return convertGoodsSpecProductDoToEntity(goodsSpecProductDos);
    }

    /**
     * 根据商品id集合查询对应sku集合
     * @param goodsIds
     * @return
     */
    public List<GoodsSpecProductEntity> listSkuByGoodsIds(List<Integer> goodsIds) {
        List<GoodsSpecProductDo> goodsSpecProductDos = goodsSpecProductDao.listSkuByGoodsIds(goodsIds);
        return convertGoodsSpecProductDoToEntity(goodsSpecProductDos);
    }

    /**
     * 删除sku
     * @param goodsId
     */
    public void deleteSkuByGoodsId(Integer goodsId) {
        goodsSpecProductDao.deleteByGoodsId(goodsId);
    }

    /**
     * 批量插入规格组
     * @param specEntities 规格组
     */
    public void batchSpecInsert(List<SpecEntity> specEntities) {
        List<SpecDo> specDos = new ArrayList<>(specEntities.size());

        Set<String> specAssignIgnoreFields = getSpecAssignIgnoreFields();
        for (SpecEntity specEntity : specEntities) {
            SpecDo specDo = new SpecDo();
            FieldsUtil.assignWithIgnoreField(specEntity, specDo, specAssignIgnoreFields);
            specDos.add(specDo);
        }
        specDao.batchInsert(specDos);

        List<SpecValEntity> specValEntities = new ArrayList<>(2);
        List<SpecValDo> specValDos = new ArrayList<>(2);
        // 设置specId，提取specVal准备入库
        for (int i = 0; i < specDos.size(); i++) {
            SpecEntity specEntity = specEntities.get(i);
            specEntity.setSpecId(specDos.get(i).getSpecId());

            for (SpecValEntity specValEntity : specEntity.getGoodsSpecVals()) {
                specValEntities.add(specValEntity);
                specValEntity.setSpecId(specEntity.getSpecId());
                SpecValDo specValDo = new SpecValDo(specEntity.getSpecId(), specValEntity.getGoodsId(), specValEntity.getSpecValName());
                specValDos.add(specValDo);
            }
        }

        specValDao.batchInsert(specValDos);
        for (int i = 0; i < specValDos.size(); i++) {
            specValEntities.get(i).setSpecValId(specValDos.get(i).getSpecValId());
        }
    }

    /**
     * 根据商品id获取其对应规格组信息
     * @param goodsId
     * @return
     */
    public List<SpecEntity> listSpecListByGoodsId(Integer goodsId) {
        List<SpecDo> specDos = specDao.getSpecsByGoodsId(goodsId);
        List<SpecValDo> specValDos = specValDao.getSepcValsByGoodsId(goodsId);

        List<SpecEntity> specEntities = new ArrayList<>(specDos.size());
        for (SpecDo specDo : specDos) {
            SpecEntity specVo = new SpecEntity();
            specVo.setGoodsId(goodsId);
            specVo.setSpecId(specDo.getSpecId());
            specVo.setSpecName(specDo.getSpecName());
            specEntities.add(specVo);
        }
        Map<Integer, List<SpecValDo>> specValDoGroup = specValDos.stream().collect(Collectors.groupingBy(SpecValDo::getSpecId));

        for (SpecEntity specEntity : specEntities) {
            List<SpecValDo> svds = specValDoGroup.get(specEntity.getSpecId());
            List<SpecValEntity> svls = svds.stream().map(svd -> {
                SpecValEntity specValEntity = new SpecValEntity();
                specValEntity.setGoodsId(goodsId);
                specValEntity.setSpecId(svd.getSpecId());
                specValEntity.setSpecValId(svd.getSpecValId());
                specValEntity.setSpecValName(svd.getSpecValName());
                return specValEntity;
            }).collect(Collectors.toList());

            specEntity.setGoodsSpecVals(svls);
        }

        return specEntities;
    }

    /**
     * 根据商品id删除规格组
     * @param goodsId
     */
    public void deleteSpecByGoodsId(Integer goodsId) {
        specDao.deleteByGoodsId(goodsId);
        specValDao.deleteByGoodsId(goodsId);
    }


    public void updateExternalSku(Integer goodsId, BigDecimal shopPrice, BigDecimal marketPrice, BigDecimal costPrice){
        goodsSpecProductDao.updateExternalSku(goodsId,shopPrice,marketPrice,costPrice);
    }
}
