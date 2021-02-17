package com.meidianyi.shop.service.pojo.wxapp.medical.im.vo;

import com.meidianyi.shop.service.pojo.wxapp.medical.im.base.ImSessionItemBase;
import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年08月05日
 */
@Data
public class ImSessionUnReadInfoVo {
    private Integer sessionId;
    private Integer doctorId;
    private String doctorName;
    private List<ImSessionItemBase> messageInfos;
}
