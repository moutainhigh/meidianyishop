package com.meidianyi.shop.service.pojo.shop.overview.searchanalysis;

import lombok.Data;

/**
 * 搜索历史统计数据
 * @author liangchen
 * @date 2019.12.16
 */
@Data
public class SearchHotWordsData {
    /** 词语 */
    private String hotWords;
    /** 次数 */
    private Integer count;
}
