package com.meidianyi.shop.service.pojo.shop.market.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 *
 * @author 卢光耀
 * @date 2019-08-16 09:40
 *
*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendUserVo {
    private String userKey;

    private Integer userNumber;
}
