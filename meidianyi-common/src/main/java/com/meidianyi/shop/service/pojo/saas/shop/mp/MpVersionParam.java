package com.meidianyi.shop.service.pojo.saas.shop.mp;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年8月9日 上午10:19:54
 */
@Data
public class MpVersionParam {

    private Integer templateId;

    private Byte isAuthOk;

    /**是否开通微信支付 0否 1是*/
    private Byte openPay;

    /**审核状态 0未提交，1审核中，2审核成功，3审核失败*/
    private Byte auditState;

    /**发布状态 0 未发布 1已发布*/
    private Byte publishState;
    
    private Integer currentPage;
    private Integer pageRows;
}
