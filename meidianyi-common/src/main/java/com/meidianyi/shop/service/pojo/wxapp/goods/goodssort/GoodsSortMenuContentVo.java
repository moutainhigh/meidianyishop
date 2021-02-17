package com.meidianyi.shop.service.pojo.wxapp.goods.goodssort;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年10月18日
 * 小程序-商品分类界面菜单返回内容类
 */
@Getter
@Setter
public class GoodsSortMenuContentVo extends MenuContentBaseVo{
    private String menuImg;
    private String menuImgLink;
    List<?> contentList;
}
