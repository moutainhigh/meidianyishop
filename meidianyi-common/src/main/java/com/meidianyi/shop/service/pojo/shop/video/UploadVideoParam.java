package com.meidianyi.shop.service.pojo.shop.video;

import lombok.Data;

/**
 * @author 新国
 */
@Data
public class UploadVideoParam {
    public Integer videoWidth;
    public Integer videoHeight;
    public Integer videoCatId = 0;
    public String uploadFileId;
};
