package com.meidianyi.shop.service.pojo.shop.member.account;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * 获取等级卡权益
 * @author 孔德成
 * @date 2020/5/14
 */
@Getter
@Setter
@ToString
public class CardInterestsParam {
    @NonNull
    private String grade;
    private Integer userId;
}
