package com.meidianyi.shop.service.pojo.shop.member.card;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * 
 * @author 黄壮壮
 * @Date: 2019年8月1日
 * @Description: 分页查询会员卡列表参数
 */
@Data
public class SearchCardParam {
	/** 会员卡类型 如： {@link com.meidianyi.shop.common.pojo.shop.member.card.CardConstant.NORMAL_TYPE } */
	private Byte cardType;
	/** 
	 * 分页信息
	 */
	/**
     * 分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
    @JsonAlias({"userId", "user_id"})
    private Integer userId;

    /**
     *	 是否过滤掉停用会员卡
     */
    private Boolean filterStop = Boolean.TRUE;
}
