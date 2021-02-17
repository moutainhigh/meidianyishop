package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 好友助力帮忙出参
 * @author liangchen
 * @date 2020.03.06
 */
@Data
public class PromoteVo {
    /** 助力值 */
    private Integer promoteValue;
    /** 能否继续助力 */
    private Byte canPromote;
    /** 能否分享 */
    private Byte canShare;
    /** 不能助力返回信息 */
    private Byte cantPromote;
}
