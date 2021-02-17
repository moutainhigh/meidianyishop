package com.meidianyi.shop.service.pojo.shop.order.goods.param;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/7/29
 **/
@Data
public class OrderGoodsParam {
    @NotBlank(message = JsonResultMessage.MSG_ORDER_ORDER_ID_NOT_NULL)
    private Integer orderId;
}
