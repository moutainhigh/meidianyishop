package com.meidianyi.shop.service.pojo.wxapp.store.showmain;

import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountVo;
import lombok.Data;

import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/9/16
 **/
@Data
public class StoreMainShowVo {
    /**
     *门店账户
     */
    private StoreAccountVo storeAccount;
    /**
     * 门店订单数统计
     */
    private List<StoreStatisticVo> statisticList;
    /**
     * 月度数据
     */
    private StoreMonthStatisticVo monthVo;
}
