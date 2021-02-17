package com.meidianyi.shop.service.saas.index.vo;

/**
 * @author luguangyao
 */
public class OrderPayViewInfo {
    /**
     * 总退款订单数
     */
    private Integer totalRefundNum;
    /**
     * 已退款订单总数
     */
    private Integer refundedNum;
    /**
     * 待退款订单总数
     */
    private Integer notRefundedNum;
    /**
     * 7天内未退款订单数量
     */
    private Integer sevenNotRefundedNum;
    /**
     * 7天内已退款订单数量
     */
    private Integer sevenRefundedNum;
    /**
     * 拒绝退款订单总数
     */
    private Integer refuseRefundNum;
    /**
     * 7天内拒绝退款订单数量
     */
    private Integer sevenRefuseRefundNum;

    public Integer getTotalRefundNum() {
        return totalRefundNum;
    }

    public void setTotalRefundNum(Integer totalRefundNum) {
        this.totalRefundNum = totalRefundNum;
    }

    public Integer getRefundedNum() {
        return refundedNum;
    }

    public void setRefundedNum(Integer refundedNum) {
        this.refundedNum = refundedNum;
    }

    public Integer getNotRefundedNum() {
        return notRefundedNum;
    }

    public void setNotRefundedNum(Integer notRefundedNum) {
        this.notRefundedNum = notRefundedNum;
    }

    public Integer getSevenNotRefundedNum() {
        return sevenNotRefundedNum;
    }

    public void setSevenNotRefundedNum(Integer sevenNotRefundedNum) {
        this.sevenNotRefundedNum = sevenNotRefundedNum;
    }

    public Integer getSevenRefundedNum() {
        return sevenRefundedNum;
    }

    public void setSevenRefundedNum(Integer sevenRefundedNum) {
        this.sevenRefundedNum = sevenRefundedNum;
    }

    public Integer getRefuseRefundNum() {
        return refuseRefundNum;
    }

    public void setRefuseRefundNum(Integer refuseRefundNum) {
        this.refuseRefundNum = refuseRefundNum;
    }

    public Integer getSevenRefuseRefundNum() {
        return sevenRefuseRefundNum;
    }

    public void setSevenRefuseRefundNum(Integer sevenRefuseRefundNum) {
        this.sevenRefuseRefundNum = sevenRefuseRefundNum;
    }
}
