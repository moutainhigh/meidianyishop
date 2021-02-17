package com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch;

import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * mq参数
 * @author 王帅
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BatchShipMqParam {
    public Integer shopId;
    private Integer taskJobId;
    private AdminTokenAuthInfo adminInfo;
    private List<BatchShipPojo> info;

    public BatchShipMqParam(Integer shopId, AdminTokenAuthInfo adminInfo, List<BatchShipPojo> info) {
        this.shopId = shopId;
        this.info = info;
        this.adminInfo = adminInfo;
    }
}
