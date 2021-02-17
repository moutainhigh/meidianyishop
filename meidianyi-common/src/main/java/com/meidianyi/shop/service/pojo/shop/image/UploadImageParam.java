package com.meidianyi.shop.service.pojo.shop.image;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/** @author 新国 */
@Getter
@Setter
public class UploadImageParam {
  /** 图片宽度 */
  @NotNull
  public Integer needImgWidth;
  /** 图片高度 */
  @NotNull
  public Integer needImgHeight;
  /** 图片种类 -1：用户上传 */
  @NotNull
  public Integer imgCatId = 0;
  /** 用户id */
  public Integer userId = 0;

  public String uploadFileId;
  /**
   * 旋转角度
   */
  private String rotate ;
  /**
   * base64格式的图片
   */
  @NotNull(groups = UploadBase64Group.class)
  public String base64Image;
};
