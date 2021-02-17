package com.meidianyi.shop.service.shop.market.prize;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.shop.tables.records.PrizeRecordRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsView;
import com.meidianyi.shop.service.pojo.shop.goods.spec.ProductSmallInfoVo;
import com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeRecordParam;
import com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeRecordVo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsMpVo;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.GoodsSpecProductService;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.PRIZE_RECORD;
import static com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeConstant.PRIZE_STATUS_EXPIRE;
import static com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeConstant.PRIZE_STATUS_RECEIVED;
import static com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeConstant.PRIZE_STATUS_UNCLAIMED;

/**
 * 我的奖品
 * @author 孔德成
 * @date 2020/1/3 9:25
 */
@Service
public class PrizeRecordService extends ShopBaseService {

    @Autowired
    private OrderGoodsService orderGoodsService;
    @Autowired
    private GoodsService  goodsService;
    @Autowired
    private GoodsSpecProductService goodsSpecProductService;
    @Autowired
    private AtomicOperation atomicOperation;
    @Autowired
    private DomainConfig domainConfig;


    /**
     * 增加奖品记录
     * @param userId
     * @param actId
     * @param recordId
     * @param type
     * @param prdId
     * @param day
     */
    public PrizeRecordRecord savePrize(Integer userId,Integer actId,Integer recordId,Byte type,Integer prdId,Integer day,Timestamp expiredTime){
        PrizeRecordRecord prizeRecord =db().newRecord(PRIZE_RECORD);
        prizeRecord.setUserId(userId);
        prizeRecord.setActivityId(actId);
        prizeRecord.setRecordId(recordId);
        prizeRecord.setActivityType(type);
        prizeRecord.setPrdId(prdId);
        prizeRecord.setExpiredDay(day);
        if (expiredTime==null){
            prizeRecord.setExpiredTime(DateUtils.getTimeStampPlus(day, ChronoUnit.DAYS));
        }
        else {
            prizeRecord.setExpiredTime(expiredTime);
        }
        prizeRecord.setPrizeStatus(PRIZE_STATUS_UNCLAIMED);
        prizeRecord.insert();
        return prizeRecord;
    }

    /**
     * 查询奖品记录
     * @param userId
     * @param param
     * @return
     */
    public PageResult<PrizeRecordVo> getList(Integer userId,PrizeRecordParam param){
        SelectConditionStep<Record> select = db().select().from(PRIZE_RECORD).where(PRIZE_RECORD.USER_ID.eq(userId))
                .and(PRIZE_RECORD.PRIZE_STATUS.eq(param.getStatus()));
        PageResult<PrizeRecordVo> pageResult = getPageResult(select, param, PrizeRecordVo.class);
        List<String> orderSns = pageResult.getDataList().stream().map(PrizeRecordVo::getOrderSn).collect(Collectors.toList());
        Map<String, List<OrderGoodsMpVo>> orderGoodsMap = orderGoodsService.getByOrderGoodsSns(orderSns);
        pageResult.getDataList().forEach(prizeRecord ->{
            List<OrderGoodsMpVo> orderGoodsMpVos = orderGoodsMap.get(prizeRecord.getOrderSn());
            if (!prizeRecord.getPrizeStatus().equals(PRIZE_STATUS_UNCLAIMED)||orderGoodsMpVos==null){
                ProductSmallInfoVo product= goodsService.getProductVoInfoByProductId(prizeRecord.getPrdId());
                if (product!=null){
                    OrderGoodsMpVo orderGoodsMpVo = new OrderGoodsMpVo();
                    orderGoodsMpVo.setProductId(prizeRecord.getPrdId());
                    orderGoodsMpVo.setGoodsAttr(product.getPrdDesc());
                    orderGoodsMpVo.setGoodsImg(domainConfig.imageUrl(product.getGoodsImg()));
                    orderGoodsMpVo.setGoodsName(product.getGoodsName());
                    orderGoodsMpVo.setGoodsId(product.getGoodsId());
                    prizeRecord.setOrderGoodsMpVo(orderGoodsMpVo);
                }else {
                    logger().info("商品规格已删除,去备份表去查询");
                    ProductSmallInfoVo productBak = goodsSpecProductService.getProductBakByPrdId(prizeRecord.getPrdId());
                    if (productBak!=null){
                        OrderGoodsMpVo orderGoodsMpVo = new OrderGoodsMpVo();
                        orderGoodsMpVo.setProductId(prizeRecord.getPrdId());
                        orderGoodsMpVo.setGoodsAttr(productBak.getPrdDesc());
                        orderGoodsMpVo.setGoodsImg(domainConfig.imageUrl(productBak.getGoodsImg()));
                        orderGoodsMpVo.setGoodsName(productBak.getGoodsName());
                        orderGoodsMpVo.setGoodsId(productBak.getGoodsId());
                        prizeRecord.setOrderGoodsMpVo(orderGoodsMpVo);
                        prizeRecord.setGoodsStatus(BaseConstant.YES);
                    }
                }
            }else {
                prizeRecord.setOrderGoodsMpVo(orderGoodsMpVos.get(0));
            }
        } );
        return pageResult;
    }

    /**
     *
     * @param userId
     * @param activityId
     */
    public void checkedPrize(Integer userId, Integer activityId) {
        PrizeRecordRecord prizeRecord = getById(activityId);

    }

    /**
     * 修改记录为已领取
     * @param id
     * @param orderSn
     * @return
     */
    public int updateReceivedPrize(Integer id, String orderSn){
        return db().update(PRIZE_RECORD)
                .set(PRIZE_RECORD.PRIZE_STATUS,PRIZE_STATUS_RECEIVED)
                .set(PRIZE_RECORD.ORDER_SN,orderSn)
                .where(PRIZE_RECORD.ID.eq(id)).execute();
    }

    public PrizeRecordRecord getById(Integer id){
        return db().selectFrom(PRIZE_RECORD).where(PRIZE_RECORD.ID.eq(id)).fetchOne();
    }

    /**
     * 定时过期的奖品
     */
    public void closePrizeGoods() {
        Timestamp localDateTime = DateUtils.getLocalDateTime();
        Result<PrizeRecordRecord> fetch = db().selectFrom(PRIZE_RECORD).where(PRIZE_RECORD.PRIZE_STATUS.eq(PRIZE_STATUS_UNCLAIMED))
                .and(PRIZE_RECORD.EXPIRED_TIME.lt(localDateTime)).fetch();
        fetch.forEach(prizeRecord->{
            GoodsView goodsView = goodsService.getGoodsViewByProductId(prizeRecord.getPrdId());
            try {
                if (goodsView!=null){
                    atomicOperation.updateStockAndSalesByLock(goodsView.getGoodsId(),prizeRecord.getPrdId(),-1,true);
                }else {
                    logger().info("奖品过期-商品规格失效");
                }
            } catch (MpException e) {
                e.printStackTrace();
                logger().error("我的奖品过期--归还商品库存失败");
            }
        });
        logger().info("修改我的奖品记录状态");
        List<Integer> ids = fetch.stream().map(PrizeRecordRecord::getId).collect(Collectors.toList());
        db().update(PRIZE_RECORD).set(PRIZE_RECORD.PRIZE_STATUS,PRIZE_STATUS_EXPIRE)
                .where(PRIZE_RECORD.ID.in(ids)).execute();

    }

}
