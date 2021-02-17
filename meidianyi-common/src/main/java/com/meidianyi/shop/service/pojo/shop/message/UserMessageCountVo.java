package com.meidianyi.shop.service.pojo.shop.message;

import lombok.Data;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-11 16:11
 **/

@Data
public class UserMessageCountVo {

    private Integer announcementCount;
    private Integer orderCount;
    private Integer chatCount;
}
