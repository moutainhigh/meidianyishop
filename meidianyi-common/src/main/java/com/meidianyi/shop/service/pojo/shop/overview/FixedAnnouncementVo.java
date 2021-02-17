package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author liufei
 * date 2019/7/16
 */
@Data
public class FixedAnnouncementVo {
    private Integer articleId;
    private String title;
    private String formatTime;
    private Timestamp createTime;
}
