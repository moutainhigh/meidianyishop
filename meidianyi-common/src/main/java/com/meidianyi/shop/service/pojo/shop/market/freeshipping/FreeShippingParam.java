package com.meidianyi.shop.service.pojo.shop.market.freeshipping;

import com.meidianyi.shop.common.foundation.validator.ListValid;
import com.meidianyi.shop.service.pojo.shop.config.pledge.group.UpdateGroup;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/7/29 17:39
 */
@Getter
@Setter
public class FreeShippingParam {
    @NotNull(groups = UpdateGroup.class)
    private Integer   id;
    @NotNull
    private String    name;
    private Timestamp startTime;
    private Timestamp endTime;
    /**
     *  有效期 0 固定期限 1永久有效
     */
    @Range(min = 0,max = 1)
    private Byte      expireType;
    /**
     * 条件 0全部 1部分
     */
    @Range(min = 0,max = 1)
    private Integer   type;
    /**
     * 优先级 最大100
     */
    @Range(min = 0,max = 100)
    private Byte      level;
    private String    recommendGoodsId;
    private String    recommendCatId;
    private String    recommendSortId;

    @Valid
    @ListValid
    private List<FreeShippingRuleParam> ruleList;


}
