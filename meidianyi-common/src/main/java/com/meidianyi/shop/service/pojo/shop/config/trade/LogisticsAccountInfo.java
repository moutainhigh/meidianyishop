package com.meidianyi.shop.service.pojo.shop.config.trade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * The type Logistics account info.
 *
 * @author liufei
 * @date 2019 /9/23
 */
@Data
public class LogisticsAccountInfo {
    /**
     * The Biz id.快递公司客户编码
     */
    @JsonProperty(value = "biz_id")
    public String bizId;
    /**
     * The Delivery id.快递公司 ID
     */
    @JsonProperty(value = "delivery_id")
    public String deliveryId;
    /**
     * The Delivery name.快递公司名称
     */
    @JsonProperty(value = "delivery_name")
    public String deliveryName;
    /**
     * The Create time.账号绑定时间
     */
    @JsonIgnore
    public Timestamp createTime;
    /**
     * The Update time.账号更新时间
     */
    @JsonIgnore
    public Timestamp updateTime;
    /**
     * The Status code.绑定状态
     * 0	绑定成功, 1	审核中, 2	绑定失败, -1    未绑定
     */
    @JsonProperty(value = "status_code")
    public Byte statusCode;
    /**
     * The Alias.账号别名
     */
    @JsonIgnore
    public String alias;
    /**
     * The Remark wrong msg.账号绑定失败的错误信息（EMS审核结果）
     */
    @JsonIgnore
    public String remarkWrongMsg;
    /**
     * The Remark content.账号绑定时的备注内容（提交EMS审核需要)）
     */
    @JsonIgnore
    public String remarkContent;
    /**
     * The Quota num.电子面单余额
     */
    @JsonIgnore
    public BigDecimal quotaNum;
    /**
     * The Quota update time.电子面单余额更新时间
     */
    @JsonIgnore
    public Timestamp quotaUpdateTime;

    public LogisticsAccountInfo() {
    }

    public LogisticsAccountInfo(String deliveryId, String deliveryName, Byte statusCode) {
        this.deliveryId = deliveryId;
        this.deliveryName = deliveryName;
        this.statusCode = statusCode;
    }
}
