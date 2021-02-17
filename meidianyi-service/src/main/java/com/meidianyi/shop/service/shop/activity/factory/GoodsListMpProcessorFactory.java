package com.meidianyi.shop.service.shop.activity.factory;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.shop.activity.processor.ActivityGoodsListProcessor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年10月29日
 */
@Service
public class GoodsListMpProcessorFactory extends AbstractProcessorFactory<ActivityGoodsListProcessor, GoodsListMpBo> {

    @Override
    public void doProcess(List<GoodsListMpBo> goodsListMpBos, Integer userId) {
        if (goodsListMpBos == null || goodsListMpBos.size() == 0) {
            return;
        }
        for (ActivityGoodsListProcessor processor : processors) {
                processor.processForList(goodsListMpBos,userId);
        }
    }
}
