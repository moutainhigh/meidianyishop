package com.meidianyi.shop.service.pojo.shop.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author 新国
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchConfig {
	
	/**
	 *  1：不设置，2：全部商品，3：自定义
	 */
	@JsonProperty(value = "title_action")
	public Integer titleAction = 1;
	
	/**
	 * 自定义搜索值
	 */
	@JsonProperty(value = "title_custom")
	public String titleCustom;
	
	/**
	 * 是否开启搜索历史 0关闭，1开启
	 */
	@JsonProperty(value = "is_open_history")
	public Integer isOpenHistory = 1;
	
	/**
	 * 是否开启热词 0关闭，1开启
	 */
	@JsonProperty(value = "is_open_hot_words")
	public Integer isOpenHotWords = 0;
	
	/**
	 * 热词列表
	 */
	@JsonProperty(value = "hot_words")
	public List<String> hotWords = new ArrayList<String>(0);

	public SearchConfig(Integer titleAction, Integer isOpenHistory, Integer isOpenHotWords) {
		super();
		this.titleAction = titleAction;
		this.isOpenHistory = isOpenHistory;
		this.isOpenHotWords = isOpenHotWords;
	}
	
}
