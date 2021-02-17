package com.meidianyi.shop.service.saas.index.vo;

import java.util.List;

/**
 * @author luguangyao
 */
public class AllOrderDataViewVo {

    private OrderStatusViewInfo orderStatusViewInfo;

    private OrderShipViewInfo orderShipViewInfo;

    private OrderPayViewInfo orderPayViewInfo;

    private DataDisplayInfo dataDisplayInfo;

    private List<DataDisplayInfo> displayInfoList;


    public List<DataDisplayInfo> getDisplayInfoList() {
        return displayInfoList;
    }

    public void setDisplayInfoList(List<DataDisplayInfo> displayInfoList) {
        this.displayInfoList = displayInfoList;
    }

    public DataDisplayInfo getDataDisplayInfo() {
        return dataDisplayInfo;
    }

    public void setDataDisplayInfo(DataDisplayInfo dataDisplayInfo) {
        this.dataDisplayInfo = dataDisplayInfo;
    }

    public OrderStatusViewInfo getOrderStatusViewInfo() {
        return orderStatusViewInfo;
    }

    public void setOrderStatusViewInfo(OrderStatusViewInfo orderStatusViewInfo) {
        this.orderStatusViewInfo = orderStatusViewInfo;
    }

    public OrderShipViewInfo getOrderShipViewInfo() {
        return orderShipViewInfo;
    }

    public void setOrderShipViewInfo(OrderShipViewInfo orderShipViewInfo) {
        this.orderShipViewInfo = orderShipViewInfo;
    }

    public OrderPayViewInfo getOrderPayViewInfo() {
        return orderPayViewInfo;
    }

    public void setOrderPayViewInfo(OrderPayViewInfo orderPayViewInfo) {
        this.orderPayViewInfo = orderPayViewInfo;
    }
}
