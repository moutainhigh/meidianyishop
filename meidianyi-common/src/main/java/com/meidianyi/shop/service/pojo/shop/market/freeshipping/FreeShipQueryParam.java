package com.meidianyi.shop.service.pojo.shop.market.freeshipping;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author 孔德成
 * @date 2019/7/31 10:23
 */
@Getter
@Setter
public class FreeShipQueryParam  extends BasePageParam {


    /**
     * 活动状态
     */
    @NotNull
    @Range(min = 0,max =4 )
    private Byte navType;
}
