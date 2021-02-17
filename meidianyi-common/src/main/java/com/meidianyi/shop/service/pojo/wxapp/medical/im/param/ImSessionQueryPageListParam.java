package com.meidianyi.shop.service.pojo.wxapp.medical.im.param;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

/**
 * @author yangpengcheng
 * @date 2020/8/12
 **/
@Data
public class ImSessionQueryPageListParam extends BasePageParam {
    String orderSn;
}
