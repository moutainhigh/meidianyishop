package com.meidianyi.shop.service.pojo.shop.member.tag;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.member.BaseMemberPojo;
import lombok.Getter;
import lombok.Setter;

/**
 * 删除参数
 * @author 黄壮壮
 * 2019-07-11 18:19
 */
@Setter
@Getter
public class DeleteTagParam extends BaseMemberPojo {
	@NotNull(message = JsonResultMessage.MSG_MEMBER_TAG_ID_NOT_NULL)
	private Integer tagId;
}
