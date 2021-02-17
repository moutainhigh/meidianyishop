package com.meidianyi.shop.service.pojo.shop.decoration.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class ModuleScrollImage extends ModuleBase {

    /**
     * 预览原图，0否，1是
     */
    @JsonProperty(value = "is_preview")
    Byte isPreview = 0;

    /**
     * 轮播速度/秒
     */
    @JsonProperty(value = "carousel_speed")
    Integer carouselSpeed = 5;


    /**
     * 轮播图列表
     */
    @JsonProperty(value = "img_items")
    List<ImageItem> imgItems = new ArrayList<>();

    @Getter
    @Setter
	public static class ImageItem{
		@JsonProperty(value = "img_url")
		String imageUrl;
		
		@JsonProperty(value = "title_link")
		String titleLink;
		
		@JsonProperty(value = "src_width")
		Integer srcWidth;
		
		@JsonProperty(value = "src_height")
		Integer srcHeight;
		
		/**
		 * 显示设置：0全部用户可见，1未在店铺内支付用户可见
		 */
		@JsonProperty(value = "can_show")
		Byte canShow = 0;
	}
}
