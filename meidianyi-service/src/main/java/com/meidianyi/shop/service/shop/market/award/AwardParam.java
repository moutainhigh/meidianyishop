package com.meidianyi.shop.service.shop.market.award;

import com.meidianyi.shop.service.pojo.shop.market.sharereward.ShareRule;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liufei
 * @date 1/13/20
 */
@Data
@Builder
public class AwardParam {
    public Integer activityId;
    public Integer userId;
    public ShareRule rule;
    @Builder.Default
    public String orderSn = StringUtils.EMPTY;
    /**
     * 积分来源
     */
    public Integer changeWay;
}
