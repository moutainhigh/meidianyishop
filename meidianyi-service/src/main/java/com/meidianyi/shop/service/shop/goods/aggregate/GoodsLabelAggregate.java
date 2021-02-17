package com.meidianyi.shop.service.shop.goods.aggregate;

import com.meidianyi.shop.common.pojo.shop.table.GoodsLabelCoupleDo;
import com.meidianyi.shop.common.pojo.shop.table.GoodsLabelDo;
import com.meidianyi.shop.dao.shop.label.GoodsLabelCoupleDao;
import com.meidianyi.shop.dao.shop.label.GoodsLabelDao;
import com.meidianyi.shop.service.pojo.shop.medical.label.MedicalLabelConstant;
import com.meidianyi.shop.service.pojo.shop.medical.label.bo.GoodsLabelBo;
import com.meidianyi.shop.service.pojo.shop.medical.label.entity.GoodsLabelCoupleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Component
public class GoodsLabelAggregate {
    @Autowired
    GoodsLabelDao goodsLabelDao;
    @Autowired
    GoodsLabelCoupleDao goodsLabelCoupleDao;

    /**
     * 批量插入标签关联数据
     * @param goodsLabelCoupleEntities
     */
    public void batchInsertCouple(List<GoodsLabelCoupleEntity> goodsLabelCoupleEntities) {
        List<GoodsLabelCoupleDo> goodsLabelCoupleDos = new ArrayList<>(goodsLabelCoupleEntities.size());

        for (GoodsLabelCoupleEntity goodsLabelCoupleEntity : goodsLabelCoupleEntities) {
            GoodsLabelCoupleDo goodsLabelCoupleDo = new GoodsLabelCoupleDo();
            goodsLabelCoupleDo.setLabelId(goodsLabelCoupleEntity.getLabelId());
            goodsLabelCoupleDo.setGtaId(goodsLabelCoupleEntity.getGtaId());
            goodsLabelCoupleDo.setType(goodsLabelCoupleEntity.getType());
            goodsLabelCoupleDos.add(goodsLabelCoupleDo);
        }
        goodsLabelCoupleDao.batchInsert(goodsLabelCoupleDos);
    }

    /**
     * 根据标签id和标签关联类型获取对应的gtaId集合,
     * type为{@link MedicalLabelConstant#GTA_ALL} 时返回null表示没有匹配项，否则表示存在全局匹配的标签
     * @param labelId
     * @param type
     * @return
     */
    public List<Integer> listGtaIdsByLabelIdAndType(Integer labelId, Byte type) {
        List<GoodsLabelCoupleDo> goodsLabelCoupleDos = goodsLabelCoupleDao.listByLabelIdAndType(labelId, type);

        if (MedicalLabelConstant.GTA_ALL.equals(type)) {
            if (goodsLabelCoupleDos.size() == 0) {
                return null;
            } else {
                return new ArrayList<>();
            }
        } else {
            return goodsLabelCoupleDos.stream().map(GoodsLabelCoupleDo::getGtaId).collect(Collectors.toList());
        }
    }

    /**
     * 获取所有关联了所有商品的标签
     * @return
     */
    public List<GoodsLabelBo> listLabelBoByType(Byte type){
        List<GoodsLabelCoupleDo> goodsLabelCoupleDos = goodsLabelCoupleDao.listByType(type);
        List<Integer> labelIds = goodsLabelCoupleDos.stream().map(GoodsLabelCoupleDo::getLabelId).collect(Collectors.toList());
        List<GoodsLabelDo> goodsLabelDos = goodsLabelDao.getLabelByLabelIds(labelIds);
        return convertCoupleDoToBo(goodsLabelCoupleDos,goodsLabelDos);
    }

    /**
     * 根据关联类型和gtaId集合获取标签聚合信息
     * @param gtaIds
     * @param type
     * @return
     */
    public List<GoodsLabelBo> listLabelAoByGtaIdsAndType(List<Integer> gtaIds, Byte type) {
        List<GoodsLabelCoupleDo> goodsLabelCoupleDos = goodsLabelCoupleDao.listByGtaIdsAndType(gtaIds, type);
        List<Integer> labelIds= goodsLabelCoupleDos.stream().map(GoodsLabelCoupleDo::getLabelId).collect(Collectors.toList());
        List<GoodsLabelDo> goodsLabelDos = goodsLabelDao.getLabelByLabelIds(labelIds);
        return convertCoupleDoToBo(goodsLabelCoupleDos,goodsLabelDos);
    }

    /**
     * GoodsLabelCoupleDo集合转GoodsLabelAo
     * @param goodsLabelCoupleDos
     * @param labelDos
     * @return
     */
    private List<GoodsLabelBo> convertCoupleDoToBo(List<GoodsLabelCoupleDo> goodsLabelCoupleDos, List<GoodsLabelDo> labelDos) {
        Map<Integer, GoodsLabelDo> labelDoMap = labelDos.stream().collect(Collectors.toMap(GoodsLabelDo::getId, Function.identity(), (x1, x2) -> x1));

        List<GoodsLabelBo> labelAos = new ArrayList<>(goodsLabelCoupleDos.size());
        for (GoodsLabelCoupleDo goodsLabelCoupleDo : goodsLabelCoupleDos) {
            GoodsLabelBo bo =new GoodsLabelBo();
            GoodsLabelDo goodsLabelDo = labelDoMap.get(goodsLabelCoupleDo.getLabelId());
            if (goodsLabelDo == null) {
                continue;
            }
            bo.setGtaId(goodsLabelCoupleDo.getGtaId());
            bo.setId(goodsLabelDo.getId());
            bo.setLabelName(goodsLabelDo.getName());
            labelAos.add(bo);
        }
        return labelAos;
    }

    /**
     * 删除标签关联信息
     * @param gtaIds  待删除id集合
     * @param gtaType 要删除的类型
     */
    public void deleteCouple(List<Integer> gtaIds, Byte gtaType) {
        goodsLabelCoupleDao.deleteCouple(gtaIds, gtaType);
    }
}
