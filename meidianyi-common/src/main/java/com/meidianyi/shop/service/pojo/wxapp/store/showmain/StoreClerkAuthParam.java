package com.meidianyi.shop.service.pojo.wxapp.store.showmain;

import lombok.Data;

/**
 * @author yangpengcheng
 * @date 2020/9/17
 **/
@Data
public class StoreClerkAuthParam {
    /**
     * 账户名
     */
    private String accountName;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 验证码
     */
    private String mobileCheckCode;
    /**
     * 当前用户id
     */
    private Integer userId;
    private Integer shopId;
    /**
     * 是否是药师
     */
    private Byte isPharmacist;
    /**
     * 药师签名
     */
    private String signature;
}
