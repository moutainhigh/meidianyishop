package com.meidianyi.shop.service.pojo.wxapp.video;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 孔德成
 * @date 2020/3/20
 */
@Data
@NoArgsConstructor
public class WxappUploadVideoParam {
    public Integer videoWidth;
    public Integer videoHeight;
    public Integer videoCatId = 0;
    public String uploadFileId;
}
