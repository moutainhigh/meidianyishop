package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail;

import com.meidianyi.shop.service.pojo.shop.member.address.UserAddressVo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 地址信息 和运费
 * @author 孔德成
 * @date 2020/4/28
 */
@Getter
@Setter
public class DeliverFeeAddressDetailVo {

    /**
     *  1可用 2不可用
     */
    private Byte status =(byte)1;
    /**商品购买时需要的运费，详情展示时的默认运费*/
    private BigDecimal deliverPrice;
    /**
     * 信息
     */
    private String message;

    private UserAddressVo address;

}
