package com.meidianyi.shop.service.pojo.shop.summary.visit;

import com.meidianyi.shop.service.pojo.shop.summary.RefDateRecord;
import lombok.Data;

/**
 * 带日期数据容器
 *
 * @author 郑保乐
 */
@Data
public class RefDateRecordHolder<T extends Number> implements RefDateRecord<T> {

    private String refDate;
    private T value;
}
