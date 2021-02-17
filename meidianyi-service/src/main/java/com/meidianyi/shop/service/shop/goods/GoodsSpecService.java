package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.db.shop.tables.records.SpecRecord;
import com.meidianyi.shop.db.shop.tables.records.SpecValsRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpec;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecVal;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.meidianyi.shop.db.shop.Tables.SPEC;
import static com.meidianyi.shop.db.shop.Tables.SPEC_VALS;

/**
 * @author 李晓冰
 * @date 2019年07月05日
 */
@Service

public class GoodsSpecService extends ShopBaseService {

    /**
     * 	规格名值处理后map内id值的key名称
     */
    static final String PRD_SPEC_ID_KEY = "specId";

    /**
     * 此服务目前只由goodsService使用，所以没有被ServiceContainer管理
     * @param goodsSpecs 内部包含了对应的specsVal值
     * @param goodsId 商品id
     */
    protected void insertSpecAndSpecVal(List<GoodsSpec> goodsSpecs, Integer goodsId) {
        DSLContext db=db();
        for (GoodsSpec goodsSpec : goodsSpecs) {
            goodsSpec.setGoodsId(goodsId);
            SpecRecord specRecord = db.newRecord(SPEC, goodsSpec);
            specRecord.insert();

            goodsSpec.setSpecId(specRecord.getSpecId());

            for (GoodsSpecVal val : goodsSpec.getGoodsSpecVals()) {
                val.setGoodsId(goodsId);
                val.setSpecId(specRecord.getSpecId());
                SpecValsRecord specValsRecord = db.newRecord(SPEC_VALS, val);
                specValsRecord.insert();
                val.setSpecValId(specValsRecord.getSpecValId());
            }
        }
    }

    /**
     * 在插入数据的基础上返回一个预处理的Map对象，供使用者快速通过规格名称字符串获取对应记录的id值。
     * @param goodsSpecs
     * @param goodsId
     * @return {'颜色':{'specId':1,'红色':11,'绿色':22},'尺寸':{'specId':2,'X':15,'M':28}}
     */
    public Map<String, Map<String, Integer>> insertSpecAndSpecValWithPrepareResult(List<GoodsSpec> goodsSpecs, Integer goodsId) {
        insertSpecAndSpecVal(goodsSpecs, goodsId);
        Map<String, Map<String, Integer>> resultMap = prepareResultMap(goodsSpecs);
        return resultMap;
    }

    /**
     * 预处理规格名和规格值，便于通过规格名称或规格值来获得对应记录在数据库中的id值。
     * @param goodsSpecs 已入库的规格值和名
     * @return {'颜色':{'specId':1,'红色':11,'绿色':22},'尺寸':{'specId':2,'X':15,'M':28}}
     */
    private Map<String, Map<String, Integer>> prepareResultMap(List<GoodsSpec> goodsSpecs) {
        // 产生 规格属性预处理数据
        Map<String, Map<String, Integer>> goodsSpecsMap = new HashMap<>(goodsSpecs.size());
        for (GoodsSpec goodsSpec : goodsSpecs) {

            Map<String, Integer> goodsSpecsValMap = new HashMap<>(goodsSpec.getGoodsSpecVals().size()+1);
            goodsSpecsValMap.put(PRD_SPEC_ID_KEY, goodsSpec.getSpecId());

            for (GoodsSpecVal goodsSpecVal : goodsSpec.getGoodsSpecVals()) {
                goodsSpecsValMap.put(goodsSpecVal.getSpecValName(), goodsSpecVal.getSpecValId());
            }

            goodsSpecsMap.put(goodsSpec.getSpecName(), goodsSpecsValMap);
        }

        return goodsSpecsMap;
    }

    /**
     * 根据商品Ids删除
     * @param db       对外事务入口
     * @param goodsIds
     */
    public void deleteByGoodsIds(DSLContext db, List<Integer> goodsIds) {
        db.update(SPEC).set(SPEC.DEL_FLAG, DelFlag.DISABLE.getCode())
                .set(SPEC.SPEC_NAME, DSL.concat(DelFlag.DEL_ITEM_PREFIX)
                        .concat(SPEC.SPEC_ID)
                        .concat(DelFlag.DEL_ITEM_SPLITER)
                        .concat(SPEC.SPEC_NAME))
                .where(SPEC.GOODS_ID.in(goodsIds)).and(SPEC.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .execute();

        db.update(SPEC_VALS).set(SPEC_VALS.DEL_FLAG,DelFlag.DISABLE.getCode())
                .set(SPEC_VALS.SPEC_VAL_NAME,DSL.concat(DelFlag.DEL_ITEM_PREFIX)
                        .concat(SPEC_VALS.SPEC_ID)
                        .concat(DelFlag.DEL_ITEM_SPLITER)
                        .concat(SPEC_VALS.SPEC_VAL_NAME))
                .where(SPEC_VALS.GOODS_ID.in(goodsIds)).and(SPEC_VALS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .execute();
    }

    /**
     * 根据规格值名的id和商品id删除数据（属于商品id，但是不在规格值和名id内）
     * @param goodsSpecs 规格值集合，该集合内包含存在id值的旧数据和不存在id值的新数据
     * @param goodsId 商品id
     */
    public void deleteForGoodsUpdate(List<GoodsSpec> goodsSpecs, Integer goodsId) {
        // 没有对应的规格值和名则将该商品下的所有相关内容全部删除
        if (goodsSpecs == null || goodsSpecs.size() == 0) {
            deleteByGoodsIds(db(), Arrays.asList(goodsId));
        } else {
           List<Integer> specNameIds=new ArrayList<>(goodsSpecs.size());
           List<Integer> specValIds=new ArrayList<>(goodsSpecs.size());

           goodsSpecs.forEach(goodsSpec -> {
                //规格名为空，则其下的所有规格值也都为空
               if (goodsSpec.getSpecId() == null) {
                   return;
               }
               specNameIds.add(goodsSpec.getSpecId());
               List<GoodsSpecVal> goodsSpecVals = goodsSpec.getGoodsSpecVals();
               goodsSpecVals.forEach(goodsSpecVal ->{
                   // 可能存在无id值的新规格值项
                   if (goodsSpecVal.getSpecValId() != null) {
                       specValIds.add(goodsSpecVal.getSpecValId());
                   }
               });
           });

            db().update(SPEC).set(SPEC.DEL_FLAG, DelFlag.DISABLE.getCode())
                .set(SPEC.SPEC_NAME, DSL.concat(DelFlag.DEL_ITEM_PREFIX)
                    .concat(SPEC.SPEC_ID)
                    .concat(DelFlag.DEL_ITEM_SPLITER)
                    .concat(SPEC.SPEC_NAME))
                .where(SPEC.GOODS_ID.eq(goodsId)).and(SPEC.SPEC_ID.notIn(specNameIds)).and(SPEC.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .execute();

            db().update(SPEC_VALS).set(SPEC_VALS.DEL_FLAG,DelFlag.DISABLE.getCode())
                .set(SPEC_VALS.SPEC_VAL_NAME,DSL.concat(DelFlag.DEL_ITEM_PREFIX)
                    .concat(SPEC_VALS.SPEC_ID)
                    .concat(DelFlag.DEL_ITEM_SPLITER)
                    .concat(SPEC_VALS.SPEC_VAL_NAME))
                .where(SPEC_VALS.GOODS_ID.eq(goodsId)).and(SPEC_VALS.SPEC_VAL_ID.notIn(specValIds)).and(SPEC_VALS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .execute();
        }
    }

    /**
     * 根据规格值名id和进行修改数据
     * @param goodsSpecs 规格值集合，该集合内包含存在id值的旧数据和不存在id值的新数据
     */
    public void updateForGoodsUpdate(List<GoodsSpec> goodsSpecs){
        // 使用了默认规格
        if (goodsSpecs == null || goodsSpecs.size() == 0) {
            return;
        }
        DSLContext db=db();
        List<SpecRecord> specRecords=new ArrayList<>(goodsSpecs.size());
        List<SpecValsRecord> specValsRecords=new ArrayList<>(goodsSpecs.size());
        goodsSpecs.forEach(goodsSpec -> {
            if (goodsSpec.getSpecId() == null) {
                return;
            }
            SpecRecord specRecord = db.newRecord(SPEC);
            specRecord.setSpecId(goodsSpec.getSpecId());
            specRecord.setSpecName(goodsSpec.getSpecName());
            specRecords.add(specRecord);
            List<GoodsSpecVal> goodsSpecVals = goodsSpec.getGoodsSpecVals();

            goodsSpecVals.forEach(goodsSpecVal -> {
                if (goodsSpecVal.getSpecValId() == null) {
                    return;
                }
                SpecValsRecord specValsRecord = db.newRecord(SPEC_VALS);
                specValsRecord.setSpecValId(goodsSpecVal.getSpecValId());
                specValsRecord.setSpecValName(goodsSpecVal.getSpecValName());
                specValsRecords.add(specValsRecord);
            });
        });
        db.batchUpdate(specRecords).execute();
        db.batchUpdate(specValsRecords).execute();
    }

    /**
     * 插入规格名值数据，并返回包含经过处理的规格名值id的Map对象，
     * 但是输入的规格名可能已存在对应的id,这时不应该插入该名称
     * @param goodsSpecs 规格对象
     * @param goodsId 商品id
     * @return {'颜色':{'specId':1,'红色':11,'绿色':22},'尺寸':{'specId':2,'X':15,'M':28}}
     */
    public Map<String,Map<String,Integer>> insertSpecAndSpecValWithPrepareResultForGoodsUpdate(List<GoodsSpec> goodsSpecs,Integer goodsId){
        DSLContext db=db();
        for (GoodsSpec goodsSpec : goodsSpecs) {
            goodsSpec.setGoodsId(goodsId);

            // 规格名id存在则不用再插入
            if (goodsSpec.getSpecId() == null) {
                SpecRecord specRecord = db.newRecord(SPEC, goodsSpec);
                specRecord.insert();
                goodsSpec.setSpecId(specRecord.getSpecId());
            }

            for (GoodsSpecVal val : goodsSpec.getGoodsSpecVals()) {
                val.setGoodsId(goodsId);
                val.setSpecId(goodsSpec.getSpecId());
                if (val.getSpecValId() != null) {
                    continue;
                }
                SpecValsRecord specValsRecord = db.newRecord(SPEC_VALS, val);
                specValsRecord.insert();
                val.setSpecValId(specValsRecord.getSpecValId());
            }
        }
        Map<String, Map<String, Integer>> resultMap = prepareResultMap(goodsSpecs);
        return resultMap;
    }

    /**
     *  根据商品Id查找规格
     * @param db
     * @param goodsId
     * @return
     */
    public List<GoodsSpec> selectByGoodsId(DSLContext db,Integer goodsId) {
        List<GoodsSpec> goodsSpecs = db.selectFrom(SPEC)
                .where(SPEC.GOODS_ID.eq(goodsId)).and(SPEC.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
                .fetch().into(GoodsSpec.class);

        Map<Integer, List<GoodsSpecVal>> specIdValMap = db.selectFrom(SPEC_VALS)
                .where(SPEC_VALS.GOODS_ID.eq(goodsId)).and(SPEC_VALS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
                .fetch().intoGroups(SPEC_VALS.SPEC_ID, GoodsSpecVal.class);

        for (GoodsSpec goodsSpec : goodsSpecs) {
            goodsSpec.setGoodsSpecVals(specIdValMap.get(goodsSpec.getSpecId()));
        }

        return goodsSpecs;
    }
}
