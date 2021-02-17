package com.meidianyi.shop.service.pojo.shop.member.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 地址信息
 * @author 孔德成
 * @date 2019/11/18 11:04
 */
@Getter
@Setter
public class AddressInfo {
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
        @JsonProperty("address_component")
        private AddressComponent addressComponent;
        @JsonProperty("formatted_addresses")
        private FormattedAddresses formattedAddresses;
        @JsonProperty("ad_info")
        private AdCodeInfo adInfo;

        @Getter
        @Setter
        public class  AdCodeInfo{
            private String adcode;
        }
        /**
         * 人性化地址
         */
        @Getter
        @Setter
        public class FormattedAddresses{
            private String recommend;
            private String rough;
        }
        @Getter
        @Setter
        public class AddressComponent extends Result {
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
