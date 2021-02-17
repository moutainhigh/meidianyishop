package com.meidianyi.shop.service.pojo.shop.overview.hotwords;

import lombok.Data;

/**
 * 用户id和热词数量
 * @author liangchen
 * @date 2019.12.19
 */
@Data
public class UserIdAndNum {
    /** 用户id */
    private Integer userId;
    /** 搜素热词数量 默认5条 */
    private Integer num = 5;
}
