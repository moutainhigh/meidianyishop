package com.meidianyi.shop.service.pojo.shop.member.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.experimental.theories.suppliers.TestedOn;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/5/27
 */
@Getter
@Setter
@ToString
public class AddressIdParam {

    @NotNull
    private Integer addressId;
}
