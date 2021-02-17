package com.meidianyi.shop.service.pojo.shop.member.tag;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;

import lombok.Data;

/**
 * @author 黄壮壮
 * 2019-07-12 11:58
 */
@Data
public class UpdateTagParam {

	@NotNull(message = JsonResultMessage.MSG_MEMBER_TAG_ID_NOT_NULL)
	private Integer tagId;
	
	@NotBlank(message = JsonResultMessage.MSG_MEMBER_TAG_NOT_NULL)
	@Size(max = 15, message = JsonResultMessage.MSG_MEMBER_TAG_LENGTH_LIMIT)
	private String tagName;
}
