package com.meidianyi.shop.service.pojo.wxapp.store;

import jodd.util.StringUtil;
import lombok.Data;

import javax.validation.constraints.PositiveOrZero;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author liufei
 * @date 11/7/19
 */
@Data
public class ReservationParam {
    private Integer serviceId;
    @PositiveOrZero(groups = {ConfirmReservation.class, ValidCon.class, ValidCon1.class})
    private Integer userId;
    private Byte orderStatus;

    /**
     * 小程序扫码进门店详情页带的参数
     */
    private String scene;

    public void initScene(){
        boolean canResolve = (this.serviceId == null || this.serviceId <= 0) && StringUtil.isNotBlank(this.scene);
        if(canResolve){
            String scene = null;
            try {
                scene = URLDecoder.decode(this.scene,"UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            if(StringUtil.isNotEmpty(scene)){
                String[] sceneParam = scene.split("=",2);
                this.serviceId =  Integer.valueOf(sceneParam[1]);
            }
        }
    }
}
