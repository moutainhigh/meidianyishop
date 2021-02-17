package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLock;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockKeys;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionNumConfig;
import com.meidianyi.shop.service.pojo.shop.goods.goods.Goods;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsDataIIllegalEnum;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsDataIllegalEnumWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由于GoodsService类属性存在循环引用，导致添加切面时属性无法注入
 * 在此wrap类上添加切面进行锁控制
 * @author 李晓冰
 * @date 2020年03月27日
 */
@Service
public class GoodsWrapService extends ShopBaseService {
    @Autowired
    GoodsService goodsService;

    /**
     * 添加商品加锁函数
     *
     * @param shopId，加锁使用
     * @param goods       商品信息
     * @return
     */
    @RedisLock(prefix = JedisKeyConstant.GOODS_LOCK)
    public GoodsDataIIllegalEnum insertWithLock(@RedisLockKeys Integer shopId, Goods goods) {
        // 是否超出数量判断
        Integer limitNum = saas.getShopApp(getShopId()).version.getLimitNum(VersionNumConfig.GOODSNUM);
        if (limitNum != -1) {
            Integer count = goodsService.selectGoodsCount();
            if (count >= limitNum) {
                return GoodsDataIIllegalEnum.GOODS_NUM_FETCH_LIMIT_NUM;
            }
        }
        // 数据重复性判断
        //存在重复值则直接返回
        GoodsDataIIllegalEnum goodsDataIllegalEnum = goodsService.columnValueExistCheckForInsert(goods);
        if (!GoodsDataIIllegalEnum.GOODS_OK.equals(goodsDataIllegalEnum)) {
            return goodsDataIllegalEnum;
        }

        GoodsDataIllegalEnumWrap codeWrap = goodsService.insert(goods);
        if (codeWrap.getGoodsId() != null) {
            try {
                goodsService.updateEs(codeWrap.getGoodsId());
            } catch (Exception e) {
                codeWrap.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_DATA_UPDATE_ES_ERROR);
            }
        }
        return codeWrap.getIllegalEnum();
    }

    @RedisLock(prefix = JedisKeyConstant.GOODS_LOCK)
    public GoodsDataIIllegalEnum updateWithLock(@RedisLockKeys Integer shopId, Goods goods){
        GoodsDataIllegalEnumWrap codeWrap = goodsService.update(goods);
        return codeWrap.getIllegalEnum();
    }
}
