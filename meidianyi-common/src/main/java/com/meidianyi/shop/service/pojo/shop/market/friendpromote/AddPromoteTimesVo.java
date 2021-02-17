package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

/**
 * 增加助力次数出参
 * @author liangchen
 * @date 2020.03.09
 */
@Data
public class AddPromoteTimesVo {
    /** 是否成功 0 失败 1 成功 */
    private Integer flag;
    /** 返回提示信息标识  0:分享获取助力次数已用完 */
    private Integer msgCode;
}
