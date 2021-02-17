package com.meidianyi.shop.service.pojo.wxapp.medical.im.vo;

import com.meidianyi.shop.service.pojo.wxapp.medical.im.base.ImSessionItemBase;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 李晓冰
 * @date 2020年07月21日
 */
@Getter
@Setter
public class ImSessionItemRenderVo extends ImSessionItemBase {
    /**是否是医师话语*/
    private boolean isDoctor;
}
