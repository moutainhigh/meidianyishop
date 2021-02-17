package com.meidianyi.shop.service.pojo.wxapp.store.showmain;

import com.meidianyi.shop.service.pojo.shop.member.address.UserAddressVo;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import lombok.Data;

import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/9/17
 **/
@Data
public class StoreOrderListVo {
    private Integer orderId;
    private String orderSn;
    private String  mobile;
    private Byte  orderStatus;
    private String  completeAddress;
    private Integer  addressId;
    private UserAddressVo userAddress;
    /**
     * 订单商品
     */
    private List<OrderGoodsVo> orderGoodsList;

}
