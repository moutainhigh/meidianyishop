package com.meidianyi.shop.service.shop.order.atomic;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLock;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockKeys;
import com.meidianyi.shop.service.foundation.util.lock.annotation.operation.AddRedisLocks;
import com.meidianyi.shop.service.foundation.util.lock.annotation.operation.ReleaseRedisLocks;
import com.meidianyi.shop.service.pojo.shop.goods.goods.BatchUpdateGoodsNumAndSaleNumForOrderParam;
import com.meidianyi.shop.service.pojo.shop.goods.goods.BatchUpdateGoodsNumAndSaleNumForOrderParam.ProductNumInfo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.GoodsSpecProductService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.YES;

/**
 * 订单库存、销量更新规则
 * @author 王帅
 */
@Component
@Slf4j
public class AtomicOperation extends ShopBaseService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsSpecProductService goodsSpecProduct;

    @Autowired
    private OrderInfoService orderInfo;

    @Autowired
    private GoodsService goods;

    /**
     * 下单扣库存或者下单前就已经扣了库存
     */
    public static List<Byte> ACT_IS_LOCK = null;
    static {
        ACT_IS_LOCK = Lists.newArrayList(
            BaseConstant.ACTIVITY_TYPE_BARGAIN,
            BaseConstant.ACTIVITY_TYPE_SEC_KILL,
            BaseConstant.ACTIVITY_TYPE_LOTTERY_PRESENT,
            BaseConstant.ACTIVITY_TYPE_PAY_AWARD,
            BaseConstant.ACTIVITY_TYPE_MY_PRIZE,
            BaseConstant.ACTIVITY_TYPE_ASSESS_ORDER,
            BaseConstant.ACTIVITY_TYPE_PROMOTE_ORDER
        );
    }
    /**
     * 该活动库存通过其他方式更新
     */
    public static List<Byte> filterAct = null;
    static {
        filterAct = Lists.newArrayList(
            BaseConstant.ACTIVITY_TYPE_BARGAIN,
            BaseConstant.ACTIVITY_TYPE_GROUP_DRAW,
            BaseConstant.ACTIVITY_TYPE_SEC_KILL,
            BaseConstant.ACTIVITY_TYPE_LOTTERY_PRESENT,
            BaseConstant.ACTIVITY_TYPE_PAY_AWARD,
            BaseConstant.ACTIVITY_TYPE_MY_PRIZE,
            BaseConstant.ACTIVITY_TYPE_ASSESS_ORDER,
            BaseConstant.ACTIVITY_TYPE_PROMOTE_ORDER
        );
    }

    /**
     * 普通商品库存更新
     * @param order 订单
     * @param goodsBo 商品
     * @param limit 超卖限制 true 不允许超卖，false 允许超卖
     * @throws MpException
     */
    @RedisLock(prefix = JedisKeyConstant.GOODS_LOCK)
    public void updateStockAndSalesByLock(OrderInfoRecord order, @RedisLockKeys List<OrderGoodsBo> goodsBo, boolean limit) throws MpException {
        updateStockandSalesByActFilter(order, goodsBo, limit);
    }

    @AddRedisLocks(redisLock = @RedisLock(prefix = JedisKeyConstant.GOODS_LOCK))
    public void addLock(@RedisLockKeys List<OrderGoodsBo> goodsBo){
        log.info("atomicOperation addLock end");
    }

    @ReleaseRedisLocks
    public void releaseLocks(){
        log.info("atomicOperation releaseLocks end");
    }

    /**
     * 普通商品库存更新（过滤某些活动）
     * @param order
     * @param goodsBo
     * @param limit
     * @throws MpException
     */
    public void updateStockandSalesByActFilter(OrderInfoRecord order, List<OrderGoodsBo> goodsBo, boolean limit) throws MpException {
        Byte[] types = OrderInfoService.orderTypeToByte(order.getGoodsType());
        //过滤活动
        for (Byte type : types) {
            if(filterAct.contains(type)) {
                return;
            }
        }
        updateStockandSales(order, goodsBo, limit);
    }

    public void updateStockandSales(OrderInfoRecord order, List<OrderGoodsBo> goodsBo, boolean limit) throws MpException {
        log.info("AtomicOperation.updateStockAndSales订单库存销量更新start,订单号{},商品{}", order.getOrderId(), goodsBo);
        //TODO 第三方对接erp
        List<Integer> goodsIds = goodsBo.stream().map(OrderGoodsBo::getGoodsId).distinct().collect(Collectors.toList());
        List<Integer> proIds = goodsBo.stream().map(OrderGoodsBo::getProductId).collect(Collectors.toList());
        //商品map
        Map<Integer, GoodsRecord> goodsRecord = goodsService.getGoodsByIds(goodsIds);
        //规格map
        Map<Integer, GoodsSpecProductRecord> productRecord = goodsSpecProduct.selectSpecByProIds(proIds);
        //商品更新map
        Map<Integer , BatchUpdateGoodsNumAndSaleNumForOrderParam> updateGoodsMap = new HashMap<>(goodsIds.size());
        for (OrderGoodsBo goodsRow : goodsBo) {
            //购买数量
            int num = goodsRow.getGoodsNumber();
            //规格
            GoodsSpecProductRecord product = productRecord.get(goodsRow.getProductId());
            //商品
            GoodsRecord goods = goodsRecord.get(goodsRow.getGoodsId());
            //update goods
            BatchUpdateGoodsNumAndSaleNumForOrderParam updateGoods = updateGoodsMap.get(goodsRow.getGoodsId());
            if(updateGoods == null) {
                updateGoods = BatchUpdateGoodsNumAndSaleNumForOrderParam.builder().goodsId(goodsRow.getGoodsId()).build();
                updateGoodsMap.put(goodsRow.getGoodsId(), updateGoods);
            }
            if(product == null || goods == null){
                //商品或规格row为null
                throw new MpException(JsonResultCode.CODE_ORDER_GOODS_NO_EXIST , null, order.getOrderSn());
            }
            //商品库存
//            int goodsStock = goods.getGoodsNumber() - num;
            logger().info("不减商品库存 **");
            int goodsStock = goods.getGoodsNumber();
            //商品销量
            int goodsSales = goods.getGoodsSaleNum() + num;
            //规格库存
//            int productStock = product.getPrdNumber() - num;
            int productStock = product.getPrdNumber();
            log.info("规格原库存；{}，扣减库存：{}", product.getPrdNumber(), productStock);
            if(product.getPrdNumber() < num) {
                //库存不足
                log.error("库存不足订单超卖,订单号:{},规格id:{};" +
                        "原商品库存:{},原商品销量:{},原规格库存:{};" +
                        "此次购买数量:{}",
                    order.getOrderSn(), product.getPrdId(),
                    goods.getGoodsNumber(), goods.getGoodsSaleNum(), product.getPrdNumber(),
                    num
                );
                if(limit) {
                    throw new MpException(JsonResultCode.CODE_ORDER_GOODS_LOW_STOCK);
                }
                //不允许出现负值
                if(goodsStock < 0) {
                    goodsStock = 0;
                }
                if(productStock < 0) {
                    productStock = 0;
                }
            }
            //内存修改
            goods.setGoodsNumber(goodsStock);
            goods.setGoodsSaleNum(goodsSales);
            product.setPrdNumber(productStock);
            //updateGoods 赋值
            updateGoods.setGoodsNum(goodsStock);
            updateGoods.setSaleNum(goodsSales);
            updateGoods.addProductsInfo(ProductNumInfo.builder().prdId(goodsRow.getProductId()).prdNum(productStock).build());
        }
        //商品更新list
        List<BatchUpdateGoodsNumAndSaleNumForOrderParam> updateGoods = new ArrayList<>(updateGoodsMap.values());
        //商品库存更新(订单出现规格相同的可以处理)
        goodsService.batchUpdateGoodsNumsAndSaleNumsForOrder(updateGoods);
        //更新库存锁
        orderInfo.updateStockLock(order, YES);
        log.info("AtomicOperation.updateStockAndSales订单库存销量更新end");
    }

    /**
     *  检查及更新商品的库存
     * @param goodsId 商品id
     * @param productId 规格id
     * @param number 数量
     * @param limit 是否允许超买 允许true 不允许false
     */
    @RedisLock(prefix = JedisKeyConstant.GOODS_LOCK)
    public void updateStockAndSalesByLock(@RedisLockKeys  Integer goodsId,Integer productId,Integer number,boolean limit) throws MpException {
        List<BatchUpdateGoodsNumAndSaleNumForOrderParam> updateGoods = new ArrayList<>();
        log.info("修改商品库存--开始");
        GoodsSpecProductRecord productRecord = goodsSpecProduct.selectSpecByProId(productId);
        Optional<GoodsRecord> goodsRecord = goodsService.getGoodsById(goodsId);
        if (productRecord==null||!goodsRecord.isPresent()){
            log.error("商品或规格不存在goodsId:{},produco:{}",goodsId,productId);
            //商品或规格row为null
            throw new MpException(JsonResultCode.CODE_ORDER_GOODS_NO_EXIST , null, null);
        }
        log.info("修改商品:{}的库存:{}，扣减库存：{}", goodsRecord.get().getGoodsName(),productRecord.getPrdNumber(), number);
        //商品库存
        int goodsStock = goodsRecord.get().getGoodsNumber() - number;
        //商品销量
        int goodsSales = goodsRecord.get().getGoodsSaleNum() + number;
        //规格库存
        int productStock =  goodsSpecProduct.getPrdNumberByPrdId(productId)- number;
        if (productStock<0){
            if(!limit) {
                log.info("商品不允许超买");
                throw new MpException(JsonResultCode.CODE_ORDER_GOODS_LOW_STOCK);
            }
            log.info("商品超买");
            //不允许出现负值
            if(goodsStock < 0) {
                goodsStock = 0;
            }

        }
        List<ProductNumInfo> productNumInfoList= new ArrayList<>();
        productNumInfoList.add(ProductNumInfo.builder().prdId(productId).prdNum(productStock).build());
        BatchUpdateGoodsNumAndSaleNumForOrderParam goodsNumAndSaleNumForOrderParam = BatchUpdateGoodsNumAndSaleNumForOrderParam.builder().goodsId(goodsId)
                .goodsNum(goodsStock).productsInfo(productNumInfoList)
                .saleNum(goodsSales).build();
        updateGoods.add(goodsNumAndSaleNumForOrderParam);
        goodsService.batchUpdateGoodsNumsAndSaleNumsForOrder(updateGoods);
        log.info("修改商品库存--结束");
    }

    /**
     * 	退款(关闭，取消)时更新库存和销量
     * @param returnGoods
     * @param order
     * @param isRestore 是否恢复库存
     */
    @RedisLock(prefix = JedisKeyConstant.GOODS_LOCK)
    public void updateStockAndSales(@RedisLockKeys List<OrderReturnGoodsVo> returnGoods, OrderInfoVo order, boolean isRestore) {
        //TODO 对接pos erp未完成

        List<Integer> goodsIds = returnGoods.stream().map(OrderReturnGoodsVo::getGoodsId).collect(Collectors.toList());
        List<Integer> proIds = returnGoods.stream().map(OrderReturnGoodsVo::getProductId).collect(Collectors.toList());
        //查询规格
        Map<Integer, GoodsSpecProductRecord> products = goodsSpecProduct.selectSpecByProIds(proIds);
        //查询商品
        Map<Integer, GoodsRecord> normalGoods = goods.getGoodsByIds(goodsIds);
        //商品更新map
        Map<Integer , BatchUpdateGoodsNumAndSaleNumForOrderParam> updateGoodsMap = new HashMap<>(goodsIds.size());
        //聚合returnGoods
        Map<Integer, Integer> collect = returnGoods.stream().collect(Collectors.toMap(OrderReturnGoodsVo::getProductId, OrderReturnGoodsVo::getGoodsNumber, Integer::sum));
        for (Map.Entry<Integer, Integer> rGoods : collect.entrySet()) {
            if(rGoods.getValue() == 0 ) {
                continue;
            }
            if(OrderConstant.DELIVER_TYPE_COURIER == order.getDeliverType()) {
                if(isRestore) {
                    //规格
                    GoodsSpecProductRecord product = products.get(rGoods.getKey());
                    if(product == null) {
                        continue;
                    }
                    //商品
                    GoodsRecord goods = normalGoods.get(product.getGoodsId());
                    if(goods == null) {
                        continue;
                    }
                    //update goods
                    BatchUpdateGoodsNumAndSaleNumForOrderParam updateGoods = updateGoodsMap.get(product.getGoodsId());
                    if(updateGoods == null) {
                        updateGoods = BatchUpdateGoodsNumAndSaleNumForOrderParam.builder().goodsId(product.getGoodsId()).build();
                        updateGoodsMap.put(product.getGoodsId(), updateGoods);
                    }
                    //退数量
                    Integer num = rGoods.getValue();
                    //商品库存
                    int goodsStock = goods.getGoodsNumber() + num;
                    //商品销量
                    int goodsSales = Math.max(goods.getGoodsSaleNum() - num, 0);
                    //规格库存
                    int productStock = product.getPrdNumber() + num;
                    //内存修改
                    goods.setGoodsNumber(goodsStock);
                    goods.setGoodsSaleNum(goodsSales);
                    product.setPrdNumber(productStock);
                    //updateGoods 赋值
                    updateGoods.setGoodsNum(goodsStock);
                    updateGoods.setSaleNum(goodsSales);
                    updateGoods.addProductsInfo(ProductNumInfo.builder().prdId(product.getPrdId()).prdNum(productStock).build());
                }
            }
        }
        //商品更新list
        List<BatchUpdateGoodsNumAndSaleNumForOrderParam> updateGoods = new ArrayList<>(updateGoodsMap.values());
        //商品库存更新
        goodsService.batchUpdateGoodsNumsAndSaleNumsForOrder(updateGoods);
    }
}
