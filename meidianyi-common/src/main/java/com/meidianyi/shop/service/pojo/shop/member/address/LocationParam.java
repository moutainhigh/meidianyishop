package com.meidianyi.shop.service.pojo.shop.member.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/5/28
 */
@Getter
@Setter
@ToString
public class LocationParam {
    /**
     * 经度
     */
    @NotNull
    private String lat;
    /**
     * 纬度
     */
    @NotNull
    private String lng;
}
