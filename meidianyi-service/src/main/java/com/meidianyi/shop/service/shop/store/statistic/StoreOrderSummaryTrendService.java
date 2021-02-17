package com.meidianyi.shop.service.shop.store.statistic;

import com.meidianyi.shop.common.pojo.shop.table.StoreOrderSummaryTrendDo;
import com.meidianyi.shop.dao.shop.store.StoreOrderSummaryTrendDao;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticAddVo;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticConstant;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticParam;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticPayVo;
import com.meidianyi.shop.service.shop.order.OrderReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author chenjie
 * @date 2020年08月27日
 */
@Service
public class StoreOrderSummaryTrendService {
    @Autowired
    protected StoreOrderSummaryTrendDao storeOrderSummaryTrendDao;

    @Autowired
    public OrderReadService orderReadService;
    /**
     * 添加记录
     *
     * @param param
     * @return
     */
    public void insertStoreStatistic(StoreOrderSummaryTrendDo param) {
        storeOrderSummaryTrendDao.insertStoreStatistic(param);
    }

    /**
     * 更新记录
     *
     * @param param
     * @return
     */
    public void updateStoreStatistic(StoreOrderSummaryTrendDo param) {
        storeOrderSummaryTrendDao.updateStoreStatistic(param);
    }

    /**
     * 查询记录
     *
     * @param param
     * @return
     */
    public StoreOrderSummaryTrendDo getStoreStatistic(StatisticParam param) {
        if (StatisticConstant.TYPE_TODAY.equals(param.getType())) {
            LocalDateTime today = LocalDate.now().atStartOfDay();
            param.setStartTime(Timestamp.valueOf(today));
            param.setEndTime(Timestamp.valueOf(today.plusDays(1)));
            return getStoreOrderSummary(param);
        } else {
            return storeOrderSummaryTrendDao.getStoreStatistic(param);
        }
    }

    /**
     * 统计门店订单数据
     * @param param
     */
    public void statisticStore(StatisticParam param) {
        StoreOrderSummaryTrendDo storeOrderSummaryTrendDo = getStoreOrderSummary(param);
        StoreOrderSummaryTrendDo hasStatisticInfo = getStoreStatistic(param);
        if (hasStatisticInfo != null) {
            storeOrderSummaryTrendDo.setId(hasStatisticInfo.getId());
            updateStoreStatistic(storeOrderSummaryTrendDo);
        } else {
            insertStoreStatistic(storeOrderSummaryTrendDo);
        }
    }

    public StoreOrderSummaryTrendDo getStoreOrderSummary(StatisticParam param) {
        StoreOrderSummaryTrendDo storeOrderSummaryTrendDo = new StoreOrderSummaryTrendDo();
        StatisticPayVo payData = orderReadService.getStoreOrderPayData(param);
        StatisticAddVo addData = orderReadService.getStoreOrderAddData(param);
        storeOrderSummaryTrendDo.setRefDate(param.getRefDate());
        storeOrderSummaryTrendDo.setOrderNum(addData.getOrderNum());
        storeOrderSummaryTrendDo.setOrderUserNum(addData.getUserNum());
        storeOrderSummaryTrendDo.setOrderPayNum(payData.getOrderNum());
        storeOrderSummaryTrendDo.setOrderPayUserNum(payData.getUserNum());
        storeOrderSummaryTrendDo.setTotalPaidMoney(payData.getTotalMoneyPaid());
        storeOrderSummaryTrendDo.setType(param.getType());
        storeOrderSummaryTrendDo.setStoreId(param.getStoreId());
        return storeOrderSummaryTrendDo;
    }
}
