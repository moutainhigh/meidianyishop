package com.meidianyi.shop.service.pojo.shop.sort;

import java.util.List;

import lombok.Data;

/**
 * 选择商家分类
 * @author 常乐
 * 2019年7月15日
 */
@Data
public class SortVo {
	public short sortId;
	public String sortName;
	public List<? extends SortVo> levelList2;
}
