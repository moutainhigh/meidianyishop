package com.meidianyi.shop.service.pojo.wxapp.coupon;

import jodd.util.StringUtil;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 优惠券详情入参类
 * @author 常乐
 * 2019年9月27日
 */
@Data
public class AvailCouponDetailParam {
	/**
	 * 优惠券码
	 */
	public String couponSn;
    /**
     * 优惠券活动ID
     */
	public Integer couponId;

	public Integer userId;

    /**
     * 小程序扫码进门店详情页带的参数
     */
    private String scene;

    public void initScene(){
        boolean canResolve = (this.couponId == null || this.couponId <= 0) && StringUtil.isNotBlank(this.scene);
        if(canResolve){
            String scene = null;
            try {
                scene = URLDecoder.decode(this.scene,"UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            if(StringUtil.isNotEmpty(scene)){
                String[] sceneParam = scene.split("=",2);
                this.couponId =  Integer.valueOf(sceneParam[1]);
            }
        }
    }
}
