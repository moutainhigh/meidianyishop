package com.meidianyi.shop.service.pojo.shop.order.write.operate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.StoreTokenAuthInfo;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.pojo.shop.order.base.IOrderBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2019-10-23 16:51
 **/
@Data
public abstract class AbstractOrderOperateQueryParam implements IOrderBase {
    @NotNull(message = JsonResultMessage.MSG_ORDER)
    private Byte action;
    /**区分前后台操作 */
    private Byte isMp;
    @JsonIgnore
    private AdminTokenAuthInfo adminInfo;
    @JsonIgnore
    private WxAppSessionUser wxUserInfo;
    @JsonIgnore
    private StoreTokenAuthInfo storeInfo;
    @Override
    public OrderServiceCode getServiceCode() {
        //enum类型values取得数组利用默认排序顺序获取对应service
        return OrderServiceCode.values()[action];
    }
}
