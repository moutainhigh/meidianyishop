package com.meidianyi.shop.service.pojo.wxapp.medical.im.param;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2020年08月05日
 */
@Data
public class ImSessionUnReadMessageInfoParam {
    /**医生查看未读消息使用*/
    private Integer doctorId;
    /**用户查看未读消息内容*/
    private Integer userId;
}
