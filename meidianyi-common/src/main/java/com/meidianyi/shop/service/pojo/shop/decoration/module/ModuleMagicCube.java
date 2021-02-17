package com.meidianyi.shop.service.pojo.shop.decoration.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class ModuleMagicCube extends ModuleBase {

	/**
	 * 魔方模板：0自定义；1：1行2个；2：1行3个；3：1行4个；4：2行2个；5：1左2右，6：1上2下；7 1左三右
	 */
	@JsonProperty(value = "table_type")
	Byte tableType = 1;
	
	/**
	 * 魔方表格大小
	 */
	@JsonProperty(value = "table_size")
	TableSize tableSize;
	
	/**
	 * 魔方块数据
	 */
    @JsonProperty(value = "data")
	Map<String,BlockItem> data;

    @Getter
    @Setter
	public static class TableSize{
        @JsonProperty(value = "rows")
		Integer rows;
        @JsonProperty(value = "cols")
		Integer cols;
	}

    @Getter
    @Setter
	public static class BlockItem{
		
		/**
		 * 魔方块名称
		 */
		@JsonProperty(value = "name")
		String name;
		
		/**
		 * 魔方块名称x位置
		 */
        @JsonProperty(value = "x")
		Integer x;
		
		/**
		 * 魔方块名称y位置
		 */
        @JsonProperty(value = "y")
		Integer y;
		
		/**
		 * 魔方块覆盖行数
		 */
        @JsonProperty(value = "rows")
		Integer rows;
		
		/**
		 * 魔方块覆盖列数
		 */
        @JsonProperty(value = "cols")
		Integer cols;
		
		/**
		 * 图片链接
		 */
		@JsonProperty(value = "img_url")
		String imgUrl;
		

		/**
		 * 跳转链接
		 */
		@JsonProperty(value = "jump_link")
		String jumpLink;
	}
}
