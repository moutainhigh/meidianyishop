package com.meidianyi.shop.service.pojo.saas.shop.image;

import java.sql.Timestamp;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author 孔德成
 * @date 2019/7/17 9:56
 */
@Getter
@Setter
public class ShopUploadedImageVo {
    private Integer   imgId;
    private String    imgType;
    private Integer   imgSize;
    private String    imgName;
    private String    imgPath;
    private String    imgUrl;
    private Integer   imgCatId;
    private Integer   imgWidth;
    private Integer   imgHeight;
    private Timestamp createTime;
}
