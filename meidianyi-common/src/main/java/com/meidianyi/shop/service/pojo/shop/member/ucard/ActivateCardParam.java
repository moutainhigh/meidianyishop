package com.meidianyi.shop.service.pojo.shop.member.ucard;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.meidianyi.shop.service.pojo.wxapp.card.param.CardCustomActionParam;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangzhuangzhuang
 */
@Data
@Builder
public class ActivateCardParam {
    @NotNull
    private String cardNo;
    /**
     * 1 为 设置激活卡信息
     */
    private Byte isSetting;
    private Integer userId;
    /**
     * 激活选项
     */
    JsonNode activateOption;
    /**
     * 自定义激活项
     */
    List<CardCustomActionParam> customOptions;

}
