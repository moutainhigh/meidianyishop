package com.meidianyi.shop.service.pojo.shop.overview.hotwords;

import lombok.Data;

/**
 * 热词及其搜索次数
 * @author liangchen
 * @date 2019.12.20
 */
@Data
public class WordsAndCount {
    /** 热词 */
    private String hotWords;
    /** 搜索次数 */
    private Integer count;
}
