package com.meidianyi.shop.service.pojo.shop.config.trade.third;

import com.meidianyi.shop.service.pojo.shop.config.trade.ReturnBusinessAddressParam;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 孔德成
 * @date 2020/5/18
 */
@Getter
@Setter
@ToString
public class ThirdInfoVo {

    private AppBo appBo;
    private AppAuthBo appAuthBo;

    private Byte cityOrderPush;
    private Byte verifyOrder;
}
