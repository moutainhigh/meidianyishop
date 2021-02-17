package com.meidianyi.shop.service.pojo.shop.video;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author 新国
 *
 */
@Data
@NoArgsConstructor
public class VideoListQueryParam {

	/**
	 * 页码
	 */
	public Integer page;
	/**
	 * 分组id
	 */
	public Integer videoCatId;
	/**
	 * 关键词
	 */
	public String keywords;
	/**
	 * 每页显示的个数
	 */
	public  Integer pageRows;

	/**
	 * 宽度要求 （更多）
	 */
	public Integer videoWidth;
	/**
	 * 高度需求（更多）
	 */
	public Integer videoHeight;
	/**
	 * 排序
	 */
	public Integer uploadSortId;
};
