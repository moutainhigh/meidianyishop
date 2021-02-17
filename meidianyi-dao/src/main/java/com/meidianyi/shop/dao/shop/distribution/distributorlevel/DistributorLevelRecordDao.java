package com.meidianyi.shop.dao.shop.distribution.distributorlevel;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel.DistributorLevelRecordParam;
import com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel.DistributorLevelRecordVo;
import org.jooq.Record;
import org.jooq.SelectSeekStep1;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.shop.tables.DistributorLevel.DISTRIBUTOR_LEVEL;
import static com.meidianyi.shop.db.shop.tables.DistributorLevelRecord.DISTRIBUTOR_LEVEL_RECORD;

/**
 * @author changle
 * @date 2020/8/17 8:51 上午
 */
@Repository
public class DistributorLevelRecordDao extends ShopBaseDao {
    /**
     * 分销员等级升降记录
     * @param param
     * @return
     */
    public PageResult<DistributorLevelRecordVo> getDistributorLevelRecordByUserId(DistributorLevelRecordParam param){
        SelectSeekStep1<Record, Integer> records = db().select().from(DISTRIBUTOR_LEVEL_RECORD)
            .where(DISTRIBUTOR_LEVEL_RECORD.USER_ID.eq(param.getUserId()))
            .and(DISTRIBUTOR_LEVEL_RECORD.OLD_LEVEL.ne(DISTRIBUTOR_LEVEL_RECORD.NEW_LEVEL))
            .orderBy(DISTRIBUTOR_LEVEL_RECORD.ID.desc());

        PageResult<DistributorLevelRecordVo> levelRecord = getPageResult(records, param.getCurrentPage(), param.getPageRows(), DistributorLevelRecordVo.class);
        return levelRecord;
    }

    /**
     * 根据分销员等级id得到等级名称
     * @param levelId
     * @return
     */
    public String getLevelNameByLevelId(Byte levelId){
        Record record = db().select(DISTRIBUTOR_LEVEL.LEVEL_NAME).from(DISTRIBUTOR_LEVEL).where(DISTRIBUTOR_LEVEL.LEVEL_ID.eq(levelId)).fetchOne();
        if(record != null){
            return record.into(String.class);
        }else{
            return null;
        }
    }
}
