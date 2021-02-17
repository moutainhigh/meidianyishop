package com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead;

import com.meidianyi.shop.service.pojo.shop.order.OrderParam;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 王帅
 * 代付详情
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InsteadPayDetailsParam extends OrderParam {
    private Integer currentPage;
    private Integer pageRows;
    private WxAppSessionUser wxUserInfo;

}
