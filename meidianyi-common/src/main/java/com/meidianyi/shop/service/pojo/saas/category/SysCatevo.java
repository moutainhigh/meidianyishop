package com.meidianyi.shop.service.pojo.saas.category;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author luguangyao
 */
@Data
public class SysCatevo {
    private Integer catId;
    private String catName;
    private Integer parentId;
    private Short level;
    private Integer hasChild;
    private Integer first;
	private Timestamp createTime;
	private Timestamp updateTime;
    /**
     * 数据库平台分类对应店家内的商品数量（不包含子孙商品数量）
     */
	private Integer goodsNumber;
    /**
     * 数据库平台分类对应店家内的商品数量（包含子孙商品数量的总和）
     */
	private Integer goodsNumberSum;
}
