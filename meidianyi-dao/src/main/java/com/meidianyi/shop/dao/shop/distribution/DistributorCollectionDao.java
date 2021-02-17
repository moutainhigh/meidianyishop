package com.meidianyi.shop.dao.shop.distribution;

import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.DistributorCollectionRecord;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.shop.tables.DistributorCollection.DISTRIBUTOR_COLLECTION;

/**
 * @author panjing
 * @data 2020/7/28 16:04
 */
@Repository
public class DistributorCollectionDao extends ShopBaseDao {

    /**
     * 获取分销员收藏的商品id集合
     * @param distributorId 分销员id
     * @return 分销员收藏商品id集合的字符串表示形式，如：1,2,3,4
     */
    public String getCollectionGoodsId(Integer distributorId) {
        return db().select(DISTRIBUTOR_COLLECTION.COLLECTION_GOODS_ID).from(DISTRIBUTOR_COLLECTION)
            .where(DISTRIBUTOR_COLLECTION.DISTRIBUTOR_ID.eq(distributorId)).fetchAny(DISTRIBUTOR_COLLECTION.COLLECTION_GOODS_ID);
    }

    /**
     * 新增分销员收藏商品记录
     */
    public void insertDistributorCollection(DistributorCollectionRecord record) {
        db().executeInsert(record);
    }

    /**
     * 根据分销员id更新分销员收藏商品记录
     * @param distributorId 分销员id
     * @param collectionGoodsId 收藏/取消收藏商品id集合的字符串表现形式
     */
    public void updateDistributorCollection(Integer distributorId, String collectionGoodsId) {
        db().update(DISTRIBUTOR_COLLECTION).set(DISTRIBUTOR_COLLECTION.COLLECTION_GOODS_ID, collectionGoodsId)
          .where(DISTRIBUTOR_COLLECTION.DISTRIBUTOR_ID.eq(distributorId)).execute();
    }
}
