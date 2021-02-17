package com.meidianyi.shop.service.pojo.wxapp.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.order.must.OrderMustParam;
import com.meidianyi.shop.service.pojo.wxapp.order.validated.CreateOrderValidatedGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 下单参数
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class CreateParam extends OrderBeforeParam{
    /**发票id*/
    private Integer invoiceId;
    /**买家留言*/
    private String message;
    /**TODO 实付金额*/
    @NotNull(groups = {CreateOrderValidatedGroup.class}, message = JsonResultMessage.MSG_ORDER_ADDRESS_NO_NULL)
    private BigDecimal orderAmount;
    /**自提日期*/
    private String pickupDate;
    /**自提时间*/
    private String pickupTime;
    /**trueName 欧派真实姓名*/
    private String trueName;
    /**idCard 欧派身份证号码*/
    private String idCard;
    /**pos扫码购门店ID*/
    private Integer scanStoreId;
    /**必填信息*/
    private OrderMustParam must;
    /**用户ip*/
    @JsonIgnore
    private String clientIp;




    public void intoRecord(OrderInfoRecord orderRecord){
        //user
        orderRecord.setUserId(getWxUserInfo().getUserId());
        orderRecord.setUserOpenid(getWxUserInfo().getWxUser().getOpenId());
        if(getMessage() != null) {
            orderRecord.setAddMessage(getMessage());
        }
        orderRecord.setDeliverType(getDeliverType());
        //TODO orderRecord.setPickupdateTime(getPickupDate() + " " + getPickupTime());
        if(getTrueName() != null) {
            orderRecord.setTrueName(getTrueName());
        }
        if(getIdCard() != null) {
            orderRecord.setIdCard(getIdCard());
        }
        //扫码购
        if(getScanStoreId() != null && getScanStoreId() > 0){
            orderRecord.setPosFlag(OrderConstant.YES);
        }
		if (null != this.getRoomId()) {
			orderRecord.setRoomId(this.getRoomId());
		}

    }
}
