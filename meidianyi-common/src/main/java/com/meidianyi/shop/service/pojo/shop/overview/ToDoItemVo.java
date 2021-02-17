package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @Author:liufei
 * @Date:2019/7/16
 * @Description:
 */
@Data
@Component
public class ToDoItemVo {
    /** 待提货订单 */
    private Integer pendingOrder;
    /** 待发货 */
    private Integer toBeDelivered;
    /**  退货退款 */
    private Integer refunds;
    /** 已售罄商品 */
    private Integer soldOutGoods;
    /** 待审核Pending Review用简写Pr表示 */
    /** 商品评价待审核 */
    private Integer productEvaluationPr;
    /** 分销员待审核 */
    private Integer distributorPr;
    /** 会员卡待审核 */
    private Integer membershipCardPr;
    /** 分销提现待审核 */
    private Integer distributionWithdrawalPr;
    /** 服务评价待审核 */
    private Integer serviceEvaluationPr;
    /** 当前选项 */
    private String backlog;
}
