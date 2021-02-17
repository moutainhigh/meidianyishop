package com.meidianyi.shop.service.pojo.wxapp.store;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jodd.util.StringUtil;
import lombok.Data;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author liufei
 * @date 10/18/19
 */
@Data
@ScriptAssert(lang = "javascript", script = "_this.userId == null || _this.userId >= 0", message = "userId可空,但上传时必须为大于等于0的整数")
public class StoreInfoParam {
    @PositiveOrZero
    @JsonAlias({"store_id", "storeId"})
    public Integer storeId;
    @NotNull(groups = {StorePayOrder.class})
    public Integer userId;

    /**
     * 小程序扫码进门店详情页带的参数
     */
    private String scene;

    // 以下字段为门店买单所需入参
    /**
     * The Order info.订单信息
     * "{"store_id":"55","card_no":"47481606403254132","order_amount":"0.01","card_dis":"0.01","invoice":62,"score_dis":0,"total_price":"0.00",
     * "openid":"o-2MM5ANXgJHG_NBG5G-WX-KPjKI","form_id":"the formId is a mock one"}"
     */
    @Valid
    @NotNull(groups = {StoreConfirmPay.class})
    public StorePayOrderInfo orderInfo;

    /**
     * 客户端ip
     */
    private String clientIp;

    /**
     * 是否开启位置授权，0未开启，1开启（默认未开启）
     */
    @JsonProperty("location_auth")
    @JsonAlias({"location_auth", "locationAuth"})
    public byte locationAuth = 0;

    /**
     * The Location.用户位置信息json
     * "{"latitude":39.95933,"longitude":116.29845,"speed":-1,"accuracy":65,"verticalAccuracy":65,"horizontalAccuracy":65,"errMsg":"getLocation:ok"}"
     */
    public Location location;

    public void setScene(){
        boolean canResolve = (this.storeId == null || this.storeId <= 0) && StringUtil.isNotBlank(this.scene);
        if(canResolve){
            String scene = null;
            try {
                scene = URLDecoder.decode(this.scene,"UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            if(StringUtil.isNotEmpty(scene)){
                String[] sceneParam = scene.split("=",2);
                this.storeId =  Integer.valueOf(sceneParam[1]);
            }
        }
    }
}
