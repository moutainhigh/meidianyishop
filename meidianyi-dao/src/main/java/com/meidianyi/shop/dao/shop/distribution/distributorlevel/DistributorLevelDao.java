package com.meidianyi.shop.dao.shop.distribution.distributorlevel;

import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.DistributorLevelRecord;
import com.meidianyi.shop.db.shop.tables.records.UserDetailRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.meidianyi.shop.common.foundation.data.DistributionConstant.LEVEL_STATUS_ON;
import static com.meidianyi.shop.common.foundation.data.DistributionConstant.LEVEL_UP_ROUTE_AUTO;
import static com.meidianyi.shop.db.shop.Tables.USER_DETAIL;
import static com.meidianyi.shop.db.shop.tables.DistributorLevel.DISTRIBUTOR_LEVEL;
import static com.meidianyi.shop.db.shop.tables.User.USER;


/**
 * @author changle
 * @date 2020/8/17 2:43 下午
 */
@Repository
public class DistributorLevelDao extends ShopBaseDao {
    /**
     * 分销员基本信息
     * @param userId
     * @return
     */
    public UserRecord getUserInfo(Integer userId){
        UserRecord into = db().select().from(USER).where(USER.USER_ID.eq(userId)).fetchOne().into(UserRecord.class);
        return into;
    }

    /**
     * 获取分销员详情信息
     * @param userId
     * @return
     */
    public UserDetailRecord getUserDetail(Integer userId){
        UserDetailRecord into = db().select().from(USER_DETAIL).where(USER_DETAIL.USER_ID.eq(userId)).fetchOne().into(UserDetailRecord.class);
        return into;
    }

    /**
     * 根据levelId查找当前分销等级信息
     * @param levelId
     * @return
     */
    public DistributorLevelRecord getDistributorLevelByLevelId(Byte levelId){
        DistributorLevelRecord into = db().select().from(DISTRIBUTOR_LEVEL).where(DISTRIBUTOR_LEVEL.LEVEL_ID.eq(levelId)).fetchOne().into(DistributorLevelRecord.class);
        return into;
    }

    /**
     * 启用并且自动升级的分销员等级
     * @return
     */
    public List<DistributorLevelRecord> getAutoUpLevel(){
        new DistributorLevelRecord();
        Result<Record> fetch = db().select().from(DISTRIBUTOR_LEVEL)
            .where(DISTRIBUTOR_LEVEL.LEVEL_UP_ROUTE.eq(LEVEL_UP_ROUTE_AUTO))
            .and(DISTRIBUTOR_LEVEL.LEVEL_STATUS.eq(LEVEL_STATUS_ON))
            .orderBy(DISTRIBUTOR_LEVEL.ID.asc()).fetch();
        if(fetch != null){
            return fetch.into(DistributorLevelRecord.class);
        }else{
            return null;
        }
    }

}
