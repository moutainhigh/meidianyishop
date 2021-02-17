package com.meidianyi.shop.dao.main;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.service.pojo.shop.auth.StoreAuthConstant;
import com.meidianyi.shop.service.pojo.shop.auth.StoreLoginParam;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountVo;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreClerkAuthParam;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.meidianyi.shop.db.main.Tables.STORE_ACCOUNT;

/**
 * @author chenjie
 * @date 2020年08月21日
 */
@Repository
public class StoreAccountDao extends MainBaseDao {
    /**
     * 根据登录信息获取对应信息
     * @param param
     * @return
     */
    public StoreAccountVo getStoreAccountInfo(StoreLoginParam param){
        return db().selectFrom(STORE_ACCOUNT)
            .where(STORE_ACCOUNT.SYS_ID.eq(param.getSysId()))
            .and(STORE_ACCOUNT.ACCOUNT_TYPE.eq(param.getStoreAccountType()))
            .and(STORE_ACCOUNT.DEL_FLAG.eq(StoreAuthConstant.DEL_NORMAL))
            .and(STORE_ACCOUNT.ACCOUNT_NAME.eq(param.getStoreUsername()).or(STORE_ACCOUNT.MOBILE.eq(param.getStoreUsername())))
            .fetchAnyInto(StoreAccountVo.class);
    }

    /**
     * 获取门店登录账号信息
     * @param storeAccountId
     * @return
     */
    public StoreAccountVo getOneInfo(Integer storeAccountId){
        return db().selectFrom(STORE_ACCOUNT)
            .where(STORE_ACCOUNT.ACCOUNT_ID.eq(storeAccountId))
            .fetchAnyInto(StoreAccountVo.class);
    }

    public int upateBind(Integer storeAccountId, String officalOpenId, byte bind) {
        return db().update(STORE_ACCOUNT).set(STORE_ACCOUNT.OFFICIAL_OPEN_ID, officalOpenId)
            .set(STORE_ACCOUNT.IS_BIND, bind).where(STORE_ACCOUNT.ACCOUNT_ID.eq(storeAccountId)).execute();

    }

    public int updateRowBind(Integer storeAccountId,byte bind) {
        return db().update(STORE_ACCOUNT).set(STORE_ACCOUNT.IS_BIND, bind)
            .where(STORE_ACCOUNT.ACCOUNT_ID.eq(storeAccountId)).execute();
    }

    /**
     * 获得绑定第三方公众号的账号
     * @param shopId
     * @param storeId
     * @return
     */
    public List<StoreAccountVo> getStoreAccountByBindThird(Integer shopId, Integer storeId) {
        return db().selectFrom(STORE_ACCOUNT)
            .where(STORE_ACCOUNT.SHOP_ID.eq(shopId))
            .and(DslPlus.findInSet(storeId, STORE_ACCOUNT.STORE_LIST))
            .fetchInto(StoreAccountVo.class);
    }

    /**
     * 店员认证
     * @param param
     * @return
     */
    public StoreAccountVo storeAccountAuth(StoreClerkAuthParam param){
        return db().select().from(STORE_ACCOUNT).where(STORE_ACCOUNT.ACCOUNT_NAME.eq(param.getAccountName()))
            .and(STORE_ACCOUNT.DEL_FLAG.eq(StoreAuthConstant.DEL_NORMAL))
            .and(STORE_ACCOUNT.MOBILE.eq(param.getMobile()))
            .and(STORE_ACCOUNT.SHOP_ID.eq(param.getShopId()))
            .fetchAnyInto(StoreAccountVo.class);
    }

    /**
     * 更新用户id
     * @param accountId
     * @param userId
     */
    public void updateUserId(Integer accountId,Integer userId){
        db().update(STORE_ACCOUNT).set(STORE_ACCOUNT.USER_ID,userId)
            .set(STORE_ACCOUNT.AUTH_TIME, DateUtils.getLocalDateTime())
            .where(STORE_ACCOUNT.ACCOUNT_ID.eq(accountId))
            .execute();
    }
    /**
     * 更新签名
     * @param accountId
     * @param signature
     */
    public void updateSignature(Integer accountId,String signature){
        db().update(STORE_ACCOUNT).set(STORE_ACCOUNT.SIGNATURE,signature)
            .set(STORE_ACCOUNT.IS_PHARMACIST,(byte)1)
            .where(STORE_ACCOUNT.ACCOUNT_ID.eq(accountId))
            .execute();
    }

    /**
     * 更新用户token
     * @param accountId
     * @param userToken
     */
    public void updateUserToken(Integer accountId,String userToken){
        db().update(STORE_ACCOUNT).set(STORE_ACCOUNT.USER_TOKEN,userToken).where(STORE_ACCOUNT.ACCOUNT_ID.eq(accountId))
            .execute();
    }


    /**
     * 通过用户id查账号id
     * @param userId
     * @return
     */
    public StoreAccountVo getByUserId(Integer userId){
        return db().select().from(STORE_ACCOUNT).where(STORE_ACCOUNT.USER_ID.eq(userId)).fetchAnyInto(StoreAccountVo.class);
    }
}
