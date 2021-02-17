package com.meidianyi.shop.service.pojo.shop.overview.hotwords;

import lombok.Data;

/**
 * 用户id和热词
 * @author liangchen
 * @date 2019.12.19
 */
@Data
public class UserIdAndWords {
    /** 用户id */
    private Integer userId;
    /** 热词 */
    private String hotWords;
}
