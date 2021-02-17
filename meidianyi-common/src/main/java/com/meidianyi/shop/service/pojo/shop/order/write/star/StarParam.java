package com.meidianyi.shop.service.pojo.shop.order.write.star;

import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.StoreOrder.STORE_ORDER;

import java.util.Arrays;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Table;

/**
 * 标星参数
 * 
 * @author 王帅
 *
 */
public class StarParam {
	private String[] orderSn;
	/**
	 * 操作类型:0普通订单,1买单订单
	 */
	private Byte type;
	/**
	 * 状态:0否,1是
	 */
	private Byte starFlag;
	/**
	 * 操作表名:[0]普通订单,[1]买单订单
	 */
	private static Table<?>[] table = { ORDER_INFO, STORE_ORDER };
	
	/**
	 * 操作标星字段:[0]普通订单,[1]买单订单
	 * 字段类型java-Byte,mysql-tinyint
	 */
	private static Field<?>[] field = { ORDER_INFO.STAR_FLAG, STORE_ORDER.STAR_FLAG };
	
	private Condition where;

	public String[] getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(String[] orderSn) {
		this.orderSn = orderSn;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public Table<?> getTable() {
		return table[type];
	}

	@SuppressWarnings("unchecked")
	public Field<Byte> getField() {
		return (Field<Byte>) field[type];
	}

	public Byte getStarFlag() {
		return starFlag;
	}

	public void setStarFlag(Byte starFlag) {
		this.starFlag = starFlag;
	}	
	
	public Condition getWhere() {
		setWhere();
		return where;
	}
	
	public void setWhere() {
		switch (type) {
		case 0 :
			where = ORDER_INFO.ORDER_SN.in(orderSn);
			break;
		case 1 :
			where = STORE_ORDER.ORDER_SN.in(orderSn);
			break;
		default:
			where = null;
			break;
		}
	}

	@Override
	public String toString() {
		return "StarParam [orderSn=" + Arrays.toString(orderSn) + "]";
	}
}
