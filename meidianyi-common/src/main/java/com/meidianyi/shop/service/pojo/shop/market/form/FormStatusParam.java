package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;

/**
 * @author liufei
 * @date 2019/8/8
 */
@Data
public class FormStatusParam {
    private Integer pageId;
    /** 状态：0未发布，1已发布 2已关闭 3 已删除*/
    private Byte formStatus;
}
