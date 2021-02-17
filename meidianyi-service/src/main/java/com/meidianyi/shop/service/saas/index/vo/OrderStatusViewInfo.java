package com.meidianyi.shop.service.saas.index.vo;

/**
 * @author luguangyao
 */
public class OrderStatusViewInfo {

    /**
     * 订单总数
     */
    private Integer totalNum;

    /**
     * 已完成订单数量
     */
    private Integer finishedNum;

    /**
     *7天内已完成订单数量
     */
    private Integer sevenFinishedNum;

    /**
     * 7天内已取消订单数量
     */
    private Integer sevenCancelledNum;
    /**
     * 7天内已关闭订单数量
     */
    private Integer sevenClosedNum;
    /**
     * 7天内退款订单数量
     */
    private Integer sevenRefundNum;
    /**
     * 7天内未支付订单数量
     */
    private Integer sevenNoPayNum;

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getFinishedNum() {
        return finishedNum;
    }

    public void setFinishedNum(Integer finishedNum) {
        this.finishedNum = finishedNum;
    }

    public Integer getSevenFinishedNum() {
        return sevenFinishedNum;
    }

    public void setSevenFinishedNum(Integer sevenFinishedNum) {
        this.sevenFinishedNum = sevenFinishedNum;
    }

    public Integer getSevenCancelledNum() {
        return sevenCancelledNum;
    }

    public void setSevenCancelledNum(Integer sevenCancelledNum) {
        this.sevenCancelledNum = sevenCancelledNum;
    }

    public Integer getSevenClosedNum() {
        return sevenClosedNum;
    }

    public void setSevenClosedNum(Integer sevenClosedNum) {
        this.sevenClosedNum = sevenClosedNum;
    }

    public Integer getSevenRefundNum() {
        return sevenRefundNum;
    }

    public void setSevenRefundNum(Integer sevenRefundNum) {
        this.sevenRefundNum = sevenRefundNum;
    }

    public Integer getSevenNoPayNum() {
        return sevenNoPayNum;
    }

    public void setSevenNoPayNum(Integer sevenNoPayNum) {
        this.sevenNoPayNum = sevenNoPayNum;
    }
}
