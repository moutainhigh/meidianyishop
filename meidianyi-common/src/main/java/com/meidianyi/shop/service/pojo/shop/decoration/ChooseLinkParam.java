package com.meidianyi.shop.service.pojo.shop.decoration;

import lombok.Data;

/**
 * @author changle
 */
@Data
public class ChooseLinkParam {
	public Integer catId;
    public String  pageName;
    /**
     * 	分页信息
     */
    private Integer currentPage;
    private Integer pageRows;
}
