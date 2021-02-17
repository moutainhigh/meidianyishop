package com.meidianyi.shop.dao.shop.goods;

import com.meidianyi.shop.common.pojo.shop.table.SpecValDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.SpecValsRecord;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.SPEC_VALS;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Repository
public class SpecValDao extends ShopBaseDao {

    /**
     * 规格值新增
     * @param specValDos 规格名集合
     * @return 规格名id集合
     */
    public void batchInsert(List<SpecValDo> specValDos) {
        DefaultDSLContext db = db();
        for (SpecValDo specValDo : specValDos) {
            SpecValsRecord record = db.newRecord(SPEC_VALS);
            record.setGoodsId(specValDo.getGoodsId());
            record.setSpecId(specValDo.getSpecId());
            record.setSpecValName(specValDo.getSpecValName());
            record.insert();
            specValDo.setSpecValId(record.getSpecValId());
        }
    }

    public List<SpecValDo> getSepcValsByGoodsId(Integer goodsId) {
        List<SpecValDo> specDos = db().selectFrom(SPEC_VALS).where(SPEC_VALS.GOODS_ID.in(goodsId))
            .fetchInto(SpecValDo.class);
        return specDos;
    }

    /**
     * 根据商品id删除
     * @param goodsId
     */
    public void deleteByGoodsId(Integer goodsId) {
        db().deleteFrom(SPEC_VALS).where(SPEC_VALS.GOODS_ID.eq(goodsId)).execute();
    }
}
