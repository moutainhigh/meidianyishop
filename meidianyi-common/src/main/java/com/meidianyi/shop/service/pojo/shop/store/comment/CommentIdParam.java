package com.meidianyi.shop.service.pojo.shop.store.comment;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄荣刚
 * @date 2019年7月18日
 *
 */
@Data
@NoArgsConstructor
public class CommentIdParam {
	
	/** 需要操作的评论的ID列表 */
	@NotEmpty
	private List<Integer> commentIdList;
 
}
