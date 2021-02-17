package com.meidianyi.shop.service.pojo.shop.market.firstspecial;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.validated.FirstSpecialAddValidatedGroup;
import com.meidianyi.shop.service.pojo.shop.market.firstspecial.validated.FirstSpecialUpdateValidatedGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-08-16 18:01
 **/
@Data
public class FirstSpecialGoodsParam {

    /** b2c_first_special_goods主键 */
    private Integer id;

    /** 商品主键 */
    @NotNull(groups = {FirstSpecialAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private Integer goodsId;

    /** 打几折 */
    private BigDecimal discount;

    /** 减多少钱 */
    private BigDecimal reducePrice;

    /** 折后价格 */
    @NotNull(groups = {FirstSpecialAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private BigDecimal goodsPrice;

    /**
     * 改价的规格数组
     */
    @NotNull(groups = {FirstSpecialUpdateValidatedGroup.class}, message = JsonResultMessage.MSG_PARAM_ERROR)
    private List<FirstSpecialGoodsProductParam> goodsProductParams;
}
