package com.meidianyi.shop.service.shop.activity.factory;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.shop.activity.processor.GoodsDetailProcessor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年11月07日
 */
@Service
public class GoodsDetailMpProcessorFactory extends AbstractProcessorFactory<GoodsDetailProcessor, GoodsDetailMpBo>{

    @Override
    public void doProcess(List<GoodsDetailMpBo> capsules, Integer userId) {
        if (capsules == null || capsules.size() == 0) {
            return;
        }
        GoodsDetailCapsuleParam param = new GoodsDetailCapsuleParam();
        param.setUserId(userId);
        doProcess(capsules.get(0),param);
    }

    public void doProcess(GoodsDetailMpBo goods, GoodsDetailCapsuleParam param) {
        if (goods == null) {
            return;
        }
        param.setGoodsId(goods.getGoodsId());
        param.setSortId(goods.getSortId());
        param.setCatId(goods.getCatId());
        for (GoodsDetailProcessor processor : processors) {
            processor.processGoodsDetail(goods,param);
        }

    }
}
