package com.meidianyi.shop.service.pojo.shop.member.ucard;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 * @time   上午10:15:48
 */
@Data
public class CardUseListParam {
	public String cardNo;
	public Byte showType;
	public Integer currentPage;
	public Integer pageRows;

}
