package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Data;

/**
 * @author: 王兵兵
 * @create: 2019-08-08 18:02
 * 会员卡选择场景  只需要ID和卡名称
 **/
@Data
public class SimpleMemberCardVo {

    private Integer id;

    private String cardName;

    /**
     * 0:普通会员卡，1:次卡,2:等级卡
     */
    private Byte cardType;
}
