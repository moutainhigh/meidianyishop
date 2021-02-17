package com.meidianyi.shop.dao.shop.goods;

import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.UserCollectionActionRecord;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import static com.meidianyi.shop.db.shop.Tables.*;
/**
 * @author chenjie
 * @date 2020年09月17日
 */
@Repository
public class UserCollectionActionDao extends ShopBaseDao {
    /**
     * 新增药品收藏行为记录
     * @param userId
     * @param goodsId
     * @param collectionType
     */
    public void insertRecord(Integer userId, Integer goodsId, Byte collectionType){
        UserCollectionActionRecord userCollectionRecord = db().newRecord(USER_COLLECTION_ACTION);
        userCollectionRecord.setGoodsId(goodsId);
        userCollectionRecord.setCollectionType(collectionType);
        userCollectionRecord.setUserId(userId);
        userCollectionRecord.insert();
    }
}
