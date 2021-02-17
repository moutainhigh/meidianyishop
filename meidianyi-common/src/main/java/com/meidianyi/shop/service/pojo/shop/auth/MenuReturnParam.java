package com.meidianyi.shop.service.pojo.shop.auth;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class MenuReturnParam {
	private List<?> privilegeList;
	private List<?> privilegePass;
}
