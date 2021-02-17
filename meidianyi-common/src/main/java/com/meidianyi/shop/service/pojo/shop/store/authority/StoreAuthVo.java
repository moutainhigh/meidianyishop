package com.meidianyi.shop.service.pojo.shop.store.authority;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 * @time 下午2:29:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreAuthVo {
	private List<StoreAuthParam> authList;
	private List<String> funCfg;
}
