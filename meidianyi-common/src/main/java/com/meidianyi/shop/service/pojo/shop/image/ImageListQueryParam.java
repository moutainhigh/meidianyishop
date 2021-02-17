package com.meidianyi.shop.service.pojo.shop.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 新国
 *
 */
@Getter
@Setter
public class ImageListQueryParam {

	/**
	 * 页码
	 */
	private Integer page;
	/**
	 * 分组id
	 */
	@NotNull
	private Integer imgCatId;
	/**
	 * 关键词
	 */
	private String keywords;
	/**
	 * 每页显示的个数
	 */
	private  Integer pageRows;
	/**
	 * 更多需求 1.开启 0关闭
	 */
	private Integer searchNeed;
	/**
	 * 宽度要求 （更多）
	 */
	private Integer needImgWidth;
	/**
	 * 高度需求（更多）
	 */
	private Integer needImgHeight;
	/**
	 * 排序
	 */
	private Integer uploadSortId;
    /**
     * imgCatId下的所有子分类id和imgCatId
     */
    @JsonIgnore
    private List<Integer> childCatIdsAndImgCatId = new ArrayList<>(1);
};
