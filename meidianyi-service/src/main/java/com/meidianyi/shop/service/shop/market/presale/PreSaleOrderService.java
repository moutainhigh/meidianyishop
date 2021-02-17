package com.meidianyi.shop.service.shop.market.presale;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderGoodsListVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListVo;
import com.meidianyi.shop.service.pojo.shop.market.presale.DetailListVo;
import com.meidianyi.shop.service.pojo.shop.market.presale.OrderExcelVo;
import com.meidianyi.shop.service.pojo.shop.market.presale.OrderListParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.OrderMust.ORDER_MUST;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * 定金膨胀活动订单
 *
 * @author 郑保乐
 */
@Service
public class PreSaleOrderService extends ShopBaseService {

    /**
     * 获取活动明细
     */
    public PageResult<DetailListVo> getPreSaleDetail(OrderListParam param) {
        SelectConditionStep<?> query = orderQuery(param);
        return getPageResult(query, param, DetailListVo.class);
    }

    /**
     * 获取活动订单列表
     */
    public PageResult<MarketOrderListVo> getOrderList(MarketOrderListParam param) {
        return saas().getShopApp(getShopId()).readOrder.getMarketOrderList(param,BaseConstant.ACTIVITY_TYPE_PRE_SALE);
    }

    /**
     * 查询订单
     */
    private SelectConditionStep<? extends Record> orderQuery(OrderListParam param) {
        SelectConditionStep<? extends Record> query = db().select(ORDER_INFO.ORDER_SN,
            ORDER_GOODS.GOODS_NAME, ORDER_GOODS.GOODS_IMG, ORDER_INFO.USER_ID, ORDER_GOODS.ORDER_ID,
            ORDER_INFO.CREATE_TIME, ORDER_INFO.MOBILE, ORDER_MUST.CONSIGNEE_REAL_NAME, ORDER_INFO.GOODS_AMOUNT,
            ORDER_INFO.CONSIGNEE,ORDER_INFO.ORDER_STATUS,ORDER_INFO.ORDER_STATUS_NAME, ORDER_INFO.ORDER_AMOUNT, ORDER_INFO.MONEY_PAID, ORDER_INFO.SHIPPING_FEE,USER.USERNAME)
            .from(ORDER_INFO)
            .leftJoin(ORDER_GOODS).on(ORDER_GOODS.ORDER_ID.eq(ORDER_INFO.ORDER_ID))
            .leftJoin(ORDER_MUST).on(ORDER_MUST.ORDER_SN.eq(ORDER_INFO.ORDER_SN))
            .leftJoin(USER).on(USER.USER_ID.eq(ORDER_INFO.USER_ID))
            .where(ORDER_INFO.GOODS_TYPE.likeRegex(OrderInfoService.getGoodsTypeToSearch(new Byte[] {BaseConstant.ACTIVITY_TYPE_PRE_SALE}))
                .and(ORDER_INFO.ACTIVITY_ID.eq(param.getId())));
        buildOptions(query, param);
        query.orderBy(ORDER_INFO.CREATE_TIME.desc());
        return query;
    }

    private void buildOptions(SelectConditionStep<? extends Record> select, OrderListParam param) {
        String mobile = param.getMobile();
        String orderSn = param.getOrderSn();
        String username = param.getUsername();
        if (isNotEmpty(orderSn)) {
            select.and(ORDER_INFO.ORDER_SN.contains(orderSn));
        }
        if (isNotEmpty(mobile)) {
            select.and(ORDER_INFO.MOBILE.contains(mobile));
        }
        if (isNotEmpty(username)) {
            select.and(USER.USERNAME.contains(username));
        }
    }

    /**
     * 导出订单 Excel
     */
    public Workbook exportOrderList(MarketOrderListParam param, String lang) {
        List<MarketOrderListVo> list = saas.getShopApp(getShopId()).readOrder.marketOrderInfo.getMarketOrderList(param, BaseConstant.ACTIVITY_TYPE_PRE_SALE);
        List<OrderExcelVo> res = new ArrayList<>();
        list.forEach(o->{
            OrderExcelVo vo = new OrderExcelVo();
            List<MarketOrderGoodsListVo> orderGoods = o.getGoods();
            //目前一个活动只针对一件商品，只需过滤掉赠品后取第一个
            orderGoods = orderGoods.stream().filter((MarketOrderGoodsListVo g)->g.getIsGift().equals(OrderConstant.IS_GIFT_N)).collect(Collectors.toList());
            vo.setOrderSn(o.getOrderSn());
            vo.setGoodsName(orderGoods.get(0).getGoodsName());
            vo.setOrderTime(o.getCreateTime());
            vo.setOrderStatus(OrderConstant.getOrderStatusName(o.getOrderStatus(),lang));
            vo.setGoodsAmount(orderGoods.get(0).getGoodsNumber());
            vo.setConsignee(o.getConsignee() + ";" + o.getMobile());
            vo.setMoney(o.getMoneyPaid());
            res.add(vo);
        });
        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(res, OrderExcelVo.class);
        return workbook;
    }
}
