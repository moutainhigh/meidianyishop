package com.meidianyi.shop.service.pojo.wxapp.medical.im.bo;

import com.meidianyi.shop.service.pojo.wxapp.medical.im.base.ImSessionItemBase;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 李晓冰
 * @date 2020年07月31日
 */
@Getter
@Setter
public class ImSessionItemBo extends ImSessionItemBase {

    private Integer imSessionId;

    private Integer fromId;

    private Integer toId;

    public ImSessionItemBo() {
    }

    public ImSessionItemBo(Integer imSessionId, Integer fromId, Integer toId,ImSessionItemBase base) {
        this.imSessionId = imSessionId;
        this.fromId = fromId;
        this.toId = toId;
        this.setMessage(base.getMessage());
        this.setType(base.getType());
        this.setSendTime(base.getSendTime());
    }
}
