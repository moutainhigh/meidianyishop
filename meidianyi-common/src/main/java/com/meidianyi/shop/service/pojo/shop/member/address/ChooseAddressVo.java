package com.meidianyi.shop.service.pojo.shop.member.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author 孔德成
 * @date 2020/4/29
 */
@Getter
@Setter
@ToString
public class ChooseAddressVo  extends UserAddressVo{
    /**
     * 可用 2不可用
     */
    private Byte status;
    private String message;

}