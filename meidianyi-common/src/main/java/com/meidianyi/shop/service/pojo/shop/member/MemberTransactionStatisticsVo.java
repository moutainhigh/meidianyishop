package com.meidianyi.shop.service.pojo.shop.member;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 黄壮壮
 * @Date: 2019年8月20日
 * @Description: 会员交易统计信息-出参
 */
@Setter
@Getter
public class MemberTransactionStatisticsVo {

    /**  全部交易统计 */
    private GeneralTransactionStatisticsVo allTransactionStatistics = new GeneralTransactionStatisticsVo();
    /**  实物交易统计 */
    private GeneralTransactionStatisticsVo physicalTransactionStatistics = new GeneralTransactionStatisticsVo();
    /**  非实物交易统计 --暂时没有非实物商品订单 */
    /**  private GeneralTransactionStatisticsVo nonPhysicalTransactionStatistics = new GeneralTransactionStatisticsVo();
    /**  会员增值交易统计 */
    private AppreciationTransactionStatisticsVo appreciationTransactionStatistics = new AppreciationTransactionStatisticsVo();
    /**  门店服务预约交易统计 */
    private GeneralTransactionStatisticsVo storeServiceOrderTransactionStatistics = new GeneralTransactionStatisticsVo();
    /**  门店买单订单交易统计 */
    private StoreOrderTransactionStatisticsVo storeOrderTransactionStatistics = new StoreOrderTransactionStatisticsVo();

    /**  分销统计 */
    private DistributionStatisticsVo distributionStatistics = new DistributionStatisticsVo();


    /**
     * 实物、非实物、门店预约 交易统计类
     */
    @Setter
    @Getter
    public static class GeneralTransactionStatisticsVo {
        /**
         * 最近下单时间
         */
        private Timestamp lastOrderTime;
        /**
         * 客单价
         */
        private BigDecimal unitPrice;
        /**
         * 累计消费金额
         */
        private BigDecimal totalMoneyPaid;
        /**
         * 累计消费订单数
         */
        private Integer orderNum;
        /**
         * 累计退款金额
         */
        private BigDecimal returnOrderMoney;
        /**
         * 累计退款订单数
         */
        private Integer returnOrderNum;
    }

    /**
     * 增值交易统计
     */
    @Getter
    @Setter
    public static class AppreciationTransactionStatisticsVo {
        /**
         * 会员卡购买订单数
         */
        private Integer memberCardPurchaseOrderNum;
        /**
         * 会员卡续费单数
         */
        private Integer memberCardRenewOrderNum;
        /**
         * 会员卡充值单数
         */
        private Integer memberCardChargeOrderNum;
        /**
         * 优惠券礼包购买订单数
         */
        private Integer couponPackPurchaseOrderNum;
    }

    /**
     * 门店买单交易统计类
     */
    @Getter
    @Setter
    public static class StoreOrderTransactionStatisticsVo {
        /**
         * 最近下单时间
         */
        private Timestamp lastOrderTime;
        /**
         * 门店买单订单数
         */
        private Integer orderNum;
        /**
         * 累计消费金额
         */
        private BigDecimal totalMoneyPaid;
        /**
         * 客单价
         */
        private BigDecimal unitPrice;

    }

    /**
     * 分销统计
     */
    @Getter
    @Setter
    public static class DistributionStatisticsVo {
        /**
         * 获返利订单数量
         */
        private Integer rebateOrderNum;
        /**
         * 返利商品总金额(元)
         */
        private BigDecimal totalCanFanliMoney;
        /**
         * 获返利佣金总额(元)
         */
        private BigDecimal rebateMoney;
        /**
         * 已提现佣金总额(元)
         */
        private BigDecimal withdrawCash;
        /**
         * 下级用户数
         */
        private String sublayerNumber;
        /**
         * 分销员等级
         */
        private String levelName;
        /**
         * 分销员分组
         */
        private String groupName;
    }


}
