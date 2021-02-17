package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelCoupleTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsLabelMpVo;
import com.meidianyi.shop.service.shop.activity.dao.GoodsLabelProcessorDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 李晓冰
 * @date 2019年11月04日
 */
@Service
@Slf4j
public class GoodsLabelProcessor implements Processor,ActivityGoodsListProcessor,GoodsDetailProcessor {

    @Autowired
    GoodsLabelProcessorDao goodsLabelProcessorDao;
    /*****处理器优先级*****/
    @Override
    public Byte getPriority() {
        return 0;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_GENERAL;
    }

    /*****************商品列表处理*******************/
    @Override
    public void processForList(List<GoodsListMpBo> capsules, Integer userId) {
        // es 处理
        if (capsules.get(0) != null && capsules.get(0).getIsDisposedByEs()) {
            return;
        }

        List<Integer> goodsIds = new ArrayList<>();
        List<Integer> catIds = new ArrayList<>();
        List<Integer> sortIds = new ArrayList<>();
        capsules.forEach(capsule->{
            goodsIds.add(capsule.getGoodsId());
            catIds.add(capsule.getCatId());
            sortIds.add(capsule.getSortId());
        });

        Map<Byte, Map<Integer, GoodsLabelMpVo>> goodsLabelsMap = goodsLabelProcessorDao.getGoodsClosestLabelsInfo(goodsIds, catIds, sortIds);

        capsules.forEach(capsule->{
            Map<Integer, GoodsLabelMpVo> goodsIdMap = goodsLabelsMap.get(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode());
            Map<Integer, GoodsLabelMpVo> catIdMap = goodsLabelsMap.get(GoodsLabelCoupleTypeEnum.CATTYPE.getCode());
            Map<Integer, GoodsLabelMpVo> sortIdMap = goodsLabelsMap.get(GoodsLabelCoupleTypeEnum.SORTTYPE.getCode());
            Map<Integer, GoodsLabelMpVo> allGoodsMap = goodsLabelsMap.get(GoodsLabelCoupleTypeEnum.ALLTYPE.getCode());

            if (goodsIdMap.get(capsule.getGoodsId()) != null) {
                capsule.setLabel(goodsIdMap.get(capsule.getGoodsId()));
            } else if (catIdMap.get(capsule.getCatId()) != null) {
                capsule.setLabel(catIdMap.get(capsule.getCatId()));
            } else if (sortIdMap.get(capsule.getSortId()) != null) {
                capsule.setLabel(sortIdMap.get(capsule.getSortId()));
            } else if (allGoodsMap.size() > 0) {
                capsule.setLabel(allGoodsMap.get(GoodsConstant.LABEL_GTA_DEFAULT_VALUE));
            }
        });
    }
    /*****************商品详情处理******************/
    @Override
    public void processGoodsDetail(GoodsDetailMpBo goodsDetailMpBo, GoodsDetailCapsuleParam param) {
        if (goodsDetailMpBo.getIsDisposedByEs()) {
            return;
        }
        List<String> labels = goodsLabelProcessorDao.getGoodsDetailLabels(param.getGoodsId(),param.getCatId(),param.getSortId());
        log.debug("商品详情-商品关联标签处理：{}",labels);
        goodsDetailMpBo.setLabels(labels);
    }

}
