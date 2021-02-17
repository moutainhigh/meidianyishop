package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author liufei
 * @date 2/21/2020
 */
@Data
@Builder
public class Metadata {
    /**
     * 显示类型：0开店必备，1任务，2提醒，3推荐
     */
    byte type;
    /**
     * 完成状态：0未完成，1已完成
     */
    byte status;
    /**
     * 任务值
     */
    int value;
    /**
     * 任务详细内容
     */
    Map<?, ?> content;
}
