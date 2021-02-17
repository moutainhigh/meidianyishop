package com.meidianyi.shop.dao.main;

import com.meidianyi.shop.common.pojo.main.table.AppAuthDo;
import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.main.Tables.APP_AUTH;

/**
 * @author 李晓冰
 * @date 2020年07月15日
 */
@Repository
public class AppAuthDao extends MainBaseDao {

    /**
     * 获取应用授权信息
     * @param shopId 店铺id
     * @param appId 对接服务类型id
     * @param status 应用授权状态
     * @return
     */
    public AppAuthDo getAppAuth(Integer shopId, String appId,Byte status) {
        return db().selectFrom(APP_AUTH).where(APP_AUTH.SHOP_ID.eq(shopId).and(APP_AUTH.APP_ID.eq(appId)).and(APP_AUTH.STATUS.eq(status)))
            .fetchAnyInto(AppAuthDo.class);
    }


}
