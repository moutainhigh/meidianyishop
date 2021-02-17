package com.meidianyi.shop.service.pojo.saas.shop.mp;

import static com.meidianyi.shop.db.main.tables.MpAuthShop.MP_AUTH_SHOP;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.impl.DSL;

import lombok.Data;

/***
 * @author 李晓冰
 * @date 2019年08月07日
 */
@Data
public class MpAuthShopListParam {

    private Integer templateId;

    private Byte isAuthOk;

    /**是否开通微信支付 0否 1是*/
    private Byte openPay;

    /**审核状态 0未提交，1审核中，2审核成功，3审核失败*/
    private Byte auditState;

    /**发布状态 0 未发布 1已发布*/
    private Byte publishState;

    /**店铺状态 0 已过期，1正使用*/
    private Byte shopState;

    private String appId;

    private Integer shopId;

    private String nickName;

    public enum ShopState {
        EXPIRE,
        INUSE;
    }

    private Integer currentPage;
    private Integer pageRows;

    public Condition buildOption() {
        Condition condition = DSL.noCondition();
        if (templateId != null) {
            condition = condition.and(MP_AUTH_SHOP.BIND_TEMPLATE_ID.eq(templateId));
        }

        if (isAuthOk != null) {
            condition = condition.and(MP_AUTH_SHOP.IS_AUTH_OK.eq(isAuthOk));
        }

        if (openPay != null) {
            condition = condition.and(MP_AUTH_SHOP.OPEN_PAY.eq(openPay));
        }

        if (auditState != null) {
            condition = condition.and(MP_AUTH_SHOP.AUDIT_STATE.eq(auditState));
        }

        if (publishState != null) {
            condition = condition.and(MP_AUTH_SHOP.PUBLISH_STATE.eq(publishState));
        }



        if (!StringUtils.isBlank(appId)) {
            condition = condition.and(MP_AUTH_SHOP.APP_ID.eq(appId));
        }

        if (shopId != null) {
            condition = condition.and(MP_AUTH_SHOP.SHOP_ID.eq(shopId));
        }

        if (!StringUtils.isBlank(nickName)) {
            condition = condition.and(MP_AUTH_SHOP.NICK_NAME.like(likeValue(nickName)));
        }

        return condition;
    }

    private String likeValue(String val) {
        return "%" + likeReplace(val) + "%";
    }

    protected String likeReplace(String val) {
        val = val.replaceAll("%", "\\%");
        return val.replaceAll("_", "\\_");
    }
    
    public Condition buildOptionByUpload() {
        Condition condition = DSL.noCondition();
        if (templateId != null) {
            condition = condition.and(MP_AUTH_SHOP.BIND_TEMPLATE_ID.eq(templateId));
        }

        if (isAuthOk != null) {
            condition = condition.and(MP_AUTH_SHOP.IS_AUTH_OK.eq(isAuthOk));
        }
        //不等于
        if (auditState != null) {
            condition = condition.and(MP_AUTH_SHOP.AUDIT_STATE.notEqual(auditState));
        }

        return condition;
    }
}
