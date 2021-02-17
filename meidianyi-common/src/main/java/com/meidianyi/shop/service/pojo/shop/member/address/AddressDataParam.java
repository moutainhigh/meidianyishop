package com.meidianyi.shop.service.pojo.shop.member.address;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/6/2 11:28
 */
@Getter
@Setter
@ToString
public class AddressDataParam {
    @NotNull
    private Integer index;
}
