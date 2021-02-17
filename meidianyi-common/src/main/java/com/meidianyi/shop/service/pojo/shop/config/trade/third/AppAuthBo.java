package com.meidianyi.shop.service.pojo.shop.config.trade.third;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 孔德成
 * @date 2020/5/18
 */
@Getter
@Setter
@ToString
public class AppAuthBo {
    private Integer id;
    /**
     * 授权状态 1启用 0禁用
     */
    private Byte status;
    /**
     * 授权key
     */
    private String sessionKey;
    /**
     * pos授权key
     */
    private String posSessionKey;
    /**
     * 1erp 2pos 卖家账号
     * 3 appkey
     */
    private String appKey;
    private String appSecret;
    /**
     * 1同步 0不同步
     */
    private Byte isSync;
    /**
     * 产品：1 ERP企业版 2：ERP旗舰版 3e快帮
     */
    private byte product;

}
