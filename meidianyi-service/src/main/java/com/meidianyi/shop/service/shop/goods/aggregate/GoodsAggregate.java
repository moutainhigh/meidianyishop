package com.meidianyi.shop.service.shop.goods.aggregate;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.GoodsMedicalInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsDo;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsPageListCondition;
import com.meidianyi.shop.dao.shop.goods.GoodsDao;
import com.meidianyi.shop.dao.shop.goods.GoodsMedicalInfoDao;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsMatchParam;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.shop.medical.goods.bo.GoodsMedicalExternalRequestItemBo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.convertor.GoodsConverter;
import com.meidianyi.shop.service.pojo.shop.medical.goods.entity.GoodsEntity;
import com.meidianyi.shop.service.pojo.shop.medical.goods.entity.GoodsMedicalInfoEntity;
import com.meidianyi.shop.service.pojo.shop.medical.goods.param.MedicalGoodsBatchOperateParam;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsDetailVo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsMedicalInfoVo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsStatusVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Component
public class GoodsAggregate {
    @Autowired
    GoodsDao goodsDao;
    @Autowired
    GoodsMedicalInfoDao goodsMedicalInfoDao;

    /**
     * 商品新增
     * @param goodsEntity
     */
    public void insert(GoodsEntity goodsEntity) {
        GoodsDo goodsDo = new GoodsDo();
        FieldsUtil.assignWithIgnoreField(goodsEntity, goodsDo, getGoodsAssignIgnoreFields());
        goodsDao.insert(goodsDo);
        Integer goodsId = goodsDo.getGoodsId();
        goodsEntity.setGoodsId(goodsId);

        if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(goodsEntity.getIsMedical())) {
            GoodsMedicalInfoDo goodsMedicalInfoDo = new GoodsMedicalInfoDo();
            GoodsMedicalInfoEntity goodsMedicalInfoEntity = goodsEntity.getGoodsMedicalInfo();
            goodsMedicalInfoEntity.setGoodsId(goodsId);
            FieldsUtil.assign(goodsMedicalInfoEntity, goodsMedicalInfoDo);
            goodsMedicalInfoDao.insert(goodsMedicalInfoDo);
        }
    }

    /**
     * 商品修改
     * @param goodsEntity
     */
    public void update(GoodsEntity goodsEntity) {
        GoodsDo goodsDo = new GoodsDo();
        FieldsUtil.assignWithIgnoreField(goodsEntity, goodsDo, getGoodsAssignIgnoreFields());
        goodsDao.update(goodsDo);

        if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(goodsEntity.getIsMedical())) {
            GoodsMedicalInfoDo goodsMedicalInfoDo = new GoodsMedicalInfoDo();
            GoodsMedicalInfoEntity goodsMedicalInfoEntity = goodsEntity.getGoodsMedicalInfo();
            FieldsUtil.assign(goodsMedicalInfoEntity, goodsMedicalInfoDo);
            goodsMedicalInfoDao.update(goodsMedicalInfoDo);
        } else {
            goodsMedicalInfoDao.deleteByGoodsId(goodsEntity.getGoodsId());
        }
    }

    public void insertExternalInfo(GoodsMedicalExternalRequestItemBo bo){
        GoodsDo goodsDo = GoodsConverter.convertGoodsMedicalExternalRequestItemBoToGoodsDo(bo);
        goodsDao.insert(goodsDo);
        if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(bo.getIsMedical())) {
            GoodsMedicalInfoDo goodsMedicalInfoDo = GoodsConverter.convertGoodsMedicalExternalRequestItemBoToGoodsMedicalInfoDo(bo);
            goodsMedicalInfoDao.insert(goodsMedicalInfoDo);
        }
        bo.setGoodsId(goodsDo.getGoodsId());
    }

    public void updateExternalInfo(GoodsMedicalExternalRequestItemBo bo){
        GoodsDo goodsDo = GoodsConverter.convertGoodsMedicalExternalRequestItemBoToGoodsDo(bo);
        if (DelFlag.DISABLE_VALUE.equals(goodsDo.getDelFlag())){
            goodsDo.setGoodsSn(DelFlag.DEL_ITEM_PREFIX+goodsDo.getGoodsId()+DelFlag.DEL_ITEM_SPLITER+goodsDo.getGoodsSn());
        }
        goodsDao.update(goodsDo);

        if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(bo.getIsMedical())){
            GoodsMedicalInfoDo goodsMedicalInfoDo = GoodsConverter.convertGoodsMedicalExternalRequestItemBoToGoodsMedicalInfoDo(bo);
            goodsMedicalInfoDo.setIsDelete(DelFlag.DISABLE_VALUE);
            goodsMedicalInfoDao.update(goodsMedicalInfoDo);
        }
    }

    /**
     * 新增外部药品信息
     * @param goodsMedicalExternalRequestItemBos
     */
    public void batchInsert(List<GoodsMedicalExternalRequestItemBo> goodsMedicalExternalRequestItemBos) {
        List<GoodsDo> goodsDos = new ArrayList<>(goodsMedicalExternalRequestItemBos.size());
        List<GoodsMedicalInfoDo> goodsMedicalInfoDos = new ArrayList<>(goodsMedicalExternalRequestItemBos.size());

        for (GoodsMedicalExternalRequestItemBo bo : goodsMedicalExternalRequestItemBos) {
            GoodsDo goodsDo = GoodsConverter.convertGoodsMedicalExternalRequestItemBoToGoodsDo(bo);
            goodsDos.add(goodsDo);
            if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(bo.getIsMedical())) {
                GoodsMedicalInfoDo goodsMedicalInfoDo = GoodsConverter.convertGoodsMedicalExternalRequestItemBoToGoodsMedicalInfoDo(bo);
                goodsMedicalInfoDos.add(goodsMedicalInfoDo);
            }
        }

        goodsDao.batchInsert(goodsDos);

        Map<String, GoodsDo> goodsSnMap = goodsDos.stream().collect(Collectors.toMap(GoodsDo::getGoodsSn, Function.identity()));

        for (GoodsMedicalInfoDo goodsMedicalInfoDo : goodsMedicalInfoDos) {
            GoodsDo goodsDo = goodsSnMap.get(goodsMedicalInfoDo.getGoodsCode());
            goodsMedicalInfoDo.setGoodsId(goodsDo.getGoodsId());
        }
        goodsMedicalInfoDao.batchInsert(goodsMedicalInfoDos);

        for (GoodsMedicalExternalRequestItemBo bo : goodsMedicalExternalRequestItemBos) {
            GoodsDo goodsDo = goodsSnMap.get(bo.getGoodsCode());
            bo.setGoodsId(goodsDo.getGoodsId());
        }
    }

    /**
     * 批量更新外部药品信息
     * @param goodsMedicalExternalRequestItemBos
     */
    public void batchUpdate(List<GoodsMedicalExternalRequestItemBo> goodsMedicalExternalRequestItemBos) {
        List<Integer> goodsIds = goodsMedicalExternalRequestItemBos.stream().map(GoodsMedicalExternalRequestItemBo::getGoodsId).collect(Collectors.toList());

        List<GoodsDo> goodsDos = new ArrayList<>(goodsMedicalExternalRequestItemBos.size());
        List<GoodsMedicalInfoDo> goodsMedicalInfoDos = new ArrayList<>(goodsMedicalExternalRequestItemBos.size());
        Map<Integer, Integer> goodsIdMedicalIdMap = goodsMedicalInfoDao.listIdWithGoodsId(goodsIds).stream().collect(Collectors.toMap(GoodsMedicalInfoDo::getGoodsId, GoodsMedicalInfoDo::getId, (x1, x2) -> x1));

        for (GoodsMedicalExternalRequestItemBo bo : goodsMedicalExternalRequestItemBos) {
            GoodsDo goodsDo = GoodsConverter.convertGoodsMedicalExternalRequestItemBoToGoodsDo(bo);
            GoodsMedicalInfoDo goodsMedicalInfoDo = new GoodsMedicalInfoDo();
            FieldsUtil.assign(bo, goodsMedicalInfoDo);
            goodsMedicalInfoDo.setId(goodsIdMedicalIdMap.get(bo.getGoodsId()));
            if (DelFlag.DISABLE_VALUE.equals(goodsDo.getDelFlag())){
                goodsDo.setGoodsSn(DelFlag.DEL_ITEM_PREFIX+goodsDo.getGoodsId()+DelFlag.DEL_ITEM_SPLITER+goodsDo.getGoodsSn());
                goodsMedicalInfoDo.setIsDelete(DelFlag.DISABLE_VALUE);
            }
            goodsDos.add(goodsDo);
            goodsMedicalInfoDos.add(goodsMedicalInfoDo);
        }
        goodsDao.batchUpdate(goodsDos);
        goodsMedicalInfoDao.batchUpdate(goodsMedicalInfoDos);
    }


    /**
     * 药房拉取到的药品信息批量修改
     * @param goodsMedicalExternalRequestItemBos
     */
    public void batchUpdateStoreGoodsInfo(List<GoodsMedicalExternalRequestItemBo> goodsMedicalExternalRequestItemBos){
        List<GoodsDo> goodsDos = new ArrayList<>(goodsMedicalExternalRequestItemBos.size());
        for (GoodsMedicalExternalRequestItemBo bo : goodsMedicalExternalRequestItemBos){
            GoodsDo goodsDo = GoodsConverter.convertGoodsMedicalExternalRequestItemBoToGoodsDoForStore(bo);
            goodsDos.add(goodsDo);
        }
        goodsDao.batchUpdate(goodsDos);
    }


    /**
     * 根据his数据状态和药房数据状态更新药品的上下架状态
     */
    public void batchUpStoreAndMedicalGoods(){
        goodsDao.switchSaleStatusAllGoods(MedicalGoodsConstant.OFF_SALE,null);
        goodsDao.batchUpStoreAndMedicalGoods();
    }
    /**
     * 商品删除
     * @param goodId
     */
    public void deleteGoodsById(Integer goodId) {
        goodsDao.deleteByGoodsId(goodId);
        goodsMedicalInfoDao.deleteByGoodsId(goodId);
    }


    public Map<Integer, BigDecimal> mapGoodsIdToGoodsPrice(Collection<Integer> goodsIds) {
        return goodsDao.mapGoodsIdToGoodsPrice(goodsIds);
    }

    public Map<String, Integer> mapGoodsSnToGoodsId(Collection<String> goodsCodes) {
        return goodsDao.mapGoodsSnToGoodsId(goodsCodes);
    }

    /**
     * 根据药品通用名称，规格系数，生产企业,查询his中的药品id对应关系
     * @param goodsKeys
     * @return
     */
    public Map<String, Integer> mapMedicalKeyToGoodsId(List<String> goodsKeys) {
        return goodsMedicalInfoDao.mapGoodsHisKeyToGoodsId(goodsKeys);
    }

    /**
     * 抓取药品时判断是更新还是新增
     * @param medicalKey 名称+规格系数+药企
     * @return
     */
    public GoodsEntity getByExternalInfo(String medicalKey){
        GoodsMedicalInfoDo byHisInfo = goodsMedicalInfoDao.getByHisInfo(medicalKey);
        if (byHisInfo==null){
            return null;
        }

        GoodsEntity goodsEntity =new GoodsEntity();
        goodsEntity.setGoodsId(byHisInfo.getGoodsId());
        GoodsMedicalInfoEntity goodsMedicalInfoEntity =new GoodsMedicalInfoEntity();
        goodsMedicalInfoEntity.setId(byHisInfo.getId());
        goodsMedicalInfoEntity.setGoodsId(byHisInfo.getGoodsId());
        goodsMedicalInfoEntity.setGoodsCommonName(byHisInfo.getGoodsCommonName());
        goodsMedicalInfoEntity.setGoodsQualityRatio(byHisInfo.getGoodsQualityRatio());
        goodsMedicalInfoEntity.setGoodsProductionEnterprise(byHisInfo.getGoodsProductionEnterprise());
        goodsEntity.setGoodsMedicalInfo(goodsMedicalInfoEntity);
        return goodsEntity;
    }

    public void updateExternalInfo(GoodsEntity goodsEntity) {
        GoodsDo goodsDo =new GoodsDo();
        FieldsUtil.assignWithIgnoreField(goodsEntity, goodsDo, getGoodsAssignIgnoreFields());
        goodsDao.update(goodsDo);

        if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(goodsEntity.getIsMedical())) {
            GoodsMedicalInfoDo goodsMedicalInfoDo = new GoodsMedicalInfoDo();
            GoodsMedicalInfoEntity goodsMedicalInfoEntity = goodsEntity.getGoodsMedicalInfo();
            FieldsUtil.assign(goodsMedicalInfoEntity, goodsMedicalInfoDo);
            goodsMedicalInfoDao.update(goodsMedicalInfoDo);
        } else {
            goodsMedicalInfoDao.deleteByGoodsId(goodsEntity.getGoodsId());
        }
    }

    /**
     * 根据商品id查询
     * @param goodsId
     * @return
     */
    public GoodsDetailVo getByGoodsId(Integer goodsId) {
        GoodsDo goodsDo = goodsDao.getByGoodsId(goodsId);
        if (goodsDo == null) {
            return null;
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        FieldsUtil.assign(goodsDo, goodsDetailVo);

        if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(goodsDo.getIsMedical())) {
            GoodsMedicalInfoDo goodsMedicalInfoDo = goodsMedicalInfoDao.getByGoodsId(goodsId);
            GoodsMedicalInfoVo goodsMedicalInfoVo = new GoodsMedicalInfoVo();
            FieldsUtil.assign(goodsMedicalInfoDo, goodsMedicalInfoVo);
            goodsDetailVo.setGoodsMedicalInfoVo(goodsMedicalInfoVo);
        }
        return goodsDetailVo;
    }

    /**
     * 判断goodsSn是否存在
     * @param goodsSn
     * @return true 是 false 否
     */
    public boolean isGoodsSnExist(String goodsSn,Integer goodsId) {
        return goodsDao.isGoodsSnExist(goodsSn,goodsId);
    }

    /**
     * 统计商品数量，包含已删除的
     * @return 商品数量
     */
    public int countAllGoods() {
        return goodsDao.countAllGoods();
    }

    /**
     * 分页获取商品信息
     * @param goodsPageListCondition 商品分页统一条件
     * @param curPage                当前页
     * @param pageRows               行数
     * @return 分页信息
     */
    public PageResult<GoodsEntity> getGoodsPageList(GoodsPageListCondition goodsPageListCondition, Integer curPage, Integer pageRows) {
        PageResult<GoodsDo> goodsDoPageResult = goodsDao.getGoodsPageList(goodsPageListCondition, curPage, pageRows);
        List<GoodsDo> dataList = goodsDoPageResult.dataList;
        List<Integer> goodsIds = dataList.stream().mapToInt(GoodsDo::getGoodsId).boxed().collect(Collectors.toList());

        List<GoodsMedicalInfoDo> goodsMedicalInfoDos = goodsMedicalInfoDao.listByGoodsIds(goodsIds);
        Map<Integer, GoodsMedicalInfoEntity> medicalInfoEntityMap = goodsMedicalInfoDos.stream().map(goodsMedicalInfoDo -> {
            GoodsMedicalInfoEntity goodsMedicalInfoEntity = new GoodsMedicalInfoEntity();
            FieldsUtil.assign(goodsMedicalInfoDo, goodsMedicalInfoEntity);
            return goodsMedicalInfoEntity;
        }).collect(Collectors.toMap(GoodsMedicalInfoEntity::getGoodsId, Function.identity()));

        List<GoodsEntity> retGoodsPageList = new ArrayList<>(dataList.size());
        Set<String> goodsAssignIgnoreFields = getGoodsAssignIgnoreFields();

        for (GoodsDo goodsDo : dataList) {
            GoodsEntity goodsEntity = new GoodsEntity();
            FieldsUtil.assignWithIgnoreField(goodsDo, goodsEntity, goodsAssignIgnoreFields);
            goodsEntity.setGoodsMedicalInfo(medicalInfoEntityMap.get(goodsEntity.getGoodsId()));
            retGoodsPageList.add(goodsEntity);
        }

        PageResult<GoodsEntity> pageResult = new PageResult<>();
        pageResult.setPage(goodsDoPageResult.getPage());
        pageResult.setDataList(retGoodsPageList);

        return pageResult;
    }

    /**
     * 获取Goods转GoodsDo时忽略的字段
     * @return 需要忽略的字段
     */
    private Set<String> getGoodsAssignIgnoreFields() {
        Set<String> assignIgnoreField = new HashSet<>(2);
        assignIgnoreField.add("goodsSpecProducts");
        assignIgnoreField.add("goodsMedicalInfo");
        assignIgnoreField.add("specs");
        assignIgnoreField.add("goodsMedicalInfo");
        assignIgnoreField.add("imgPaths");
        assignIgnoreField.add("labelIds");
        return assignIgnoreField;
    }

    /**
     * 根据goodsId,goodsCommonName,goodsQualityRatio,productionEnterprise匹配药品Id
     * @param goodsMatchParam
     * @return
     */
    public Integer matchGoodsMedical(GoodsMatchParam goodsMatchParam) {
        GoodsStatusVo goodsInfo = goodsDao.getGoodsIdByInfo(goodsMatchParam);
        if (goodsInfo != null && DelFlag.NORMAL_VALUE.equals(goodsInfo.getDelFlag()) && GoodsConstant.ON_SALE.equals(goodsInfo.getIsOnSale())) {
            return goodsInfo.getGoodsId();
        }
        goodsMatchParam.setGoodsId(null);
        GoodsStatusVo goodsInfo1 = goodsDao.getGoodsIdByInfo(goodsMatchParam);
        if (goodsInfo1 != null && DelFlag.NORMAL_VALUE.equals(goodsInfo1.getDelFlag()) && GoodsConstant.ON_SALE.equals(goodsInfo1.getIsOnSale())) {
            return goodsInfo1.getGoodsId();
        }
        goodsMatchParam.setProductionEnterprise(null);
        GoodsStatusVo goodsInfo2 = goodsDao.getGoodsIdByInfo(goodsMatchParam);
        if (goodsInfo2 != null && DelFlag.NORMAL_VALUE.equals(goodsInfo2.getDelFlag()) && GoodsConstant.ON_SALE.equals(goodsInfo2.getIsOnSale())) {
            return goodsInfo2.getGoodsId();
        }
        goodsMatchParam.setGoodsQualityRatio(null);
        GoodsStatusVo goodsInfo3 = goodsDao.getGoodsIdByInfo(goodsMatchParam);
        if (goodsInfo3 != null && DelFlag.NORMAL_VALUE.equals(goodsInfo3.getDelFlag()) && GoodsConstant.ON_SALE.equals(goodsInfo3.getIsOnSale())) {
            return goodsInfo3.getGoodsId();
        }
        if (goodsInfo != null) {
            return  goodsInfo.getGoodsId();
        }
        if (goodsInfo1 != null) {
            return  goodsInfo1.getGoodsId();
        }
        if (goodsInfo2 != null) {
            return  goodsInfo2.getGoodsId();
        }
        if (goodsInfo3 != null) {
            return  goodsInfo3.getGoodsId();
        }
        return null;
    }

    public void batchOperate(MedicalGoodsBatchOperateParam param){
        goodsDao.batchOperate(param);
    }
}
