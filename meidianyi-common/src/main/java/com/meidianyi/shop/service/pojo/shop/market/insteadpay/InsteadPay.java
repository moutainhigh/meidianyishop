package com.meidianyi.shop.service.pojo.shop.market.insteadpay;

import lombok.Data;

import java.util.List;

/**
 * 好友代付
 * @author 李晓冰
 * @date 2019年08月19日
 */
@Data
public class InsteadPay {

    /**
     * 是否开启好友代付，false未开启，true开启
     */
    private Boolean status;

    /**
     * 是否开启单人代付 false未开启，true开启
     */
    private Boolean singlePay;

    /**
     * 发起人默认留言，单人
     */
    private String orderUserMessageSingle;

    /**
     * 代付人默认留言，单人
     */
    private String insteadPayMessageSingle;

    /**
     * 是否开启多人代付false未开启，true开启
     */
    private Boolean multiplePay;

    /**
     * 发起人默认留言，多人
     */
    private String orderUserMessageMultiple;

    /**
     * 代付人默认留言，多人
     */
    private String insteadPayMessageMultiple;

    /**
     * 多人代付时的金额比例中文文字
     */
    private List<String> payRatioText;
    /**
     * 多人代付时的金额比例 比例值
     */
    private List<Integer> payRatioNumber;


    public static final Byte NOT_SET=0;
    public static final Byte ONE_WAY=1;
    public static final Byte TWO_WAY=2;
    /**
     * 代付人数量0未设置，1一种代付，2多种代付
     */
    private Byte insteadPayWay;
}
