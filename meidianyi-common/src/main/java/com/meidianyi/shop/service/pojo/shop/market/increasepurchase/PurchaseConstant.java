package com.meidianyi.shop.service.pojo.shop.market.increasepurchase;

import lombok.Data;

/**
 * @author liufei
 * @date 2019/8/14
 * @description
 */
@Data
public class PurchaseConstant {
    /**
     * 加价购/分享有礼等营销页面通用常量参数
     * 页面分页展示分模块，所有0，进行中1 ，未开始2，已过期3，已停用4 ；筛选优先级高于下面的条件
     * <p>
     * 所有0
     */
    public static final byte PURCHASE_ALL = 0b0000;
    /**
     * 已停用4
     */
    public static final byte PURCHASE_TERMINATED = 0b0100;
    /**
     * 已过期3
     */
    public static final byte PURCHASE_EXPIRED = 0b0011;
    /**
     * 未开始2
     */
    public static final byte PURCHASE_PREPARE = 0b0010;
    /**
     * 进行中1
     */
    public static final byte PURCHASE_PROCESSING = 0b0001;

    /**
     * 获取分享二维码常量参数
     */
    public static final String IDENTITY_ID = "identity_id=";
    /**
     * 使用mysql GROUPCONCAT()函数 分隔符常量
     */
    public static final String GROUPCONCAT_SEPARATOR = " ; ";
    /**
     * 使用mysql CONCAT_WS()函数 分隔符常量
     */
    public static final String CONCAT_WS_SEPARATOR = " --- ";

    /**
     * 通用标识常量
     * <p>
     * 删除状态标识：0未删除，1已删除；默认查询结果不包含已删除信息
     * 活动状态标识：0未禁用（启用），1禁用
     * 是否永久标识，0非永久，1永久
     * 是否给用户打标签标志，1打标签
     */
    public static final Byte FLAG_ZERO = 0;
    public static final Byte FLAG_ONE = 1;

    /**
     * 辅助常量
     */
    public static final byte CONDITION_ZERO = 0;
    public static final byte CONDITION_ONE = 1;
    public static final byte CONDITION_TWO = 2;
    public static final byte CONDITION_THREE = 3;

    public static final Byte BYTE_THREE = 3;
}
