package com.meidianyi.shop.dao.main.order;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import com.meidianyi.shop.db.main.tables.records.InquiryOrderRecord;
import com.meidianyi.shop.service.pojo.saas.order.MainInquiryOrderStatisticsParam;
import com.meidianyi.shop.service.pojo.saas.order.MainInquiryOrderStatisticsVo;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.statistics.InquiryOrderStatistics;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo.InquiryOrderTotalVo;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import static com.meidianyi.shop.db.main.Tables.INQUIRY_ORDER;
import static com.meidianyi.shop.db.main.Tables.SHOP;
import static org.jooq.impl.DSL.*;
import static org.jooq.impl.DSL.avg;

/**
 * @author yangpengcheng
 * @date 2020/8/14
 **/
@Repository
public class MainInquiryOrderDao extends MainBaseDao {

    /**
     * 同步新订单
     * @param list
     */
    public void inquiryOrderSynchronizeInsert(List<InquiryOrderDo> list){
        List<InquiryOrderRecord> recordList=new ArrayList<>();
        for(InquiryOrderDo inquiryOrderDo:list){
            InquiryOrderRecord record=getListByShopIdAndOrderSn(inquiryOrderDo.getShopId(),inquiryOrderDo.getOrderSn());
            if(record!=null){
                continue;
            }
            InquiryOrderRecord recordNew=db().newRecord(INQUIRY_ORDER);
            FieldsUtil.assign(inquiryOrderDo,recordNew);
            recordList.add(recordNew);
        }
        db().batchInsert(recordList).execute();
    }

    /**
     * 同步更新订单
     * @param list
     */
    public void inquiryOrderSynchronizeUpdate(List<InquiryOrderDo> list,Integer shopId){
        List<InquiryOrderRecord> recordList=new ArrayList<>();
        for(InquiryOrderDo inquiryOrderDo:list){
            InquiryOrderRecord record=getListByShopIdAndOrderSn(shopId,inquiryOrderDo.getOrderSn());
            FieldsUtil.assign(inquiryOrderDo,record);
            recordList.add(record);
        }
        db().batchUpdate(recordList).execute();
    }

    /**
     * 根据shopId,orderSn查询
     * @param shopId
     * @param orderSn
     * @return
     */
    public InquiryOrderRecord getListByShopIdAndOrderSn(Integer shopId,String orderSn){
        return db().select().from(INQUIRY_ORDER).where(INQUIRY_ORDER.SHOP_ID.eq(shopId)
             .and(INQUIRY_ORDER.ORDER_SN.eq(orderSn))).fetchAnyInto(InquiryOrderRecord.class);
    }
    /**
     *问诊订单统计报表详情分页查询
     * @param param
     * @return
     */
    public PageResult<MainInquiryOrderStatisticsVo> orderStatisticsPage(MainInquiryOrderStatisticsParam param){
        SelectJoinStep<? extends Record> select=buildSelectOptions(param);
        select=buildWhereOptions(select,param);
        select.groupBy(INQUIRY_ORDER.SHOP_ID,SHOP.SHOP_NAME,INQUIRY_ORDER.DOCTOR_ID,INQUIRY_ORDER.DOCTOR_NAME,date(INQUIRY_ORDER.CREATE_TIME))
        .orderBy(INQUIRY_ORDER.CREATE_TIME.desc());
        PageResult<MainInquiryOrderStatisticsVo> result=this.getPageResult(select,param.getCurrentPage(),param.getPageRows(),MainInquiryOrderStatisticsVo.class);
        return result;
    }

    public SelectJoinStep<? extends Record> buildSelectOptions(MainInquiryOrderStatisticsParam param){
        SelectJoinStep<? extends Record> select=db().select(
            date((INQUIRY_ORDER.CREATE_TIME)).as(InquiryOrderStatistics.CREAT_TIME),
            //咨询单数
            count((INQUIRY_ORDER.ORDER_ID)).as(InquiryOrderStatistics.AMOUNT),
            //咨询总金额
            sum(INQUIRY_ORDER.ORDER_AMOUNT).as(InquiryOrderStatistics.AMOUNT_PRICE),
            //咨询单次价格
            avg(INQUIRY_ORDER.ORDER_AMOUNT).as(InquiryOrderStatistics.ONE_PRICE),
            //医师id
            INQUIRY_ORDER.DOCTOR_ID.as(InquiryOrderStatistics.DOCTOR_ID),
            //医师名称
            INQUIRY_ORDER.DOCTOR_NAME.as(InquiryOrderStatistics.DOCTOR_NAME),
            //shopId
            INQUIRY_ORDER.SHOP_ID.as(InquiryOrderStatistics.SHOP_ID),
            //店铺名称
            SHOP.SHOP_NAME.as(InquiryOrderStatistics.SHOP_NAME)
        ).from(INQUIRY_ORDER).leftJoin(SHOP).on(INQUIRY_ORDER.SHOP_ID.eq(SHOP.SHOP_ID));
        return select;
    }
    public SelectJoinStep<? extends Record> buildWhereOptions(SelectJoinStep<? extends Record> selectJoinStep, MainInquiryOrderStatisticsParam param){
        selectJoinStep.where(INQUIRY_ORDER.IS_DELETE.eq(DelFlag.NORMAL_VALUE));
        selectJoinStep.where(INQUIRY_ORDER.ORDER_STATUS.gt(InquiryOrderConstant.ORDER_TO_PAID));
        selectJoinStep.where(INQUIRY_ORDER.ORDER_STATUS.ne(InquiryOrderConstant.ORDER_CANCELED));
        if(param.getShopId()!=null){
            selectJoinStep.where(INQUIRY_ORDER.SHOP_ID.eq(param.getShopId()));
        }
        if(StringUtils.isNotBlank(param.getDoctorName())){
            selectJoinStep.where(INQUIRY_ORDER.DOCTOR_NAME.like(likeValue(param.getDoctorName())));
        }
        if(param.getStartTime()!=null){
            selectJoinStep.where(INQUIRY_ORDER.CREATE_TIME.ge(param.getStartTime()));
        }
        if(param.getEndTime()!=null){
            selectJoinStep.where(INQUIRY_ORDER.CREATE_TIME.le(param.getEndTime()));
        }

        return selectJoinStep;
    }

    /**
     * 问诊订单统计报表详情查询
     * @param param
     * @return
     */
    public List<MainInquiryOrderStatisticsVo> orderStatistics(MainInquiryOrderStatisticsParam param){
        SelectJoinStep<? extends Record> select=buildSelectOptions(param);
        select=buildWhereOptions(select,param);
        select.groupBy(INQUIRY_ORDER.SHOP_ID,SHOP.SHOP_NAME,INQUIRY_ORDER.DOCTOR_ID,INQUIRY_ORDER.DOCTOR_NAME,date(INQUIRY_ORDER.CREATE_TIME))
            .orderBy(INQUIRY_ORDER.CREATE_TIME.desc());
        List<MainInquiryOrderStatisticsVo> list=select.fetchInto(MainInquiryOrderStatisticsVo.class);
        return list;
    }
    /**
     * 咨询报表总数total查询
     * @param param
     * @return
     */
    public InquiryOrderTotalVo orderStatisticsTotal(MainInquiryOrderStatisticsParam param){
        SelectJoinStep<? extends Record> select=db().select(
            //咨询单数
            count((INQUIRY_ORDER.ORDER_ID)).as(InquiryOrderStatistics.AMOUNT_TOTAL),
            //咨询总金额
            sum(INQUIRY_ORDER.ORDER_AMOUNT).as(InquiryOrderStatistics.AMOUNT_PRICE_TOTAL),
            //咨询单次价格
            avg(INQUIRY_ORDER.ORDER_AMOUNT).as(InquiryOrderStatistics.ONE_PRICE_TOTAL)
        ).from(INQUIRY_ORDER);
        select=buildWhereOptions(select,param);
        InquiryOrderTotalVo inquiryOrderTotalVo=select.fetchOneInto(InquiryOrderTotalVo.class);
        return inquiryOrderTotalVo;
    }
}
