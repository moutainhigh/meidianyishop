package com.meidianyi.shop.service.saas.index.vo;

/**
 * @author luguangyao
 */
public class OrderShipViewInfo {

    /**
     * 已发货订单总数量
     */
    private Integer totalShippedNum;

    /**
     * 待发货订单总数量
     */
    private Integer totalBeShippedNum;

    /**
     * 7天内待发货订单数量
     */
    private Integer sevenBeShippedNum;
    /**
     * 7天内已发货订单数量
     */
    private Integer sevenShippedNum;
    /**
     * 自提订单总数量
     */
    private Integer totalPickedUpNum;
    /**
     * 待自提订单总数量
     */
    private Integer totalPickUpNum;

    /**
     * 7天内自提订单总数量
     */
    private Integer sevenPickedUpNum;
    /**
     * 7天内待提订单总数量
     */
    private Integer sevenPickUpNum;

    public Integer getTotalShippedNum() {
        return totalShippedNum;
    }

    public void setTotalShippedNum(Integer totalShippedNum) {
        this.totalShippedNum = totalShippedNum;
    }

    public Integer getTotalBeShippedNum() {
        return totalBeShippedNum;
    }

    public void setTotalBeShippedNum(Integer totalBeShippedNum) {
        this.totalBeShippedNum = totalBeShippedNum;
    }

    public Integer getSevenBeShippedNum() {
        return sevenBeShippedNum;
    }

    public void setSevenBeShippedNum(Integer sevenBeShippedNum) {
        this.sevenBeShippedNum = sevenBeShippedNum;
    }

    public Integer getSevenShippedNum() {
        return sevenShippedNum;
    }

    public void setSevenShippedNum(Integer sevenShippedNum) {
        this.sevenShippedNum = sevenShippedNum;
    }

    public Integer getTotalPickedUpNum() {
        return totalPickedUpNum;
    }

    public void setTotalPickedUpNum(Integer totalPickedUpNum) {
        this.totalPickedUpNum = totalPickedUpNum;
    }

    public Integer getTotalPickUpNum() {
        return totalPickUpNum;
    }

    public void setTotalPickUpNum(Integer totalPickUpNum) {
        this.totalPickUpNum = totalPickUpNum;
    }

    public Integer getSevenPickedUpNum() {
        return sevenPickedUpNum;
    }

    public void setSevenPickedUpNum(Integer sevenPickedUpNum) {
        this.sevenPickedUpNum = sevenPickedUpNum;
    }

    public Integer getSevenPickUpNum() {
        return sevenPickUpNum;
    }

    public void setSevenPickUpNum(Integer sevenPickUpNum) {
        this.sevenPickUpNum = sevenPickUpNum;
    }
}
