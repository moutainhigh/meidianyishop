package com.meidianyi.shop.service.pojo.wxapp.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 孔德成
 * @date 2019/10/14 18:19
 */
@Data
public class WxAppCartListParam  {
    @JsonProperty(value = "user_id")
    Integer userId;
}
