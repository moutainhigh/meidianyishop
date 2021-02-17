package com.meidianyi.shop.service.pojo.shop.distribution;

import com.meidianyi.shop.service.pojo.shop.image.UploadImageParam;
import com.meidianyi.shop.service.pojo.shop.image.UploadPath;
import lombok.Data;

/**
 * 分销员微信二维码入参
 * @author panjing
 * @data 2020/7/31 10:33
 */
@Data
public class DistributorImageParam {
    /**
     * 分销员id
     */
    private Integer distributorId;

    /**
     * 分销员微信图片id
     */
    private Integer imageId;

    /**
     * 提交上来的图片名称
     */
    private String submittedFileName;

    /**
     * 图片类型
     */
    private String contentType;

    /**
     * 图片大小
     */
    private Integer size;

    /**
     * 图片路径相关参数
     */
    private UploadPath uploadPath;

    /**
     * 图片上传所需参数
     */
    private UploadImageParam uploadImageParam;


}
