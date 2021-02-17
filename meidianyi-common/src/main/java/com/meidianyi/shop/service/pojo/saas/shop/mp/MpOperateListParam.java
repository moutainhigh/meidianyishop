package com.meidianyi.shop.service.pojo.saas.shop.mp;

import static com.meidianyi.shop.db.main.Tables.MP_OPERATE_LOG;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.impl.DSL;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2019年08月07日
 */
@Data
public class MpOperateListParam {

    private Integer templateId;
    private String appId;

    private Integer currentPage;
    private Integer pageRows;

    public Condition buildOption() {
        Condition condition = DSL.noCondition();

        if (templateId!=null) {
            condition = condition.and(MP_OPERATE_LOG.TEMPLATE_ID.eq(templateId));
        }

        if (!StringUtils.isBlank(appId)) {
            condition = condition.and(MP_OPERATE_LOG.APP_ID.eq(appId));
        }

        return condition;
    }
}
