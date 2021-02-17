package com.meidianyi.shop.service.pojo.shop.summary.visit;

import com.meidianyi.shop.service.foundation.util.I18N;

import lombok.Data;

/**
 * 访问页面统计出参单项
 *
 * @author 郑保乐
 */
@Data
public class PageVisitVoItem {

    /**
     * 页面路径
     */
    private String pagePath;

    /**
     * 页面名称
     */
    @I18N(propertiesFileName = "page")
    private String pageName;

    /**
     * 入口页面次数
     */
    private String entryPagePv;

    /**
     * 退出页面次数
     */
    private String exitPagePv;

    /**
     * 退出页面次数
     */
    private Double exitRate;

    /**
     * 分享次数
     */
    private String pageSharePv;

    /**
     * 分享人数
     */
    private String pageShareUv;

    /**
     * 次均访问时长
     */
    private Double pageStayTimePv;

    /**
     * 访问次数
     */
    private String pageVisitPv;

    /**
     * 访问人数
     */
    private String pageVisitUv;
}
