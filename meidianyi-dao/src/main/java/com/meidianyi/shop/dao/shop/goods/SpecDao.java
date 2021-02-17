package com.meidianyi.shop.dao.shop.goods;

import com.meidianyi.shop.common.pojo.shop.table.SpecDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.SpecRecord;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.SPEC;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Repository
public class SpecDao extends ShopBaseDao {

    /**
     * 规格名新增
     * @param specDos 规格名集合
     * @return 规格名id集合
     */
    public void batchInsert(List<SpecDo> specDos) {
        DefaultDSLContext db = db();
        for (SpecDo specDo : specDos) {
            SpecRecord record = db.newRecord(SPEC);
            record.setGoodsId(specDo.getGoodsId());
            record.setSpecName(specDo.getSpecName());
            record.insert();
            specDo.setSpecId(record.getSpecId());
        }
    }

    /**
     * 根据商品id删除规格组信息
     * @param goodsId
     */
    public void deleteByGoodsId(Integer goodsId) {
        db().deleteFrom(SPEC).where(SPEC.GOODS_ID.eq(goodsId)).execute();
    }

    public List<SpecDo> getSpecsByGoodsId(Integer goodsId) {
        List<SpecDo> specDos = db().selectFrom(SPEC).where(SPEC.GOODS_ID.eq(goodsId))
            .fetchInto(SpecDo.class);
        return specDos;
    }
}
