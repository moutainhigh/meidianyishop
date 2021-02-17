package com.meidianyi.shop.dao.shop.distribution;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.service.pojo.shop.distribution.RebateGoodsDetailExportVo;
import com.meidianyi.shop.service.pojo.shop.distribution.RebateGoodsDetailParam;
import com.meidianyi.shop.service.pojo.shop.distribution.RebateGoodsDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.db.shop.Tables.ORDER_GOODS;

/**
 * @author panjing
 * @date 2020/7/15 10:26
 */
@Repository
public class OrderGoodsRebateDao extends ShopBaseDao {

    /**
     * 查询别名
     */
    private static final String INVITE = "n";

    /**
     * 商品返利导出详情
     * @param param
     * @return
     */
    public List<RebateGoodsDetailExportVo> listRebateGoodsExportDetails(RebateGoodsDetailParam param) {
        SelectJoinStep<? extends Record> select = buildDetail(param);

        if (param.getStartNum() != null && param.getEndNum() != null) {
            select.limit(param.getStartNum(), param.getEndNum());
        }

        return select.fetchInto(RebateGoodsDetailExportVo.class);
    }

    /**
     * 商品返利详情分页查询
     * @param param
     * @return
     */
    public PageResult<RebateGoodsDetailVo> listRebateGoodsDetails(RebateGoodsDetailParam param) {
        return this.getPageResult(buildDetail(param), param.getCurrentPage(), param.getPageRows(), RebateGoodsDetailVo.class);
    }

    /**
     * 商品返利查询构造
     * @param param
     * @return
     */
    private SelectJoinStep<? extends Record> buildDetail(RebateGoodsDetailParam param) {
        int hundred=100;
        SelectJoinStep<? extends Record> select =
            db().select(ORDER_GOODS.GOODS_IMG, ORDER_GOODS.GOODS_NAME, ORDER_GOODS.GOODS_NUMBER, ORDER_GOODS.CAN_CALCULATE_MONEY,
                ORDER_GOODS_REBATE.ORDER_SN, USER.USERNAME, USER.MOBILE, USER.USER_ID, ORDER_GOODS_REBATE.REBATE_LEVEL, USER.as(INVITE).USER_ID.as("distributorId"),
                USER.as(INVITE).USERNAME.as("distributorName"), USER_DETAIL.REAL_NAME.as("distributorRealName"), USER.as(INVITE).MOBILE.as("distributorMobile"),
                (ORDER_GOODS_REBATE.REBATE_PERCENT).multiply(hundred).as("rebatePercent"), ORDER_GOODS_REBATE.REAL_REBATE_MONEY, ORDER_INFO.SETTLEMENT_FLAG, ORDER_INFO.FINISHED_TIME)
                .from(ORDER_GOODS_REBATE
                    .leftJoin(ORDER_INFO).on(ORDER_GOODS_REBATE.ORDER_SN.eq(ORDER_INFO.ORDER_SN))
                    .leftJoin(USER.as(INVITE)).on(ORDER_GOODS_REBATE.REBATE_USER_ID.eq(USER.as(INVITE).USER_ID))
                    .leftJoin(USER_DETAIL).on(ORDER_GOODS_REBATE.REBATE_USER_ID.eq(USER_DETAIL.USER_ID))
                    .leftJoin(USER).on(ORDER_INFO.USER_ID.eq(USER.USER_ID))
                    .leftJoin(ORDER_GOODS).on(ORDER_GOODS_REBATE.ORDER_SN.eq(ORDER_GOODS.ORDER_SN))
                        .and(ORDER_GOODS_REBATE.GOODS_ID.eq(ORDER_GOODS.GOODS_ID)));
        buildOptionDetail(select, param);

        return select;
    }

    /**
     * 商品返利明细条件查询
     * @param select
     * @param param
     */
    private void buildOptionDetail(SelectJoinStep<? extends Record> select, RebateGoodsDetailParam param) {
        select.where(ORDER_INFO.FANLI_TYPE.eq((byte) 1).and(ORDER_GOODS_REBATE.GOODS_ID.eq(param.getGoodsId())));
        // 被邀请人手机号
        if (StringUtils.isNotBlank(param.getMobile()) ) {
            select.where(USER.MOBILE.contains(param.getMobile()));
        }
        // 被邀请人用户名
        if(StringUtils.isNotBlank(param.getUsername())) {
            select.where(USER.USERNAME.contains(param.getUsername()));
        }
        // 分销员真实姓名
        if (StringUtils.isNotBlank(param.getDistributorRealName()) ) {
            select.where(USER_DETAIL.REAL_NAME.contains(param.getDistributorRealName()));
        }
        // 分销员返利关系
        if (param.getRebateLevel() != null) {
            select.where(ORDER_GOODS_REBATE.REBATE_LEVEL.eq(param.getRebateLevel()));
        }
        // 分销员手机号
        if (StringUtils.isNotBlank(param.getDistributorMobile())) {
            select.where(USER.as(INVITE).MOBILE.contains(param.getDistributorMobile()));
        }
        // 分销员用户名
        if (StringUtils.isNotBlank(param.getDistributorName())) {
            select.where(USER.as(INVITE).USERNAME.contains(param.getDistributorName()));
        }
        // 返利开始时间
        if (param.getStartRebateTime() != null && param.getEndRebateTime() != null) {
            select.where(ORDER_INFO.FINISHED_TIME.ge(param.getStartRebateTime()));
        }
        // 返利结束时间
        if (param.getEndRebateTime() != null) {
            select.where(ORDER_INFO.FINISHED_TIME.le(param.getEndRebateTime()));
        }
        // 返利订单号
        if (param.getRebateOrderSn() != null) {
            select.where(ORDER_GOODS_REBATE.ORDER_SN.contains(param.getRebateOrderSn()));
        }
        // 返利状态
        if (param.getRebateStatus() != null) {
            select.where(ORDER_INFO.SETTLEMENT_FLAG.eq(param.getRebateStatus()));
        }
        // 根据返利结束时间排序
        select.orderBy(ORDER_INFO.FINISHED_TIME.desc());
    }
}
