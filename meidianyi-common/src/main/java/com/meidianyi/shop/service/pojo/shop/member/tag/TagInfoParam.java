package com.meidianyi.shop.service.pojo.shop.member.tag;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.member.BaseMemberPojo;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 黄壮壮 2019-07-11 18:20
 */
@Getter
@Setter
public class TagInfoParam extends BaseMemberPojo {
	@NotBlank(message = JsonResultMessage.MSG_MEMBER_TAG_NOT_NULL)
	@Size(max = 15, message = JsonResultMessage.MSG_MEMBER_TAG_LENGTH_LIMIT)
	private String tagName;
}
