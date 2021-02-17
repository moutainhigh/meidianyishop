package com.meidianyi.shop.service.pojo.shop.overview.hotwords;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 日期和热词
 * @author liangchen
 * @date 2019.12.20
 */
@Data
public class DateAndWordsFlag {
    /** 开始日期 */
    private Timestamp startDate;
    /** 结束日期 */
    private Timestamp endDate;
    /** 是否是热词 */
    private Byte isHotWords;
}
