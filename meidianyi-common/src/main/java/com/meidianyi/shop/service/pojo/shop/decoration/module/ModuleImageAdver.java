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
public class ModuleImageAdver extends ModuleBase {
	/**
	 * 图片模板：1单列图，2双列图，3横向滑动（大）4横向滑动（小）5横向滑动（导航）
	 */
	@JsonProperty(value = "image_type")
	Byte imageType = 0;
	
	/**
	 * 是否预览原图：0否，1是
	 */
	@JsonProperty(value = "is_preview")
	Byte isPreview = 0;
	
	/**
	 * 图片间距：0-20
	 */
	@JsonProperty(value = "image_space")
	Byte imageSpace = 0;
	
	/**
	 * 模块名称
	 */
	@JsonProperty(value = "module_title")
	String moduleTitle="";
	
	/**
	 * 图片列表
	 */
	@JsonProperty(value = "image_list")
	List<ImageAdItem> imageList = new ArrayList<>();

    @Getter
    @Setter
	public static class ImageAdItem{
		
		/**
		 * 图片地址
		 */
		@JsonProperty(value = "image")
		String image;
		
		/**
		 * 图片宽度
		 */
		@JsonProperty(value = "width")
		Integer width;
		
		/**
		 * 图片高度
		 */
		@JsonProperty(value = "height")
		Integer height;
		
		/**
		 * 图片链接
		 */
		@JsonProperty(value = "link")
		String link;

        /**
         * 是否只有新用户可见，1是，0所以用户可见
         */
        @JsonProperty(value = "can_show")
		private Byte canShow;

        /**
         * title
         */
        @JsonProperty(value = "title")
        private String title;
	}
}
