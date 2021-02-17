package com.meidianyi.shop.service.pojo.shop.member.account;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
* @author 黄壮壮
* @Date: 2019年8月9日
* @Description: 为会员添加会员卡入参
*/
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberCardParam {
	/** 会员id */
	@NotNull private List<Integer> userIdList;
	/** 会员卡id */
	@NotNull private List<Integer> cardIdList;
}
