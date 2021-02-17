package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

/**
 * @author liufei
 * date 2019/7/18
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssiDataOrder implements PendingRule<AssiDataOrder> {
    final Byte BYTE_TWO = 2;
    /**
     * 发货逾期 value非零表示有deliver个订单超过3天未发货 否订单发货进度良好
     */
    public Metadata deliver;
    /**
     * 退款申请逾期 value非零表示有refund个订单退款申请超过3天未处理 否退款处理进度良好
     */
    public Metadata refund;

    /**
     * 提醒发货 value非零表示有N个订单提醒发货，否则订单发货进度良好
     */
    public Metadata remind;

    @Override
    public AssiDataOrder ruleHandler() {
        handler1(deliver, refund, remind);
        return this;
    }

    public AssiDataOrder setType() {
        if (deliver.getStatus() == BYTE_ONE) {
            deliver.setType(BYTE_ONE);
        } else if (deliver.getStatus() == BYTE_ZERO) {
            deliver.setType(BYTE_TWO);
        }

        if (refund.getStatus() == BYTE_ONE) {
            refund.setType(BYTE_ONE);
        } else if (refund.getStatus() == BYTE_ZERO) {
            refund.setType(BYTE_TWO);
        }

        if (remind.getStatus() == BYTE_ONE) {
            remind.setType(BYTE_ONE);
        } else if (remind.getStatus() == BYTE_ZERO) {
            remind.setType(BYTE_TWO);
        }
        return this;
    }

    @Override
    public int getUnFinished() {
        int num = unFinished(deliver, refund, remind);
        log.debug("Order unFinished Num:{}", num);
        return num;
    }
}
