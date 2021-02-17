package com.meidianyi.shop.service.shop.task.market;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.seckill.SecKillProductVo;
import com.meidianyi.shop.service.pojo.shop.market.seckill.SeckillVo;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.es.EsDataUpdateMqService;
import com.meidianyi.shop.service.shop.market.seckill.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.SecKillDefine.SEC_KILL_DEFINE;

/**
 * 监控秒杀活动导致的goods表goodsType变化
 * @author: 王兵兵
 * @create: 2019-11-08 16:19
 **/
@Service
public class SeckillTaskService  extends ShopBaseService {
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private EsDataUpdateMqService esDataUpdateMqService;

    public void monitorGoodsType(){
        List<Integer> pastSeckillGoodsIdList = getPastSeckillGoodsId();
        List<Integer> currentSeckillGoodsIdList = new ArrayList<>();

        List<SeckillVo> onGoingSeckillList = getSecKillWithMonitor();
        for(SeckillVo seckill : onGoingSeckillList){
            int unpaidGoodsNum = 0;
            for(SeckillVo.SeckillGoods secKillGoods : seckill.getGoods()){
                for (SecKillProductVo secKillProduct : secKillGoods.getSecKillProduct()){
                    unpaidGoodsNum += seckillService.seckillList.getUnpaidSeckillNumberByPrd(seckill.getSkId(),secKillProduct.getProductId());
                }

                int goodsNumber = goodsService.getGoodsView(secKillGoods.getGoodsId()).getGoodsNumber();
                if(seckill.getStock() + unpaidGoodsNum > 0 && goodsNumber + unpaidGoodsNum > 0){
                    //只有处于进行中的、当前还有库存的秒杀活动所锁定的goodsId
                    currentSeckillGoodsIdList.add(secKillGoods.getGoodsId());
                }
            }
        }
        //求差集
        List<Integer> changeToNormalGoodsIds = Util.diffList(pastSeckillGoodsIdList,currentSeckillGoodsIdList);
        List<Integer> changeToActGoodsIds = Util.diffList(currentSeckillGoodsIdList,pastSeckillGoodsIdList);

        if(changeToNormalGoodsIds != null && changeToNormalGoodsIds.size() > 0){
            //活动已失效，将goodsType改回去
            goodsService.changeToNormalType(changeToNormalGoodsIds);
            //异步更新ES
            esDataUpdateMqService.addEsGoodsIndex(changeToNormalGoodsIds,getShopId(), DBOperating.UPDATE);
            //TODO 记录变动
        }

        if(changeToActGoodsIds != null && changeToActGoodsIds.size() > 0){
            //有新的活动生效，商品goodsType标记活动类型
            changeToSeckillType(changeToActGoodsIds);
            //刷新秒杀库存
            seckillService.updateSeckillProcudtStock(changeToActGoodsIds);
            //异步更新ES
            esDataUpdateMqService.addEsGoodsIndex(changeToActGoodsIds,getShopId(), DBOperating.UPDATE);
            //TODO 记录变动
        }

    }

    /**
     * 当前有效的进行中秒杀
     * @return
     */
    private List<SeckillVo> getSecKillWithMonitor(){
        List<SeckillVo> res = db().select(SEC_KILL_DEFINE.STOCK,SEC_KILL_DEFINE.GOODS_ID,SEC_KILL_DEFINE.SK_ID).from(SEC_KILL_DEFINE).where(
            SEC_KILL_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
            .and(SEC_KILL_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
            .and(SEC_KILL_DEFINE.START_TIME.lt(DateUtils.getLocalDateTime()))
            .and(SEC_KILL_DEFINE.END_TIME.gt(DateUtils.getLocalDateTime()))
        ).fetchInto(SeckillVo.class);
        for(SeckillVo seckill : res){
            seckill.setGoods(seckillService.getSecKillGoods(seckill.getSkId()));
        }
        return res;
    }

    /**
     * 当前所有goodsType还是秒杀的商品ID（某些商品可能已经过期，需要更新回普通商品）
     * @return
     */
    private List<Integer> getPastSeckillGoodsId(){
        return db().select(GOODS.GOODS_ID).from(GOODS).where(GOODS.GOODS_TYPE.eq(BaseConstant.ACTIVITY_TYPE_SEC_KILL)).fetchInto(Integer.class);
    }

    /**
     * 批量将活动商品改成秒杀商品
     * @param goodsIds
     */
    private void changeToSeckillType(List<Integer> goodsIds){
        db().update(GOODS).set(GOODS.GOODS_TYPE, BaseConstant.ACTIVITY_TYPE_SEC_KILL).where(GOODS.GOODS_ID.in(goodsIds)).execute();
    }
}
