package com.meidianyi.shop.service.shop.order.ship;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.dao.main.StoreAccountDao;
import com.meidianyi.shop.db.shop.tables.PartOrderGoodsShip;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.PartOrderGoodsShipRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.shipping.BaseShippingInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.shipping.ShippingInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.shipping.ShippingInfoVo.Goods;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.ShipParam;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountVo;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.PartOrderGoodsShip.PART_ORDER_GOODS_SHIP;

/**
 * Table:part_order_goods_ship
 *
 * @author 王帅
 *
 */
@Service
public class ShipInfoService extends ShopBaseService {

	public final PartOrderGoodsShip TABLE = PART_ORDER_GOODS_SHIP;
	@Autowired
	private StoreAccountDao storeAccountDao;

	/**
	 * 	通过订单sn[]查询其下配送信息，已通过相同的物流号进行聚合
	 * @param sOrderSns
	 * @return  Map<Integer, List<OrderGoods>>
	 */
	public Map<String,List<ShippingInfoVo>> getShippingByOrderSn(List<String> sOrderSns) {
		if(sOrderSns.size() == 0) {
			return new HashMap<>(0);
		}
		Map<String, List<ShippingInfoVo>> goods = db().select(TABLE.asterisk())
				.from(TABLE)
				.where(TABLE.ORDER_SN.in(sOrderSns))
				.orderBy(TABLE.REC_ID.desc())
				.fetchGroups(TABLE.ORDER_SN,ShippingInfoVo.class);
		//聚合List<ShippingInfoVo>>相同批次号的对象合并它放在在数组第一次出现的位置
		for(Entry<String, List<ShippingInfoVo>> temp : goods.entrySet()) {
			List<ShippingInfoVo> voList = temp.getValue();
			Iterator<ShippingInfoVo> iterator = voList.iterator();
			while(iterator.hasNext()) {
				ShippingInfoVo next = iterator.next();
				//如果批次号相同则聚合
				int indexOf = voList.indexOf(next);
				shipInfoCompletions(next);
				//该批次号不是第一次出现的位置,将该记录信息合并到第一次出现的位置并删除
				if(voList.get(indexOf).getGoods() != null) {
					voList.get(indexOf).getGoods().add(new Goods(next.getOrderGoodsId(), next.getGoodsName(), next.getGoodsAttr(), next.getSendNumber() ,null, null,next.getGoodsId(),next.getProductId()));
					iterator.remove();
				}else {
					//第一次出现的位置,初始化goodlist,并将该记录的商品行信息转移到Goods上
					ArrayList<Goods> firstGoodsList = new ArrayList<Goods>();
					firstGoodsList.add(new Goods(next.getOrderGoodsId(), next.getGoodsName(), next.getGoodsAttr(), next.getSendNumber(), null, null,next.getGoodsId(),next.getProductId()));
					voList.get(indexOf).setGoods(firstGoodsList);
				}
			}
		}
		return goods;
	}

	/**
	 * 快递
	 * @param shippingInfoVo
	 */
	private void shipInfoCompletions(ShippingInfoVo shippingInfoVo) {
		if (shippingInfoVo.getShippingPlatform().equals(OrderConstant.PLATFORM_ADMIN)){
			logger().info("admin配送");
			shippingInfoVo.setShippingAccountName("admin管理");
		}else if (shippingInfoVo.getShippingPlatform().equals(OrderConstant.PLATFORM_STORE)){
			logger().info("store用户配送");
			StoreAccountVo storeAccountVo=storeAccountDao.getOneInfo(shippingInfoVo.getShippingAccountId());
			shippingInfoVo.setShippingAccountName(storeAccountVo.getAccountName());
		}else if (shippingInfoVo.getShippingPlatform().equals(OrderConstant.PLATFORM_WXAPP_STORE)){
			logger().info("wxStore用户配送");
			StoreAccountVo storeAccountVo=storeAccountDao.getOneInfo(shippingInfoVo.getShippingAccountId());
			shippingInfoVo.setShippingAccountName(storeAccountVo.getAccountName());
		}else if (shippingInfoVo.getShippingPlatform().equals(OrderConstant.PLATFORM_WXAPP)){
			logger().info("wx买家自提");
			shippingInfoVo.setShippingAccountName("自提");
		}
	}

	public PartOrderGoodsShipRecord addRecord(OrderGoodsRecord orderGoodsVo, OrderInfoRecord orderRecord, String batchNo, ShipParam param, Integer sendNumber) {
		PartOrderGoodsShipRecord record = new PartOrderGoodsShipRecord();
		record.setShopId(getShopId());
		record.setOrderGoodsId(orderGoodsVo.getRecId());
		record.setOrderSn(orderGoodsVo.getOrderSn());
		record.setBatchNo(batchNo);
		record.setGoodsId(orderGoodsVo.getGoodsId());
		record.setGoodsName(orderGoodsVo.getGoodsName());
		record.setProductId(orderGoodsVo.getProductId());
		record.setSendNumber(sendNumber.shortValue());
		record.setGoodsAttr(orderGoodsVo.getGoodsAttr());
		record.setShippingType(orderRecord.getDeliverType());
		record.setShippingAccountId(param.getShipAccountId());
		record.setShippingMobile(param.getMobile());
		record.setShippingPlatform(param.getPlatform());
		//核销时不设置
		record.setShippingId(param.getShippingId());
		record.setShippingNo(param.getShippingNo());
		if (param.getShipUserId()!=null){
			record.setShippingUserId(param.getShipUserId());
		}
		if (orderRecord.getDeliverType().equals(OrderConstant.DELIVER_TYPE_SELF)){
			//自提订单核销
			Timestamp temp = DateUtils.getSqlTimestamp();
			record.setConfirmTime(temp);
		}
		return record;
	}

	public void receive(String orderSn,Byte platform,Integer userId,Integer accountId) {
		db().update(TABLE).set(TABLE.CONFIRM_TIME, DateUtils.getSqlTimestamp())
				.set(TABLE.CONFIRM_PLATFORM, platform)
				.set(TABLE.CONFIRM_ACCOUNT_ID, accountId)
				.set(TABLE.CONFIRM_USER_ID, userId)
				.where(TABLE.ORDER_SN.eq(orderSn)).execute();
	}

    /**
     * 	通过order_sn和product_id查单条订单行的发货信息
     * @return  BaseShippingInfoVo
     */
    public BaseShippingInfoVo getOrderGoodsShipping(String orderSn,Integer recId) {
        Record record = (Record) db().select(PART_ORDER_GOODS_SHIP.ORDER_SN,PART_ORDER_GOODS_SHIP.SHIPPING_ID,PART_ORDER_GOODS_SHIP.SHIPPING_NAME,PART_ORDER_GOODS_SHIP.SHIPPING_NO,PART_ORDER_GOODS_SHIP.SHIPPING_TIME,PART_ORDER_GOODS_SHIP.CONFIRM_TIME).from(PART_ORDER_GOODS_SHIP).where(PART_ORDER_GOODS_SHIP.ORDER_SN.eq(orderSn).and(PART_ORDER_GOODS_SHIP.ORDER_GOODS_ID.eq(recId))).fetchOne();
        if(record != null){
            return record.into(BaseShippingInfoVo.class);
        }else {
            return null;
        }
    }

    /**
     * 查询完成数
	 * 订单状态  (已收货 已完成)
	 * 退款状态 (默认,撤销,未通过,已完成,拒绝)
     * @param accountId
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer getCountFinishedNumByAccountIdUserId(Integer accountId,Integer userId, Integer storeId,Timestamp startTime,Timestamp endTime){
        SelectConditionStep<? extends Record> select=db().select(DSL.isnull(DSL.countDistinct(PART_ORDER_GOODS_SHIP.ORDER_SN),0))
				.from(PART_ORDER_GOODS_SHIP)
                .leftJoin(ORDER_INFO).on(PART_ORDER_GOODS_SHIP.ORDER_SN.eq(ORDER_INFO.ORDER_SN))
                .where(PART_ORDER_GOODS_SHIP.SHIPPING_ACCOUNT_ID.eq(accountId))
		//订单状态  (已收货 已完成)
		.and(ORDER_INFO.ORDER_STATUS.in(OrderConstant.ORDER_RECEIVED , OrderConstant.ORDER_FINISHED))
		//退款状态 (默认,撤销,未通过,已完成,拒绝)
		.and(ORDER_INFO.REFUND_STATUS.in(OrderConstant.REFUND_DEFAULT_STATUS,OrderConstant.REFUND_STATUS_CLOSE,
				OrderConstant.REFUND_STATUS_AUDIT_NOT_PASS,OrderConstant.REFUND_STATUS_FINISH,OrderConstant.REFUND_STATUS_REFUSE));
        if(storeId!=null){
            select.and(ORDER_INFO.STORE_ID.eq(storeId));
        }
        if(startTime!=null){
            select.and(PART_ORDER_GOODS_SHIP.CONFIRM_TIME.ge(startTime));
        }
        if(endTime!=null){
            select.and(PART_ORDER_GOODS_SHIP.CONFIRM_TIME.le(endTime));
        }
        return select.fetchAnyInto(Integer.class);
    }

    /**
     * 查询配送中数量
     * @param accountId
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer getCountDeliveryNumByAccountIdUserId(Integer accountId,Integer userId, Timestamp startTime,Timestamp endTime){
        SelectConditionStep<? extends Record> select=db().select(DSL.isnull(DSL.countDistinct(PART_ORDER_GOODS_SHIP.ORDER_SN),0))
				.from(PART_ORDER_GOODS_SHIP)
				.where(PART_ORDER_GOODS_SHIP.SHIPPING_ACCOUNT_ID.eq(accountId))
				.and(PART_ORDER_GOODS_SHIP.SHIPPING_USER_ID.eq(userId));
        if(startTime!=null){
            select.and(PART_ORDER_GOODS_SHIP.SHIPPING_TIME.ge(startTime));
        }
        if(endTime!=null){
            select.and(PART_ORDER_GOODS_SHIP.SHIPPING_TIME.le(endTime));
        }
        return select.fetchAnyInto(Integer.class);
    }


}
