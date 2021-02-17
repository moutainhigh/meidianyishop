package com.meidianyi.shop.dao.shop.goods;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.service.pojo.shop.goods.goodsanalysis.GoodsAnalysisListParam;
import com.meidianyi.shop.service.pojo.shop.goods.goodsanalysis.GoodsAnalysisListVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticConstant;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.service.pojo.shop.goods.goodsanalysis.GoodsAnalysisListParam.*;

/**
 * @author chenjie
 * @date 2020年09月17日
 */
@Repository
public class GoodsAnalysisDao extends ShopBaseDao {
    public PageResult<GoodsAnalysisListVo> getGoodsAnalysisPageList(GoodsAnalysisListParam param){
        SelectHavingStep<Record3<Integer, Integer, Integer>> goodsBrowseTable = getGoodsBrowseTable(param);
        SelectHavingStep<Record2<Integer, Integer>> goodsAddCartTable = getGoodsAddCartTable(param);
        SelectHavingStep<Record2<Integer, BigDecimal>> goodsCollectionTable = getGoodsCollectionTable(param);
        SelectHavingStep<Record3<Integer, BigDecimal, BigDecimal>> goodsSaleTable = getGoodsSaleTable(param);
        SelectJoinStep<? extends Record> select = db()
            .select(GOODS.GOODS_ID,GOODS.GOODS_NAME,GOODS_MEDICAL_INFO.GOODS_QUALITY_RATIO,GOODS_MEDICAL_INFO.GOODS_PRODUCTION_ENTERPRISE
                    ,goodsBrowseTable.field(UV),goodsBrowseTable.field(PV)
                ,goodsAddCartTable.field(ADD_CART_USER_NUM),goodsCollectionTable.field(COLLECTION_INCREMENT_NUM)
                ,goodsSaleTable.field(SALE_NUM),goodsSaleTable.field(SALE_MONEY)
            )
            .from(GOODS)
            .leftJoin(GOODS_MEDICAL_INFO).on(GOODS_MEDICAL_INFO.GOODS_ID.eq(GOODS.GOODS_ID))
            .leftJoin(goodsBrowseTable).on(goodsBrowseTable.field(USER_GOODS_RECORD.GOODS_ID).eq(GOODS.GOODS_ID))
            .leftJoin(goodsAddCartTable).on(goodsAddCartTable.field(USER_CART_RECORD.GOODS_ID).eq(GOODS.GOODS_ID))
            .leftJoin(goodsCollectionTable).on(goodsCollectionTable.field(USER_COLLECTION_ACTION.GOODS_ID).eq(GOODS.GOODS_ID))
            .leftJoin(goodsSaleTable).on(goodsSaleTable.field(ORDER_GOODS.GOODS_ID).eq(GOODS.GOODS_ID))
            ;
        select.where(GOODS.DEL_FLAG.eq((byte) 0));
        if (param.getOrderField() != null) {
            goodsFiledSorted(select, param);
        } else {
            select.orderBy(GOODS.GOODS_ID.desc());
        }
        return this.getPageResult(select, param.getCurrentPage(),
            param.getPageRows(), GoodsAnalysisListVo.class);
    }

    /**
     * 获取商品浏览统计子查询
     * @return
     */
    public SelectHavingStep<Record3<Integer, Integer, Integer>> getGoodsBrowseTable(GoodsAnalysisListParam param){
        return db().select(USER_GOODS_RECORD.GOODS_ID
            , DSL.count(USER_GOODS_RECORD.ID).as(PV)
            ,DSL.countDistinct(USER_GOODS_RECORD.USER_ID).as(UV)
        )
            .from(USER_GOODS_RECORD)
            .where(USER_GOODS_RECORD.CREATE_TIME.ge(param.getStartTime()))
            .and(USER_GOODS_RECORD.CREATE_TIME.le(param.getEndTime()))
            .groupBy(USER_GOODS_RECORD.GOODS_ID);
    }

    /**
     * 获取商品加购统计子查询
     * @return
     */
    public SelectHavingStep<Record2<Integer, Integer>> getGoodsAddCartTable(GoodsAnalysisListParam param){
        return db().select(USER_CART_RECORD.GOODS_ID
            ,DSL.countDistinct(USER_CART_RECORD.USER_ID).as(ADD_CART_USER_NUM)
        )
            .from(USER_CART_RECORD)
            .where(USER_CART_RECORD.CREATE_TIME.ge(param.getStartTime()))
            .and(USER_CART_RECORD.CREATE_TIME.le(param.getEndTime()))
            .and(USER_CART_RECORD.DEL_FLAG.eq((short) BaseConstant.NO))
            .groupBy(USER_CART_RECORD.GOODS_ID);
    }

    /**
     * 获取商品收藏统计子查询
     * @return
     */
    public SelectHavingStep<Record2<Integer, BigDecimal>> getGoodsCollectionTable(GoodsAnalysisListParam param){
        return db().select(USER_COLLECTION_ACTION.GOODS_ID
            ,DSL.sum(USER_COLLECTION_ACTION.COLLECTION_TYPE).as(COLLECTION_INCREMENT_NUM)
        )
            .from(USER_COLLECTION_ACTION)
            .where(USER_COLLECTION_ACTION.CREATE_TIME.ge(param.getStartTime()))
            .and(USER_COLLECTION_ACTION.CREATE_TIME.le(param.getEndTime()))
            .groupBy(USER_COLLECTION_ACTION.GOODS_ID);
    }

    /**
     * 获取商品出售统计子查询
     * @return
     */
    public SelectHavingStep<Record3<Integer, BigDecimal, BigDecimal>> getGoodsSaleTable(GoodsAnalysisListParam param){
        return db().select(ORDER_GOODS.GOODS_ID
            , DSL.sum(ORDER_GOODS.GOODS_NUMBER).as(SALE_NUM)
            ,DSL.sum(ORDER_GOODS.GOODS_NUMBER.multiply(ORDER_GOODS.GOODS_PRICE)).as(SALE_MONEY)
        )
            .from(ORDER_GOODS)
            .leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_SN.eq(ORDER_GOODS.ORDER_SN))
            .where(ORDER_INFO.ORDER_STATUS.ge(OrderConstant.ORDER_WAIT_DELIVERY))
            .and(ORDER_INFO.PAY_TIME.ge(param.getStartTime()))
            .and(ORDER_INFO.PAY_TIME.le(param.getEndTime()))
            .groupBy(ORDER_GOODS.GOODS_ID);
    }

    /**
     * 商品统计查询
     *
     * @param select
     * @param param
     */
    protected void buildOptions(SelectJoinStep<? extends Record> select, GoodsAnalysisListParam param) {

    }

    /**
     * 对商品统计按指定字段进行排序
     * @param select 查询实体
     * @param param 排序参数
     */
    private void goodsFiledSorted(SelectJoinStep<? extends Record> select, GoodsAnalysisListParam param) {
        if (ASC.equals(param.getOrderDirection())) {
            switch (param.getOrderField()) {
                case PV:
                    select.orderBy(DSL.field(PV).asc());
                    break;
                case UV:
                    select.orderBy(DSL.field(UV).asc());
                    break;
                case ADD_CART_USER_NUM:
                    select.orderBy(DSL.field(ADD_CART_USER_NUM).asc());
                    break;
                case COLLECTION_INCREMENT_NUM:
                    select.orderBy(DSL.field(COLLECTION_INCREMENT_NUM).asc());
                    break;
                case SALE_NUM:
                    select.orderBy(DSL.field(SALE_NUM).asc());
                    break;
                case SALE_MONEY:
                    select.orderBy(DSL.field(SALE_MONEY).asc());
                    break;
                default:
                    break;
            }
        } else {
            switch (param.getOrderField()) {
                case PV:
                    select.orderBy(DSL.field(PV).desc());
                    break;
                case UV:
                    select.orderBy(DSL.field(UV).desc());
                    break;
                case ADD_CART_USER_NUM:
                    select.orderBy(DSL.field(ADD_CART_USER_NUM).desc());
                    break;
                case COLLECTION_INCREMENT_NUM:
                    select.orderBy(DSL.field(COLLECTION_INCREMENT_NUM).desc());
                    break;
                case SALE_NUM:
                    select.orderBy(DSL.field(SALE_NUM).desc());
                    break;
                case SALE_MONEY:
                    select.orderBy(DSL.field(SALE_MONEY).desc());
                    break;
                default:
                    break;
            }
        }
    }
}
