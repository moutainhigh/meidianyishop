package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author liufei
 * date 2019/7/18
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssiDataMarket implements PendingRule<AssiDataMarket> {
    /** 分销审核超时 0: 分销员审核进度良好 否有examine个分销员申请超过3天未处理 */
    public Metadata examine;
    /**
     * 会员卡激活审核，主要取决于card_num的数量
     * K---card_id：card_num非0时表示待处理申请的会员卡id
     * K---card_name：card_num非0时表示待处理申请的会员卡名称
     * K---card_num：为0表示会员卡激活审核进度良好，否表示card_name类会员卡有card_num个会员卡激活申请超过2天未处理
     */
    public Metadata member;
    /**
     * 优惠券库存偏小
     * K---id:优惠券id
     * V---actName:优惠券名称
     */
    public Metadata voucher;

    @Override
    public AssiDataMarket ruleHandler() {
        handler1(examine, member, voucher);
        return this;
    }

    @Override
    public int getUnFinished() {
        int num = unFinished(examine, member) +
            (Objects.isNull(voucher.getContent()) ? 0 : voucher.getContent().size());
        log.debug("Market unFinished Num:{}", num);
        return num;
    }
}
