package com.meidianyi.shop.service.pojo.wxapp.store;

import com.meidianyi.shop.db.shop.tables.records.ServiceOrderRecord;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConsumpData;
import lombok.Builder;
import lombok.Data;

/**
 * @author liufei
 * @date 11/25/19
 */
@Data
@Builder
public class ServiceOrderTran {
    private AccountParam account;
    private CardConsumpData cardConsumpData;
    private ServiceOrderRecord serviceOrder;
}
