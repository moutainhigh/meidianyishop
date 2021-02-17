package com.meidianyi.shop.service.pojo.shop.member.tag;

import javax.validation.constraints.Size;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.member.BaseMemberPojo;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 标签管理入参
 * 
 * @author 黄壮壮 2019-07-09 11:09
 */

@Getter
@Setter
public class TagPageListParam extends BaseMemberPojo {
	
	@Size(max=15,message = JsonResultMessage.MSG_MEMBER_TAG_LENGTH_LIMIT)
	private String tagName;
}
