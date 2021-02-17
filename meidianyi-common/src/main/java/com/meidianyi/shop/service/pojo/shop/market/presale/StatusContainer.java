package com.meidianyi.shop.service.pojo.shop.market.presale;

import java.sql.Timestamp;

/**
 * @author 郑保乐
 */
public interface StatusContainer {

    /**
     * 获取活动开始时间
     *
     * @return 活动开始时间
     */
    Timestamp getStartTime();

    /**
     * 获取活动结束时间
     *
     * @return 活动结束时间
     */
    Timestamp getEndTime();

    /**
     * 获取一段定金开始时间
     *
     * @return 一段定金开始时间
     */
    Timestamp getPreStartTime();

    /**
     * 获取一段定金结束时间
     *
     * @return 一段定金结束时间
     */
    Timestamp getPreEndTime();

    /**
     * 获取二段定金开始时间
     *
     * @return 二段定金开始时间
     */
    Timestamp getPreStartTime2();

    /**
     * 获取二段定金结束时间
     *
     * @return 二段定金结束时间
     */
    Timestamp getPreEndTime2();

    /**
     * getPrePayStep
     *
     * @return
     */
    Byte getPrePayStep();

    /**
     * 获取状态
     *
     * @return 状态
     */
    Byte getStatus();
}
