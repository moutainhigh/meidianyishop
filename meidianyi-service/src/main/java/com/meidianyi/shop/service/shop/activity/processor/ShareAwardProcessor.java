package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.shop.market.sharereward.WxShareRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: 王兵兵
 * @create: 2020-05-08 15:18
 **/
@Service
public class ShareAwardProcessor implements Processor, GoodsDetailProcessor {

    @Autowired
    private WxShareRewardService shareRewardService;

    /**
     * 商品详情处理器处理函数
     *
     * @param capsule 商品详情对象{@link GoodsDetailMpBo}
     * @param param
     */
    @Override
    public void processGoodsDetail(GoodsDetailMpBo capsule, GoodsDetailCapsuleParam param) {
        capsule.setShareAwardId(shareRewardService.getAvailableShareAward(param.getUserId(), param.getGoodsId()));
        if (param.getShareAwardLaunchUserId() != null && param.getShareAwardLaunchUserId() > 0 && param.getShareAwardId() != null && param.getShareAwardId() > 0 && !param.getShareAwardLaunchUserId().equals(param.getUserId())) {
            shareRewardService.fromShare2GoodsDetail(param.getShareAwardLaunchUserId(), param.getUserId(), param.getGoodsId(), param.getShareAwardId());
        }
    }

    /**
     * 活动排序优先级
     *
     * @return
     */
    @Override
    public Byte getPriority() {
        return 0;
    }

    /**
     * 获取活动类型,方便查找
     *
     * @return 活动类型
     */
    @Override
    public Byte getActivityType() {
        return 0;
    }
}
