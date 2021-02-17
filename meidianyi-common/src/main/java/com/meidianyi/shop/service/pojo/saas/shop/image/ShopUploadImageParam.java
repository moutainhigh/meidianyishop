package com.meidianyi.shop.service.pojo.saas.shop.image;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2019/7/16 19:00
 */
@Data
public class ShopUploadImageParam {
    public Integer needImgWidth;
    public Integer needImgHeight;
    public Integer imgCatId = 0;
    public String uploadFileId;

}
