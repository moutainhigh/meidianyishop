package com.meidianyi.shop.service.pojo.shop.member.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 地址坐标
 * @author 孔德成
 * @date 2019/11/18 11:38
 */
@Getter
@Setter
public class AddressLocation {
    public static final Integer STATUS_OK = 0;
    /**
     * 状态码，0为正常,
     * 310请求参数信息有误，
     * 311Key格式错误,
     * 306请求有护持信息请检查字符串,
     * 110请求来源未被授权
     */
    private Integer status;
    private String message;
    private Result result;
    @Getter
    @Setter
    public class Result{
        @JsonProperty("address_components")
        private AddressComponent addressComponent;
        private Location location;

        @Setter
        @Getter
        public class Location{
            /**
             * 经度
             */
            private String lat;
            /**
             * 纬度
             */
            private String lng;
        }
        @Getter
        @Setter
        public class AddressComponent {
            private String nation;
            private String province;
            private String city;
            private String district;
            private String street;
            @JsonProperty("street_number")
            private String streetNumber;
        }
    }



}
