package com.meidianyi.shop.service.pojo.shop.market.firstspecial;

import java.math.BigDecimal;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.validated.FirstSpecialUpdateValidatedGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2019-08-16 18:01
 **/
@Data
public class FirstSpecialGoodsProductParam {

    /** b2c_first_special_product主键 */
    private Integer id;

    /** 规格ID */
    private Integer prdId;

    /** 修改后的规格价 */
    @NotNull(groups = {FirstSpecialUpdateValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private BigDecimal prdPrice;
}
