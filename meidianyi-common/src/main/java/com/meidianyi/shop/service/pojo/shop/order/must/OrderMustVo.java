package com.meidianyi.shop.service.pojo.shop.order.must;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.Util;

import lombok.Data;

/**
 * @author: 王兵兵
 * @create: 2019-10-17 17:08
 * 下单必填信息
 **/
@Data
public class OrderMustVo {
    private String lang;
    private static final String LANGUAGE_TYPE_EXCEL = "excel";

    private Integer   id;
    private String    orderSn;
    /** 必填信息 */
    private String    mustContent;
    /** 下单人真实姓名 */
    private String    orderRealName;
    /** 下单人身份证号码 */
    private String    orderCid;
    /** 收货人真实姓名 */
    private String    consigneeRealName;
    /** 收货人身份证号码 */
    private String    consigneeCid;
    /** 自定义信息标题 */
    private String    customTitle;
    /** 自定义信息内容 */
    private String    custom;


    /** 翻译并格式化输出下单必填信息 */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(Util.translateMessage(lang, JsonResultMessage.ORDER_MUST_ORDER_REAL_NAME,LANGUAGE_TYPE_EXCEL));
        sb.append(":").append(orderRealName == null ? "" : orderRealName).append(";");

        sb.append(Util.translateMessage(lang, JsonResultMessage.ORDER_MUST_ORDER_CID,LANGUAGE_TYPE_EXCEL));
        sb.append(":").append(orderCid == null ? "" : orderCid).append(";");

        sb.append(Util.translateMessage(lang, JsonResultMessage.ORDER_MUST_CONSIGNEE_REAL_NAME,LANGUAGE_TYPE_EXCEL));
        sb.append(":").append(consigneeRealName == null ? "" : consigneeRealName).append(";");

        sb.append(Util.translateMessage(lang, JsonResultMessage.ORDER_MUST_CONSIGNEE_CID,LANGUAGE_TYPE_EXCEL));
        sb.append(":").append(consigneeCid == null ? "" : consigneeCid).append(";");

        if(customTitle != null && custom != null){
            sb.append(customTitle);
            sb.append(":").append(custom).append(";");
        }

        return sb.toString();
    }
}
