package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

/**
 * 是否可以助力
 * @author liangchen
 * @date 2020.02.27
 */
@Data
public class CanPromote {
    /** 是否可 0否 1是 */
    private Byte code;
    /** 提示信息 0：该助力申请未发起 1：助力已完成，不再需要助力 2：今天的助力次数已经用完了 3：助力次数已用完 */
    private Byte msg;
}
