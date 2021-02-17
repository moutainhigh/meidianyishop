package com.meidianyi.shop.service.pojo.saas.shop.mp;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年9月9日 下午4:16:42
 */
@Data
public class MpUploadListParam {
    private Byte state;

    private Integer currentPage;
    private Integer pageRows;
}
