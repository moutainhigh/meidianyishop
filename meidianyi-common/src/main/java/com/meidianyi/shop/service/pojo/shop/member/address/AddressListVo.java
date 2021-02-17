package com.meidianyi.shop.service.pojo.shop.member.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 地址信息列表
 * @author 孔德成
 * @date 2020/5/27
 */
@Getter
@Setter
@ToString
public class AddressListVo {

    private List<UserAddressVo> addressList;
}
