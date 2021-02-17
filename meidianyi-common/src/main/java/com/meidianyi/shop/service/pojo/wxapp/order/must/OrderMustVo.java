package com.meidianyi.shop.service.pojo.wxapp.order.must;

import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import lombok.Getter;
import lombok.Setter;

/**
 * 下单必填信息
 * @author 王帅
 */
@Getter
@Setter
public class OrderMustVo {
    private Byte isShow;
    private Byte orderRealName;
    private Byte orderCid;
    private Byte consigneeRealName;
    private Byte consigneeCid;
    private Byte custom;
    private String customTitle;

    /**
     * 使用trade初始化
     */
    public void init(Byte orderRealName, Byte orderCid, Byte consigneeRealName, Byte consigneeCid, Byte custom, String customTitle){
        this.orderRealName = orderRealName;
        this.orderCid = orderCid;
        this.consigneeRealName = consigneeRealName;
        this.consigneeCid = consigneeCid;
        this.custom = custom;
        this.customTitle = customTitle;
    }

    /**
     * 校验
     * @return
     */
    public Byte isCheck(){
        if(isShow == null){
            if((orderRealName + orderCid + consigneeRealName + consigneeCid + custom) > 0){
                isShow = OrderConstant.YES;
            }else{
                isShow = OrderConstant.NO;
            }
        }
        return isShow;
    }

    /**
     * 展示
     * @return
     */
    public OrderMustVo show(){
        isShow = OrderConstant.YES;
        return this;
    }

    public OrderMustVo hide(){
        isShow = OrderConstant.NO;
        return this;
    }
}
