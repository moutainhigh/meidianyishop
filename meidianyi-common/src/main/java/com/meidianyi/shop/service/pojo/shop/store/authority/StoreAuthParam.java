package com.meidianyi.shop.service.pojo.shop.store.authority;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 解析门店权限json的类
 * 
 * @author zhaojianqiang
 * @time 上午10:50:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreAuthParam {
	public String name;
	public String enName;
	public Byte check;
	/** 2仅店长端账户可见；1仅店员端可见；0不限制 */
	private Byte isOnly;
    public String linkUrl;
    public Integer topIndex;
    public List<String> includeApi;
    public List<Sub> sub;
}
