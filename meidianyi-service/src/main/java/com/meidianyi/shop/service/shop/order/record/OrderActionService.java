package com.meidianyi.shop.service.shop.order.record;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.Tables;
import com.meidianyi.shop.db.shop.tables.OrderAction;
import com.meidianyi.shop.db.shop.tables.records.OrderActionRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreOrderListParam;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreOrderListVo;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.OrderAction.ORDER_ACTION;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;

;

/**
 * 	订单状态变化记录
 * @author 王帅
 *
 */
@Service
public class OrderActionService extends ShopBaseService{
	public final OrderAction TABLE = ORDER_ACTION;
	
	public void addRecord(OrderInfoVo order , OrderOperateQueryParam param, byte beforeStatus ,String desc) {
		OrderActionRecord record = db().newRecord(TABLE);
		record.setOrderId(order.getOrderId());
		record.setOrderSn(order.getOrderSn());
		record.setShopId(getShopId());
		record.setUserId(order.getUserId());
		record.setOrderStatus(beforeStatus);
		record.setActionNote(desc);
		if(param.getAdminInfo() != null) {
			record.setActionUser(param.getAdminInfo().getSysId() + "," + param.getAdminInfo().getUserName());
		}
		if(param.getWxUserInfo() != null){
			record.setUserOpenid(param.getWxUserInfo().getWxUser().getOpenId());
			if(param.getIsMp()!=null&&OrderConstant.IS_MP_STORE_CLERK==param.getIsMp()){
                record.setAccountId(param.getWxUserInfo().getStoreAccountId());
                record.setUserId(param.getWxUserInfo().getUserId());

            }
		}
        if(param.getStoreInfo()!=null){
            if(param.getIsMp()!=null&&OrderConstant.IS_MP_STORE_CLERK==param.getIsMp()){
                record.setAccountId(param.getStoreInfo().getStoreAccountId());
                record.setUserId(param.getStoreInfo().getStoreAuthInfoVo().getStoreAccountInfo().getUserId());
            }
        }
        if(param.getIsMp() != null && OrderConstant.IS_MP_AUTO == param.getIsMp()){
            record.setActionUser("cron");
            record.setActionNote("自动任务," + record.getActionNote());
        }else if (param.getIsMp() != null && OrderConstant.IS_MP_MQ == param.getIsMp()){
            record.setActionUser("mq");
            record.setActionNote("mq," + record.getActionNote());
        }
		record.insert();
	}
	
	public void addRecord(OrderInfoRecord order , OrderOperateQueryParam param, byte beforeStatus ,String desc) {
		OrderActionRecord record = db().newRecord(TABLE);
		record.setOrderId(order.getOrderId());
		record.setOrderSn(order.getOrderSn());
		record.setShopId(getShopId());
		record.setUserId(order.getUserId());
		record.setOrderStatus(beforeStatus);
        record.setActionNote(desc);
        if(param.getAdminInfo() != null) {
            record.setActionUser(param.getAdminInfo().getSysId() + "," + param.getAdminInfo().getUserName());
        }
        if(param.getWxUserInfo() != null){
            record.setUserOpenid(param.getWxUserInfo().getWxUser().getOpenId());
            if(param.getIsMp()!=null&&OrderConstant.IS_MP_STORE_CLERK==param.getIsMp()){
                record.setAccountId(param.getWxUserInfo().getStoreAccountId());
                record.setUserId(param.getWxUserInfo().getUserId());
            }
        }
        if(param.getStoreInfo()!=null){
            if(param.getIsMp()!=null&&OrderConstant.IS_MP_STORE_CLERK==param.getIsMp()){
                record.setAccountId(param.getStoreInfo().getStoreAccountId());
                record.setUserId(param.getStoreInfo().getStoreAuthInfoVo().getStoreAccountInfo().getUserId());
            }
        }
        if(param.getIsMp() != null && OrderConstant.IS_MP_AUTO == param.getIsMp()){
            record.setActionUser("cron");
            record.setActionNote("自动任务," + record.getActionNote());
        }else if (param.getIsMp() != null && OrderConstant.IS_MP_MQ == param.getIsMp()){
            record.setActionUser("mq");
            record.setActionNote("mq," + record.getActionNote());
        }else if (param.getIsMp() != null && OrderConstant.IS_MP_POS == param.getIsMp()){
            record.setActionUser("pos");
            record.setActionNote("pos" + record.getActionNote());
        }
		record.insert();
	}

    /**
     * 数量单数
     * @param accountId
     * @param orderStatus
     * @param storesId
     * @return
     */
	public Integer getCountNumByUserIdOrderStatus(Integer accountId, Byte orderStatus, Integer storesId){
	    return db().selectCount().from(TABLE)
            .leftJoin(ORDER_INFO).on(TABLE.ORDER_ID.eq(ORDER_INFO.ORDER_ID))
            .where(TABLE.ACCOUNT_ID.eq(accountId)).and(TABLE.ORDER_STATUS.eq(orderStatus))
            .and(ORDER_INFO.STORE_ID.eq(storesId))
            .fetchAnyInto(Integer.class);
    }
    /**
     * 时间查询数量单数
     * @param accountId
     * @param orderStatus
     * @param storesIds
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer getCountNumByUserIdOrderStatusAndTime(Integer accountId, Byte orderStatus, List<Integer> storesIds, Timestamp startTime,Timestamp endTime){
        return db().selectCount().from(TABLE)
            .leftJoin(ORDER_INFO).on(TABLE.ORDER_ID.eq(ORDER_INFO.ORDER_ID))
            .where(TABLE.ACCOUNT_ID.eq(accountId)).and(TABLE.ORDER_STATUS.eq(orderStatus))
            .and(ORDER_INFO.STORE_ID.in(storesIds))
            .and(TABLE.CREATE_TIME.ge(startTime))
            .and(TABLE.CREATE_TIME.le(endTime))
            .fetchAnyInto(Integer.class);
    }
    /**
     * 获取门店用户完成的订单操作
     * @param param
     * @return
     */
    public PageResult<StoreOrderListVo> getStoreClerkOrderFinishedList(StoreOrderListParam param){
        SelectJoinStep<? extends Record> select = db().select(ORDER_INFO.ORDER_ID,ORDER_INFO.ORDER_SN,ORDER_INFO.ORDER_STATUS,ORDER_INFO.MOBILE,ORDER_INFO.ADDRESS_ID,ORDER_INFO.COMPLETE_ADDRESS)
            .from(Tables.ORDER_ACTION)
            .leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_ID.eq(Tables.ORDER_ACTION.ORDER_ID));
        select.where(ORDER_INFO.STORE_ID.eq(param.getStoreId())).and(Tables.ORDER_ACTION.ACCOUNT_ID.eq(param.getStoreAccountId()))
            .and(Tables.ORDER_ACTION.ORDER_STATUS.eq(OrderConstant.ORDER_RECEIVED))
            .orderBy(Tables.ORDER_ACTION.CREATE_TIME.desc());
        return getPageResult(select,param.getCurrentPage(),param.getPageRows(),StoreOrderListVo.class);
    }
}
