package com.meidianyi.shop.service.pojo.shop.video.category;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 新国
 *
 */
@Data
public class CategoryTreeItemVo {
	private Integer id = 0;
	private String name ;
	private Integer level=0;
	private List<CategoryTreeItemVo> child=new ArrayList<>();
}
