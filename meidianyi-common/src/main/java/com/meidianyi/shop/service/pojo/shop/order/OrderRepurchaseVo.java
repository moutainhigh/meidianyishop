package com.meidianyi.shop.service.pojo.shop.order;

import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 孔德成
 * @date 2020/4/26
 */
@Getter
@Setter
public class OrderRepurchaseVo {
    /**
     * 状态 1 成功 2 部分成功
     */
    private Byte status;
    /**
     * 提示信息
     */
    private String content;
    private Integer cardId;
    ResultMessage resultMessage;
}
