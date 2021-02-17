package com.meidianyi.shop.dao.shop.img;

import com.meidianyi.shop.common.pojo.shop.table.GoodsImgDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.GoodsImgRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.GOODS_IMG;

/**
 * @author 李晓冰
 * @date 2020年07月06日
 */
@Repository
public class GoodsImgDao extends ShopBaseDao {

    /**
     * 批量插入
     * @param goodsImgDos
     */
    public void batchInsert(List<GoodsImgDo> goodsImgDos) {
        List<GoodsImgRecord> goodsImgRecords = new ArrayList<>(goodsImgDos.size());

        for (GoodsImgDo goodsImgDo : goodsImgDos) {
            GoodsImgRecord record = new GoodsImgRecord();
            record.setGoodsId(goodsImgDo.getGoodsId());
            record.setImgUrl(goodsImgDo.getImgUrl());
            goodsImgRecords.add(record);
        }

        db().batchInsert(goodsImgRecords).execute();
    }

    /**
     * 根据商品id查询商品图片
     * @param goodsId
     * @return
     */
    public List<String> listByGoodsId(Integer goodsId) {
        return db().select(GOODS_IMG.IMG_URL).from(GOODS_IMG).where(GOODS_IMG.GOODS_ID.eq(goodsId)).fetch(GOODS_IMG.IMG_URL);
    }

    /**
     * 根据药品id删除图片
     * @param goodsId 药品id
     */
    public void deleteByGoodsId(Integer goodsId) {
        db().deleteFrom(GOODS_IMG).where(GOODS_IMG.GOODS_ID.eq(goodsId)).execute();
    }
}
