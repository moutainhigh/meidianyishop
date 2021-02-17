package com.meidianyi.shop.service.shop.task.table;

import cn.hutool.core.date.DateUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.dao.shop.order.InquiryOrderDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.wxapp.account.UserSysVo;
import com.meidianyi.shop.service.saas.order.SaasOrderService;
import com.meidianyi.shop.service.saas.order.SaasReturnOrderService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * 同步表的操作
 *
 * @author zhaojianqiang
 * @time 下午2:28:30
 */
@Service
@Slf4j
public class TableTaskService extends ShopBaseService {
	@Autowired
	private UserService userService;
	@Autowired
	private SaasOrderService saasOrderService;
	@Autowired
	private SaasReturnOrderService saasReturnOrderService;
	@Autowired
    private InquiryOrderDao inquiryOrderDao;


	private static com.meidianyi.shop.db.shop.Tables SHOP_TABLES;

	public void userSys() {
		log.info("#####################开始同步店：" + getShopId() + "的user#####################");
		// userService.synchronize();
		UserSysVo vo = userService.synchronizeUser();
		log.info("店铺{}，用户总数：{}，更新成功：{}，更新失败：{}，插入成功：{}，插入失败：{}", getShopId(), vo.getSum(), vo.getUpdateSuccess(),
				vo.getUpdateFail(), vo.getInsertSuccess(), vo.getInsertFail());
		log.info("#####################结束同步店：" + getShopId() + "的user#####################");

		log.info("#####################开始同步店：" + getShopId() + "的userDetail#####################");
		// userService.synchronize();
		UserSysVo vo2 = userService.synchronizeUserDetail();
		log.info("店铺{}，用户详情总数：{}，更新成功：{}，更新失败：{}，插入成功：{}，插入失败：{}", getShopId(), vo2.getSum(), vo2.getUpdateSuccess(),
				vo2.getUpdateFail(), vo2.getInsertSuccess(), vo2.getInsertFail());
		log.info("#####################结束同步店：" + getShopId() + "的userDetail#####################");
	}




    /**
     * 同步问诊订单
     */
    public void inquiryOrderSynchronize(Integer shopId){
        logger().info("【同步任务】---问诊订单数据同步到主库shopId:{}",shopId);
        //新增
        List<InquiryOrderDo> newInquiryOrderDoList= inquiryOrderDao.getListByCreateTime(DateUtils.getTimestampForStartTime(-1),DateUtils.getTimestampForEndTime(-1));
        saas().mainInquiryOrderService.inquiryOrderSynchronizeInsert(newInquiryOrderDoList);

        //更新
        List<InquiryOrderDo> inquiryOrderDoList= inquiryOrderDao.getListByUpdateTime(DateUtils.getTimestampForStartTime(-1),DateUtils.getTimestampForEndTime(-1));
        saas().mainInquiryOrderService.inquiryOrderSynchronizeUpdate(inquiryOrderDoList,shopId);




	}
	/**
	 * 增量更新最近一天的数据
	 */
	public void orderDeltaUpdates(Integer shopId) {
		log.info("#####################开始同步店：" + getShopId() + "的order_Info#####################");
		Timestamp beginTime = DateUtil.beginOfDay(DateUtil.date()).toTimestamp();
		Timestamp endTime = DateUtil.endOfDay(DateUtil.date()).toTimestamp();
		//同步新增order_info
		saasOrderService.synOrderCreate(beginTime, endTime,shopId);
		//同步订单更新
		saasOrderService.synOrderUpdate(beginTime, endTime,shopId);
		//同步新增订单商品
		saasOrderService.synOrderGoodsCreate(beginTime, endTime,shopId);
		//同步订单商品更新
		saasOrderService.synOrderGoodsUpdate(beginTime, endTime,shopId);
	}
	/**
	 * 增量更新最近一天的数据
	 */
	public void ruturnOrderDeltaUpdates(Integer shopId) {
		log.info("#####################开始同步店：" + getShopId() + "的ruturn_order#####################");
		Timestamp beginTime = DateUtil.beginOfDay(DateUtil.date()).toTimestamp();
		Timestamp endTime = DateUtil.endOfDay(DateUtil.date()).toTimestamp();
		//同步新增order_info
		saasReturnOrderService.synOrderCreate(beginTime, endTime,shopId);
		//同步订单更新
		saasReturnOrderService.synOrderUpdate(beginTime, endTime,shopId);
		log.info("#####################开始同步店：" + getShopId() + "的ruturn_order_goods#####################");
		//同步新增订单商品
		saasReturnOrderService.synOrderGoodsCreate(beginTime, endTime,shopId);
		//同步订单商品更新
		saasReturnOrderService.synOrderGoodsUpdate(beginTime, endTime,shopId);
	}


}
