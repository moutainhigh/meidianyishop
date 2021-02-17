package com.meidianyi.shop.dao.shop.order;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.OrderGoodsDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.goods.store.OrderStoreGoodsBo;
import com.meidianyi.shop.service.pojo.shop.order.goods.store.OrderStorePosBo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.OrderPrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.PrescriptionQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.audit.OrderGoodsSimpleAuditVo;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.Tables.ORDER_INFO;
import static com.meidianyi.shop.db.shop.Tables.STORE;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.ReturnOrderGoods.RETURN_ORDER_GOODS;

/**
 * @author 孔德成
 * @date 2020/7/13 14:41
 */
@Repository
public class OrderGoodsDao extends ShopBaseDao {

    /**
     * 根据订单号获取订单商品的规格id
     *
     * @param orderSn 订单号
     * @return 规格ids
     */
    public List<Integer> getProductIdByOrderSn(String orderSn) {
        return db().select(ORDER_GOODS.PRODUCT_ID).from(ORDER_GOODS).where(ORDER_GOODS.ORDER_SN.eq(orderSn)).fetch(ORDER_GOODS.PRODUCT_ID);
    }

    /**
     * 待审核处方列表
     *
     * @return
     */
    public PageResult<OrderPrescriptionVo> listGoodsOldPrescription(PrescriptionQueryParam param) {
        SelectConditionStep<Record2<Integer, String>> where = db()
                .select(ORDER_GOODS.ORDER_ID, ORDER_GOODS.PRESCRIPTION_OLD_CODE)
                .from(ORDER_GOODS)
                .leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_ID.eq(ORDER_GOODS.ORDER_ID))
                .where(ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_TO_AUDIT));
        if (param.getAuditType() != null) {
            where.and(ORDER_GOODS.MEDICAL_AUDIT_TYPE.eq(param.getAuditType()));
        }
        if (param.getAuditStatus() != null) {
            where.and(ORDER_GOODS.MEDICAL_AUDIT_STATUS.eq(param.getAuditStatus()));
        }
        where.groupBy(ORDER_GOODS.ORDER_ID, ORDER_GOODS.PRESCRIPTION_OLD_CODE)
        .orderBy(ORDER_GOODS.CREATE_TIME.desc());
        return getPageResult(where, param.getCurrentPage(), param.getPageRows(), OrderPrescriptionVo.class);
    }

    /**
     * 待审核处方数量
     * @return
     */
    public Integer countAuditOrder(){
        SelectSeekStep1<Record1<Integer>, Timestamp> record1s = db().selectCount()
                .from(ORDER_GOODS)
                .leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_ID.eq(ORDER_GOODS.ORDER_ID))
                .where(ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_TO_AUDIT))
                .and(ORDER_GOODS.MEDICAL_AUDIT_STATUS.eq(OrderConstant.MEDICAL_AUDIT_DEFAULT))
                .and(ORDER_GOODS.MEDICAL_AUDIT_TYPE.eq(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_AUDIT))
                .groupBy(ORDER_GOODS.ORDER_ID, ORDER_GOODS.PRESCRIPTION_OLD_CODE)
                .orderBy(ORDER_GOODS.CREATE_TIME.desc());
        return db().fetchCount(record1s);
    }

    /**
     *  @param orderIdList
     * @param prescriptionCodeList
     * @return
     */
    public Map<Integer, List<OrderGoodsDo>> mapOrderGoodsByOrderId(List<Integer> orderIdList, List<String> prescriptionCodeList) {
        return db().select()
                .from(ORDER_GOODS)
                .where(ORDER_GOODS.ORDER_ID.in(orderIdList))
                .and(ORDER_GOODS.PRESCRIPTION_OLD_CODE.in(prescriptionCodeList))
                .and(ORDER_GOODS.MEDICAL_AUDIT_TYPE.eq(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_AUDIT))
                .and(ORDER_GOODS.MEDICAL_AUDIT_STATUS.eq(OrderConstant.MEDICAL_AUDIT_DEFAULT))
                .orderBy(ORDER_GOODS.CREATE_TIME.desc())
                .fetchGroups(ORDER_GOODS.ORDER_ID, OrderGoodsDo.class);

    }


    /**
     * 更改处方号
     * @param orderId
     * @param prescriptionCode
     */
    public void updatePrescriptionCode(Integer orderId,String prescriptionCode) {
        db().update(ORDER_GOODS).set(ORDER_GOODS.PRESCRIPTION_CODE, prescriptionCode).where(ORDER_GOODS.ORDER_ID.eq(orderId)).execute();
    }



    /**
     * 审核订单商品状态
     * @param recIds
     * @param auditStatus
     */
    public void updateAuditStatusByRecIds(List<Integer> recIds, byte auditStatus) {
        db().update(ORDER_GOODS)
                .set(ORDER_GOODS.MEDICAL_AUDIT_STATUS,auditStatus)
                .set(ORDER_GOODS.AUDIT_TIME,DSL.now())
                .where(ORDER_GOODS.REC_ID.in(recIds)).execute();
    }


    /**
     * 审核订单商品状态
     * @param recIds
     * @param auditStatus
     */
    public void updateAuditStatusByRecIds(List<Integer> recIds, byte auditStatus,String prescriptionCode) {
        db().update(ORDER_GOODS)
                .set(ORDER_GOODS.MEDICAL_AUDIT_STATUS,auditStatus)
                .set(ORDER_GOODS.PRESCRIPTION_CODE,prescriptionCode)
                .set(ORDER_GOODS.AUDIT_TIME,DSL.now())
                .where(ORDER_GOODS.REC_ID.in(recIds)).execute();
    }


    /**
     * 获取审核订单信息
     * @param orderId 订单id
     * @return
     */
    public List<OrderGoodsSimpleAuditVo> listSimpleAuditByOrderId(Integer orderId) {
        return db().select(ORDER_GOODS.REC_ID,ORDER_GOODS.PRESCRIPTION_OLD_CODE,ORDER_GOODS.PRESCRIPTION_CODE,
                ORDER_GOODS.MEDICAL_AUDIT_TYPE,ORDER_GOODS.MEDICAL_AUDIT_STATUS,ORDER_GOODS.GOODS_ID,
                ORDER_GOODS.GOODS_NUMBER,ORDER_GOODS.PRODUCT_ID,GOODS.GOODS_IMG,GOODS.SHOP_PRICE)
                .from(ORDER_GOODS)
                .leftJoin(GOODS).on(ORDER_GOODS.GOODS_ID.eq(GOODS.GOODS_ID))
                .where(ORDER_GOODS.ORDER_ID.eq(orderId))
                .fetchInto(OrderGoodsSimpleAuditVo.class);
    }


    public Integer medicalSalesReport(){
        db().select().from(ORDER_GOODS).where().fetch();

        return null;
    }


    public List<OrderGoodsDo> listUpdateOrderGoodsByYesterday(Timestamp beginTime, Timestamp endTime) {
        return  db().selectFrom(ORDER_GOODS)
                .where(ORDER_GOODS.UPDATE_TIME.ge(beginTime)).and(ORDER_GOODS.UPDATE_TIME.le(endTime))
                .and(ORDER_GOODS.CREATE_TIME.le(beginTime))
                .fetchInto(OrderGoodsDo.class);
    }

    public List<OrderGoodsDo> listCreateOrderGoodsByYesterday(Timestamp beginTime, Timestamp endTime) {
        return  db().selectFrom(ORDER_GOODS)
                .where(ORDER_GOODS.CREATE_TIME.ge(beginTime)).and(ORDER_GOODS.CREATE_TIME.le(endTime))
                .fetchInto(OrderGoodsDo.class);
    }

    /**
     * 根据订单号获取处方号列表
     * @param orderSn
     * @return
     */
    public List<String> getPrescriptionCodeListByOrderSn(String orderSn){
        return db().select(ORDER_GOODS.PRESCRIPTION_CODE).from(ORDER_GOODS).where(ORDER_GOODS.ORDER_SN.eq(orderSn))
            .fetchInto(String.class);
    }

    /**
     * 根据订单号和goodId获取
     * @param orderId
     * @param goodsId
     * @return
     */
    public OrderGoodsDo getByOrderIdGoodsIdPrdId(Integer orderId,Integer goodsId,Integer prdId){
        return db().select().from(ORDER_GOODS).where(ORDER_GOODS.GOODS_ID.eq(goodsId))
            .and(ORDER_GOODS.ORDER_ID.eq(orderId)).and(ORDER_GOODS.PRODUCT_ID.eq(prdId))
            .fetchAnyInto(OrderGoodsDo.class);
    }

    /**
     * 根据处方号和goodId获取
     * @param prescriptionCode
     * @param goodsId
     * @return
     */
    public OrderGoodsDo getByPreCodeGoodsIdPrdId(String prescriptionCode,Integer goodsId,Integer prdId){
        return db().select().from(ORDER_GOODS).where(ORDER_GOODS.GOODS_ID.eq(goodsId))
            .and(ORDER_GOODS.PRESCRIPTION_CODE.eq(prescriptionCode)).and(ORDER_GOODS.PRODUCT_ID.eq(prdId))
            .fetchAnyInto(OrderGoodsDo.class);
    }

    /**
     * 根据处方号查药品
     * @param prescriptionCode 处方号
     * @return List<OrderGoodsDo>
     */
    public List<OrderGoodsDo> getByPrescription(String prescriptionCode) {
        return db().select().from(ORDER_GOODS)
            .where(ORDER_GOODS.PRESCRIPTION_CODE.eq(prescriptionCode))
            .fetchInto(OrderGoodsDo.class);
    }

    /**
     * 根据处方明细号查
     * @param prescriptionDetailCode
     * @return
     */
    public OrderGoodsDo getByPrescriptionDetailCode(String prescriptionDetailCode){
        return db().select()
            .from(ORDER_GOODS).where(ORDER_GOODS.PRESCRIPTION_DETAIL_CODE.eq(prescriptionDetailCode))
            .fetchAnyInto(OrderGoodsDo.class);

    }
    /**
     * 更新处方明细号
     * @param recId
     * @param prescriptionDetailCode
     */
    public void updatePrescriptionDetailCode(Integer recId,String prescriptionDetailCode){
        db().update(ORDER_GOODS).set(ORDER_GOODS.PRESCRIPTION_DETAIL_CODE,prescriptionDetailCode)
            .where(ORDER_GOODS.REC_ID.eq(recId)).execute();
    }

    /**
     * 获取订单的orderGoods
     * @param orderId
     * @return
     */
    public List<OrderGoodsVo> getOrderGoodsListByOrderId(Integer orderId){
        return db().select().from(ORDER_GOODS).where(ORDER_GOODS.ORDER_ID.eq(orderId))
            .fetchInto(OrderGoodsVo.class);
    }

    /**
     *
     * @param recId
     * @return
     */
    public OrderGoodsRecord getByRecId(Integer recId){
        return db().select().from(ORDER_GOODS).where(ORDER_GOODS.REC_ID.eq(recId))
            .fetchOneInto(OrderGoodsRecord.class);
    }


    public void getCanShipGoods(Integer orderId){
        db().select(ORDER_GOODS.asterisk(),
                ORDER_GOODS.GOODS_NUMBER.sub(ORDER_GOODS.RETURN_NUMBER)
                        .sub(ORDER_GOODS.SEND_NUMBER)
                        .sub(DSL.sum(RETURN_ORDER_GOODS.GOODS_NUMBER)))
                .from(ORDER_GOODS)
                .leftJoin(RETURN_ORDER_GOODS)
                    .on(RETURN_ORDER_GOODS.ORDER_SN.eq(ORDER_GOODS.ORDER_SN)
                    .and(RETURN_ORDER_GOODS.SUCCESS.eq(OrderConstant.SUCCESS_RETURNING))
                    )
                .where(ORDER_GOODS.ORDER_ID.eq(orderId))
                .and(ORDER_GOODS.SEND_NUMBER.eq(0))
                .groupBy()
                .fetchAny();
    }

    public List<OrderGoodsDo> getByOrderId(Integer orderId) {
        return db().select().from(ORDER_GOODS).where(ORDER_GOODS.ORDER_ID.eq(orderId)).fetchInto(OrderGoodsDo.class);
    }

    /**
     * 查询推送pos所需参数信息
     * @param orderSn 订单sn
     * @return OrderStorePosBo
     */
    public OrderStorePosBo getOrderToPharmacyPos(String orderSn){
        OrderStorePosBo orderStorePosBo = db().select(ORDER_INFO.ORDER_SN.as("orderSn")
            , STORE.STORE_CODE.as("shopSn")
            , ORDER_INFO.PAY_TIME.as("orderTime")
            , ORDER_INFO.CONSIGNEE.as("userName")
            , ORDER_INFO.MOBILE.as("userPhone")
            , ORDER_INFO.COMPLETE_ADDRESS.as("orderAddress")
            , ORDER_INFO.DELIVER_TYPE.as("isPickUp")
            , ORDER_INFO.ORDER_AMOUNT.as("goodsSumPrice")
            , ORDER_INFO.ADD_MESSAGE.as("orderMemo"))
            .from(ORDER_INFO)
            .leftJoin(STORE)
            .on(STORE.STORE_ID.eq(ORDER_INFO.STORE_ID))
            .where(ORDER_INFO.ORDER_SN.eq(orderSn)).fetchAnyInto(OrderStorePosBo.class);
        if (orderStorePosBo.getIsPickUp() != 1) {
            orderStorePosBo.setIsPickUp((byte) 0);
        }
        List<OrderStoreGoodsBo> orderStoreGoodsBos = db().select(ORDER_GOODS.GOODS_NAME.as("goodsCommonName")
            , ORDER_GOODS.GOODS_QUALITY_RATIO.as("goodsQualityRatio")
            , ORDER_GOODS.GOODS_PRODUCTION_ENTERPRISE.as("goodsProductionEnterprise")
            , ORDER_GOODS.GOODS_APPROVAL_NUMBER.as("goodsApprovalNumber")
            , ORDER_GOODS.GOODS_SN.as("goodsCode")
            , ORDER_GOODS.GOODS_NUMBER.as("goodsNumber")
            , ORDER_GOODS.DISCOUNTED_TOTAL_PRICE.as("goodsPrice")
            , GOODS.GOODS_BAR_CODE.as("goodsBarCode"))
            .from(ORDER_GOODS)
            .leftJoin(GOODS)
            .on(ORDER_GOODS.GOODS_ID.eq(GOODS.GOODS_ID))
            .where(ORDER_GOODS.ORDER_SN.eq(orderSn)).fetchInto(OrderStoreGoodsBo.class);
        orderStorePosBo.setGoodsInfos(orderStoreGoodsBos);
        return orderStorePosBo;
    }
}
