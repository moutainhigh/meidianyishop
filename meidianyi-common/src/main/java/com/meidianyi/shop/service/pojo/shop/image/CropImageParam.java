package com.meidianyi.shop.service.pojo.shop.image;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 *
 * @author 新国
 *
 */
@Getter
@Setter
public class CropImageParam {
	/**
	 * 图片存放地址
	 */
	public String remoteImgPath;
	public Integer cropWidth;
	public Integer cropHeight;
	@NotNull
	public Integer x;
	@NotNull
	public Integer y;
	@NotNull
	public Integer w;
	@NotNull
	public Integer h;
	public Double imgScaleW;
	public Integer imgCatId;
	public Integer remoteImgId;
	private Integer size;
};
