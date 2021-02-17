package com.meidianyi.shop.service.pojo.shop.member.card.create;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * @author 黄壮壮
 * @Desc 会员卡自定义审核数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCustomAction {
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public static enum  ActionType{
		SINGLE(0),
		MULTIPLE(1),
		TEXT(2),
		PICTURE(3);
		public Byte val;
		ActionType(int i){
			this.val = (byte)i;
		}
	}
	/**
	 * 类型：0单选，1多选，2文本，3图片
	 */
	@JsonAlias({"type"})
	@JsonProperty("custom_type")
	private Byte type;
	
	/**
	 * 标题
	 */
	@JsonAlias({"title"})
	@JsonProperty("custom_title")
	private String title;
	
	/**
	 * 选项内容
	 */
	@JsonAlias({"content"})
	@JsonProperty("option_arr")
	private List<String> content;
	
	/**
	 * 条件校验必须 
	 */
	@JsonAlias({"conditionChecked"})
	@JsonProperty("option_ver")
	private Byte conditionChecked;
	
	/**
	 * 是否使用改激活项
	 */
	@JsonAlias({"checked"})
	@JsonProperty("is_checked")
	private Byte checked;
	
	/**
	 * 	上传的图片张数
	 */
	private Integer pictureNumber;
}
