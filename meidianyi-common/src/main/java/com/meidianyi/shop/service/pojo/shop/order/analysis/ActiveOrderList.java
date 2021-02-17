package com.meidianyi.shop.service.pojo.shop.order.analysis;

import lombok.Data;

import java.util.List;

/**
 *  获取活动中新老用户的成交数
 *
 * @author 孔德成
 * @date 2019/8/2 17:40
 */
@Data
public class ActiveOrderList {

    List<OrderActivityUserNum> oldUserNum;
    List<OrderActivityUserNum> newUserNum;
    private Integer oldUser;
    private Integer newUser;
}
