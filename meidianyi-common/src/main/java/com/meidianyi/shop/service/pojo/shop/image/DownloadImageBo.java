package com.meidianyi.shop.service.pojo.shop.image;

import lombok.Data;

/**
 * 外链下载并上传upYun后信息类
 * @author 李晓冰
 * @date 2020年03月24日
 */
@Data
public class DownloadImageBo {
    private String imageName;
    private int width;
    private int height;
    private int size;
    private String relativeFilePath;
    private String imgUrl;
    private String imageType;
    private boolean alreadyHas;
}
