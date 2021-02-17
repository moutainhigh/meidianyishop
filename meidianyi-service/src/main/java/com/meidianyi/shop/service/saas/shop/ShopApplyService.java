package com.meidianyi.shop.service.saas.shop;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.main.tables.records.ChargeRenewRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.ChargeRenewAddParam;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import static com.meidianyi.shop.db.main.tables.ChargeRenew.CHARGE_RENEW;

/**
 * @author: 王兵兵
 * @create: 2020-05-06 15:11
 **/
@Service
public class ShopApplyService extends MainBaseService {

    /**
     * 记录申请信息
     *
     * @param param
     * @param authInfo
     * @return -1表示今天已经申请过了
     */
    public Byte insertChargeRenew(ChargeRenewAddParam param, AdminTokenAuthInfo authInfo) {
        if (
            db().fetchExists(CHARGE_RENEW, CHARGE_RENEW.APPLY_ID.eq(authInfo.subAccountId == 0 ? authInfo.sysId : authInfo.subAccountId).and(CHARGE_RENEW.APPLY_TYPE.eq(param.getApplyType())).and(CHARGE_RENEW.CREATED.ge(Timestamp.valueOf(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_BEGIN, DateUtils.getLocalDateTime())))))
        ) {
            return -1;
        } else {
            ChargeRenewRecord chargeRenewRecord = db().newRecord(CHARGE_RENEW);
            chargeRenewRecord.setApplyId(authInfo.subAccountId == 0 ? authInfo.sysId : authInfo.subAccountId);
            chargeRenewRecord.setApplyName(authInfo.userName);
            chargeRenewRecord.setSysId(authInfo.sysId);
            chargeRenewRecord.setShopId(authInfo.loginShopId);
            chargeRenewRecord.setShopName(saas.shop.getShopById(authInfo.loginShopId).getShopName());
            chargeRenewRecord.setCreated(DateUtils.getLocalDateTime());
            chargeRenewRecord.setApplyMod(param.getApplyMod());
            chargeRenewRecord.setApplyType(param.getApplyType());
            chargeRenewRecord.insert();
            return 1;
        }
    }
}
