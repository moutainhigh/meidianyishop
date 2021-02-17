package com.meidianyi.shop.service.shop.task;

import com.meidianyi.shop.service.shop.task.goods.FootprintDeleteTaskService;
import com.meidianyi.shop.service.shop.task.goods.GoodsPvUpdateTaskService;
import com.meidianyi.shop.service.shop.task.market.*;
import com.meidianyi.shop.service.shop.task.order.OrderTaskService;
import com.meidianyi.shop.service.shop.task.overview.GoodsStatisticTaskService;
import com.meidianyi.shop.service.shop.task.overview.StatisticalTableInsert;
import com.meidianyi.shop.service.shop.task.table.TableTaskService;
import com.meidianyi.shop.service.shop.task.wechat.MaMpScheduleTaskService;
import com.meidianyi.shop.service.shop.task.wechat.WechatTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author luguangyao
 */
@Service
public class ShopTaskService {

    @Autowired
    public WechatTaskService wechatTaskService;

    @Autowired
    public SeckillTaskService seckillTaskService;

    @Autowired
    public ReducePriceTaskService reducePriceTaskService;

    @Autowired
    public GroupBuyTaskService groupBuyTaskService;

    @Autowired
    public BargainTaskService bargainTaskService;

    @Autowired
    public PreSaleTaskService preSaleTaskService;

    @Autowired
    public OrderTaskService orderTaskService;
    /**
     * 奖品
     */
    @Autowired
    public MyPrizeTaskService prizeTaskService;

    @Autowired
    public CouponPackTaskService couponPackTaskService;

    /**
     * The Goods task service.概况模块-商品统计
     */
    @Autowired
    public GoodsStatisticTaskService goodsStatisticTaskService;

    @Autowired
    public FootprintDeleteTaskService footprintDeleteTaskService;

    @Autowired
    public StatisticalTableInsert statisticalTableInsert;

    @Autowired
    public MaMpScheduleTaskService maMpScheduleTaskService;
    @Autowired
    public TableTaskService tableTaskService;

    @Autowired
    public GoodsPvUpdateTaskService goodsPvUpdateTaskService;
}

