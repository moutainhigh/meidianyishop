package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.service.pojo.shop.medical.label.MedicalLabelConstant;
import com.meidianyi.shop.service.pojo.shop.medical.label.bo.GoodsLabelBo;
import com.meidianyi.shop.service.pojo.shop.medical.label.bo.LabelRelationInfoBo;
import com.meidianyi.shop.service.pojo.shop.medical.label.entity.GoodsLabelCoupleEntity;
import com.meidianyi.shop.service.pojo.shop.medical.label.vo.GoodsLabelVo;
import com.meidianyi.shop.service.shop.goods.aggregate.GoodsLabelAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2020年07月09日
 */
@Service
public class MedicalGoodsLabelService {
    @Autowired
    private GoodsLabelAggregate goodsLabelAggregate;

    /**
     * 获取标签关联的所有关联信息
     * @param labelId 标签id
     * @return
     */
    public LabelRelationInfoBo getLabelRelationInfo(Integer labelId) {
        LabelRelationInfoBo labelRelationInfoBo = new LabelRelationInfoBo();

        List<Integer> gtaIds = goodsLabelAggregate.listGtaIdsByLabelIdAndType(labelId, MedicalLabelConstant.GTA_ALL);
        labelRelationInfoBo.setIsAll(gtaIds != null);

        gtaIds = goodsLabelAggregate.listGtaIdsByLabelIdAndType(labelId, MedicalLabelConstant.GTA_SORT);
        if (gtaIds.size() > 0) {
            labelRelationInfoBo.setSortIds(gtaIds);
        }

        gtaIds = goodsLabelAggregate.listGtaIdsByLabelIdAndType(labelId, MedicalLabelConstant.GTA_GOODS);
        if (gtaIds.size() > 0) {
            labelRelationInfoBo.setGoodsIds(gtaIds);
        }
        return labelRelationInfoBo;
    }

    /**
     * 获取关联所有商品的标签
     * @return
     */
    public List<GoodsLabelVo> listAllRelatedLabels(){
        List<GoodsLabelBo> goodsLabelBos = goodsLabelAggregate.listLabelBoByType(MedicalLabelConstant.GTA_ALL);
        return goodsLabelBos.stream().map(bo->new GoodsLabelVo(bo.getId(),bo.getLabelName())).collect(Collectors.toList());
    }
    /**
     * 获取商品id所关联的标签信息
     * @param goodsId 商品id信息
     * @return
     */
    public List<GoodsLabelVo> listGoodsIdRelatedLabels(Integer goodsId){
        List<GoodsLabelBo> goodsLabelBos = goodsLabelAggregate.listLabelAoByGtaIdsAndType(Collections.singletonList(goodsId), MedicalLabelConstant.GTA_GOODS);
        return goodsLabelBos.stream().map(bo->new GoodsLabelVo(bo.getId(),bo.getLabelName())).collect(Collectors.toList());
    }

    /**
     * 获取分类关联的标签信息
     * @param sortIds 分类id集合
     * @return
     */
    public List<GoodsLabelVo> listSortIdsRelatedLabels(List<Integer> sortIds) {
        List<GoodsLabelBo> goodsLabelBos = goodsLabelAggregate.listLabelAoByGtaIdsAndType(sortIds, MedicalLabelConstant.GTA_SORT);
        return goodsLabelBos.stream().map(bo->new GoodsLabelVo(bo.getId(),bo.getLabelName())).collect(Collectors.toList());
    }
    /**
     * 根据gtaId和type 获取gtaId到其所关联的所有标签的映射
     * @param gtaIds 关联对象id
     * @param type 关联对象类型
     * @return map key:gtaId value:标签
     */
    public Map<Integer, List<GoodsLabelVo>> mapGtaToLabel(List<Integer> gtaIds, Byte type) {
        List<GoodsLabelBo> labelBos = goodsLabelAggregate.listLabelAoByGtaIdsAndType(gtaIds, type);
        Map<Integer, List<GoodsLabelBo>> goodsLabelBoMap = labelBos.stream().collect(Collectors.groupingBy(GoodsLabelBo::getGtaId));

        Map<Integer, List<GoodsLabelVo>> retMap = new HashMap<>(goodsLabelBoMap.size());
        for (Map.Entry<Integer, List<GoodsLabelBo>> entry : goodsLabelBoMap.entrySet()) {
            List<GoodsLabelVo> vos = entry.getValue().stream().map(ao -> {
                GoodsLabelVo vo = new GoodsLabelVo();
                vo.setId(ao.getId());
                vo.setLabelName(ao.getLabelName());
                return vo;
            }).collect(Collectors.toList());
            retMap.put(entry.getKey(),vos);
        }
        return retMap;
    }

    /**
     * 批量添加商品关联标签关系
     * @param labelIds 标签id集合
     * @param goodsId 商品id
     */
    public void batchInsertGoodsCouple(List<Integer> labelIds, Integer goodsId) {
        batchInsertCouple(labelIds,goodsId,MedicalLabelConstant.GTA_GOODS);
    }
    /**
     * 批量添加标签关联关系
     * @param labelIds 标签id集合
     * @param gtaId 被关联的对象id
     * @param type 关联类型
     */
    public void batchInsertCouple(List<Integer> labelIds, Integer gtaId, Byte type){
        List<GoodsLabelCoupleEntity> goodsLabelCoupleEntities = new ArrayList<>(labelIds.size());
        for (Integer labelId : labelIds) {
            GoodsLabelCoupleEntity goodsLabelCoupleEntity = new GoodsLabelCoupleEntity();
            goodsLabelCoupleEntity.setLabelId(labelId);
            goodsLabelCoupleEntity.setGtaId(gtaId);
            goodsLabelCoupleEntity.setType(type);
            goodsLabelCoupleEntities.add(goodsLabelCoupleEntity);
        }
        goodsLabelAggregate.batchInsertCouple(goodsLabelCoupleEntities);
    }

    /**
     * 删除商品关联的标签关联
     * @param goodsId
     */
    public void deleteGoodsCouples(Integer goodsId){
        goodsLabelAggregate.deleteCouple(Collections.singletonList(goodsId),MedicalLabelConstant.GTA_GOODS);
    }
}
