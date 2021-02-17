package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductBakRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.db.shop.tables.records.StoreGoodsRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListVo;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpec;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecProduct;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecVal;
import com.meidianyi.shop.service.pojo.shop.goods.spec.ProductSmallInfoVo;
import com.meidianyi.shop.service.pojo.shop.recommend.SkuAttrList;
import com.meidianyi.shop.service.pojo.shop.store.goods.StoreGoodsListQueryVo;
import com.meidianyi.shop.service.shop.store.store.StoreGoodsService;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.GOODS_BRAND;
import static com.meidianyi.shop.db.shop.Tables.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.Tables.GOODS_SPEC_PRODUCT_BAK;
import static com.meidianyi.shop.db.shop.Tables.SORT;
import static com.meidianyi.shop.db.shop.Tables.STORE_GOODS;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;

/**
 * @author 李晓冰
 * @date 2019年07月05日
 */
@Service
public class GoodsSpecProductService extends ShopBaseService {

    @Autowired
    private GoodsSpecService goodsSpecService;
    /**
     * 规格名值描述分割符
     */
    public static final String PRD_DESC_DELIMITER = ";";
    /**
     * 规格名值id分隔符
     */
    public static final String PRD_SPEC_DELIMITER = "!!";
    /**
     * 规格名和值分隔符
     */
    public static final String PRD_VAL_DELIMITER = ":";

    /**
     * 规格名值处理后map内id值的key名称
     */
    public static final String PRD_SPEC_ID_KEY = GoodsSpecService.PRD_SPEC_ID_KEY;


    /**
     * 插入商品sku之前预处理器prdSpecs字段，目前用于用户填写了自己的sku
     *
     * @param goodsSpecProducts 规格属性
     * @param goodsSpecs        规格名值
     * @param goodsId           商品id
     */
    public void insert(List<GoodsSpecProduct> goodsSpecProducts, List<GoodsSpec> goodsSpecs, Integer goodsId) {

        if (goodsSpecs == null || goodsSpecs.size() == 0) {
            GoodsSpecProduct goodsSpecProduct = goodsSpecProducts.get(0);
            goodsSpecProduct.setGoodsId(goodsId);
            insert(goodsSpecProduct);
        } else {
            // 先插入规格名和规格值信息
            Map<String, Map<String, Integer>> goodsSpecsMap = goodsSpecService
                .insertSpecAndSpecValWithPrepareResult(goodsSpecs, goodsId);

            insertAndSetPrdSpec(goodsSpecProducts, goodsSpecsMap, goodsId);
        }
    }

    /**
     * 根据 =规格id获取规格信息
     *
     * @param prdIds
     * @return
     */
    public Map<Integer, GoodsSpecProductRecord> goodsSpecProductByIds(List<Integer> prdIds) {
        return db().selectFrom(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_ID.in(prdIds)).fetchMap(GOODS_SPEC_PRODUCT.PRD_ID);
    }

    /**
     * 根据处理后的商品规格名值数据插入规格项（sku）,在插入前动态计算其prdSpec
     *
     * @param goodsSpecProducts sku
     * @param goodsSpecsMap     {'颜色':{'specId':1,'红色':11,'绿色':22},'尺寸':{'specId':2,'X':15,'M':28}}
     * @param goodsId           商品id
     */
    private void insertAndSetPrdSpec(List<GoodsSpecProduct> goodsSpecProducts, Map<String, Map<String, Integer>> goodsSpecsMap, Integer goodsId) {
        for (GoodsSpecProduct goodsSpecProduct : goodsSpecProducts) {
            goodsSpecProduct.setGoodsId(goodsId);
            String prdDescs = goodsSpecProduct.getPrdDesc();

            StringBuilder sb = new StringBuilder();
            //specDesc参数格式为：颜色:绿色;尺寸:X
            for (String prdDesc : prdDescs.split(PRD_DESC_DELIMITER)) {
                String[] s = prdDesc.split(PRD_VAL_DELIMITER);


                String spec = s[0], specVal = s[1];
                if (sb.length() != 0) {
                    sb.append(PRD_SPEC_DELIMITER);
                }
                Map<String, Integer> nameIdMap = goodsSpecsMap.get(spec);
                sb.append(nameIdMap.get(PRD_SPEC_ID_KEY)).append(PRD_VAL_DELIMITER).append(nameIdMap.get(specVal));
            }
            //格式：1:2!!1:3
            goodsSpecProduct.setPrdSpecs(sb.toString());

            insert(goodsSpecProduct);
        }
    }

    /**
     * 插入商品sku数据
     *
     * @param goodsSpecProduct 商品规格属性
     */
    private void insert(GoodsSpecProduct goodsSpecProduct) {
        GoodsSpecProductRecord goodsSpecProductRecord = db().newRecord(GOODS_SPEC_PRODUCT, goodsSpecProduct);
        goodsSpecProductRecord.insert();
        goodsSpecProduct.setPrdId(goodsSpecProductRecord.getPrdId());
    }

    /**
     * 根据商品ids删除规格值
     *
     * @param goodsIds
     * @param goodsIds
     */
    public void deleteByGoodsIds(List<Integer> goodsIds) {
        DSLContext db = db();
        //将sku表内数据备份至sku_bak内
        db.insertInto(GOODS_SPEC_PRODUCT_BAK, GOODS_SPEC_PRODUCT_BAK.PRD_ID, GOODS_SPEC_PRODUCT_BAK.SHOP_ID, GOODS_SPEC_PRODUCT_BAK.GOODS_ID
            , GOODS_SPEC_PRODUCT_BAK.PRD_PRICE, GOODS_SPEC_PRODUCT_BAK.PRD_MARKET_PRICE, GOODS_SPEC_PRODUCT_BAK.PRD_COST_PRICE, GOODS_SPEC_PRODUCT_BAK.PRD_NUMBER
            , GOODS_SPEC_PRODUCT_BAK.PRD_SN, GOODS_SPEC_PRODUCT_BAK.PRD_CODES, GOODS_SPEC_PRODUCT_BAK.PRD_SPECS, GOODS_SPEC_PRODUCT_BAK.PRD_DESC
            , GOODS_SPEC_PRODUCT_BAK.SELF_FLAG, GOODS_SPEC_PRODUCT_BAK.LOW_SHOP_PRICE, GOODS_SPEC_PRODUCT_BAK.PRD_IMG
            , GOODS_SPEC_PRODUCT_BAK.PRICE_FLAG, GOODS_SPEC_PRODUCT_BAK.CREATE_TIME, GOODS_SPEC_PRODUCT_BAK.UPDATE_TIME)
            .select(db.select(GOODS_SPEC_PRODUCT.PRD_ID, GOODS_SPEC_PRODUCT.SHOP_ID, GOODS_SPEC_PRODUCT.GOODS_ID
                , GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.PRD_MARKET_PRICE, GOODS_SPEC_PRODUCT.PRD_COST_PRICE, GOODS_SPEC_PRODUCT.PRD_NUMBER
                , GOODS_SPEC_PRODUCT.PRD_SN, GOODS_SPEC_PRODUCT.PRD_CODES, GOODS_SPEC_PRODUCT.PRD_SPECS, GOODS_SPEC_PRODUCT.PRD_DESC
                , GOODS_SPEC_PRODUCT.SELF_FLAG, GOODS_SPEC_PRODUCT.LOW_SHOP_PRICE, GOODS_SPEC_PRODUCT.PRD_IMG
                , GOODS_SPEC_PRODUCT.PRICE_FLAG, GOODS_SPEC_PRODUCT.CREATE_TIME, GOODS_SPEC_PRODUCT.UPDATE_TIME).from(GOODS_SPEC_PRODUCT)
                .where(GOODS_SPEC_PRODUCT.GOODS_ID.in(goodsIds)))
            .execute();

        //真删除sku内数据
        db.deleteFrom(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.in(goodsIds)).execute();

        //假删除规格名和值内数据
        goodsSpecService.deleteByGoodsIds(db, goodsIds);
    }

    /**
     * 根据商品id查找对应sku
     *
     * @param goodsId
     * @return
     */
    public List<GoodsSpecProduct> selectByGoodsId(Integer goodsId) {
        List<GoodsSpecProduct> goodsSpecProducts = db().selectFrom(GOODS_SPEC_PRODUCT)
            .where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId)).fetch().into(GoodsSpecProduct.class);

        return goodsSpecProducts;
    }

    /**
     * 根据商品GoodsSn查询对应的sku
     * @param goodsSn
     * @return
     */
    public List<GoodsSpecProduct> selectByGoodsSn(String goodsSn) {
        return db().select(GOODS_SPEC_PRODUCT.PRD_ID, GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.PRD_MARKET_PRICE, GOODS_SPEC_PRODUCT.PRD_COST_PRICE, GOODS_SPEC_PRODUCT.PRD_NUMBER,
            GOODS_SPEC_PRODUCT.PRD_SN, GOODS_SPEC_PRODUCT.PRD_SPECS, GOODS_SPEC_PRODUCT.PRD_DESC)
            .from(GOODS_SPEC_PRODUCT.innerJoin(GOODS).on(GOODS_SPEC_PRODUCT.GOODS_ID.eq(GOODS.GOODS_ID)))
            .where(GOODS.GOODS_SN.eq(goodsSn).and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)))
            .fetchInto(GoodsSpecProduct.class);
    }

    /**
     * 根据商品id查找对应sku
     *
     * @param goodsIds
     * @return
     */
    public Map<Integer, Result<GoodsSpecProductRecord>> selectByGoodsIds(List<Integer> goodsIds) {
        return db().selectFrom(GOODS_SPEC_PRODUCT)
            .where(GOODS_SPEC_PRODUCT.GOODS_ID.in(goodsIds))
            .fetchGroups(GOODS_SPEC_PRODUCT.GOODS_ID);
    }

    /**
     * 根据商品id查询对应的规格属性名值对象
     *
     * @param goodsId 商品id
     * @return 规格属性名值对象
     */
    public List<GoodsSpec> selectSpecByGoodsId(Integer goodsId) {
        List<GoodsSpec> goodsSpecs = goodsSpecService.selectByGoodsId(db(), goodsId);
        return goodsSpecs;
    }

    /**
     * 根据prdId修改商品规格信息并删除无用的（数据库中存在但是出入的对象集合中不存在）
     *
     * @param goodsSpecProducts 规格对象集合
     */
    public void updateAndDeleteForGoodsUpdate(List<GoodsSpecProduct> goodsSpecProducts, List<GoodsSpec> goodsSpecs, Integer goodsId) {

        List<Integer> goodsSpecProductIds = goodsSpecProducts.stream().map(GoodsSpecProduct::getPrdId).collect(Collectors.toList());

        // 删除无效sku
        deleteForGoodsUpdate(goodsSpecProductIds, goodsId);
        //假删除规格名和值内数据
        goodsSpecService.deleteForGoodsUpdate(goodsSpecs, goodsId);

        batchUpdateGoodsSku(goodsSpecProducts);

        goodsSpecService.updateForGoodsUpdate(goodsSpecs);
    }


    private void batchUpdateGoodsSku(List<GoodsSpecProduct> goodsSpecProducts) {
        if (goodsSpecProducts.size() == 0) {
            return;
        }
        GoodsSpecProduct item = goodsSpecProducts.get(0);
        DSLContext db = db();
        String sql = db.update(GOODS_SPEC_PRODUCT)
            .set(GOODS_SPEC_PRODUCT.PRD_PRICE, item.getPrdPrice())
            .set(GOODS_SPEC_PRODUCT.PRD_MARKET_PRICE, item.getPrdMarketPrice())
            .set(GOODS_SPEC_PRODUCT.PRD_COST_PRICE, item.getPrdCostPrice())
            .set(GOODS_SPEC_PRODUCT.PRD_NUMBER, item.getPrdNumber())
            .set(GOODS_SPEC_PRODUCT.PRD_SN, item.getPrdSn())
            .set(GOODS_SPEC_PRODUCT.PRD_CODES, item.getPrdCodes())
            .set(GOODS_SPEC_PRODUCT.PRD_SPECS, item.getPrdSpecs())
            .set(GOODS_SPEC_PRODUCT.PRD_DESC, item.getPrdDesc())
            .set(GOODS_SPEC_PRODUCT.PRD_IMG, item.getPrdImg())
            .set(GOODS_SPEC_PRODUCT.PRD_WEIGHT, item.getPrdWeight())
            .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(item.getPrdId()))
            .getSQL();
        Query query = db.query(sql);
        BatchBindStep batchStep = db.batch(query);
        int addedCount = 0;
        final int batchCount = 500;

        for (int i = 0; i < goodsSpecProducts.size(); i++) {
            item = goodsSpecProducts.get(i);
            addedCount++;
            batchStep = batchStep.bind(item.getPrdPrice(), item.getPrdMarketPrice(), item.getPrdCostPrice(), item.getPrdNumber(), item.getPrdSn(),
                item.getPrdCodes(), item.getPrdSpecs(), item.getPrdDesc(), item.getPrdImg(),item.getPrdWeight(), item.getPrdId());

            if (addedCount == batchCount) {
                batchStep.execute();
                batchStep = db.batch(query);
                addedCount = 0;
            }
        }
        batchStep.execute();
    }

    /**
     * 删除属于商品id但是不在prdIds集合内的所有项
     *
     * @param prdIds  规格项id
     * @param goodsId 商品id
     */
    private void deleteForGoodsUpdate(List<Integer> prdIds, Integer goodsId) {
        DSLContext db = db();
        //将sku表内数据备份至sku_bak内
        db.insertInto(GOODS_SPEC_PRODUCT_BAK, GOODS_SPEC_PRODUCT_BAK.PRD_ID, GOODS_SPEC_PRODUCT_BAK.SHOP_ID, GOODS_SPEC_PRODUCT_BAK.GOODS_ID
            , GOODS_SPEC_PRODUCT_BAK.PRD_PRICE, GOODS_SPEC_PRODUCT_BAK.PRD_MARKET_PRICE, GOODS_SPEC_PRODUCT_BAK.PRD_COST_PRICE, GOODS_SPEC_PRODUCT_BAK.PRD_NUMBER
            , GOODS_SPEC_PRODUCT_BAK.PRD_SN, GOODS_SPEC_PRODUCT_BAK.PRD_CODES, GOODS_SPEC_PRODUCT_BAK.PRD_SPECS, GOODS_SPEC_PRODUCT_BAK.PRD_DESC
            , GOODS_SPEC_PRODUCT_BAK.SELF_FLAG, GOODS_SPEC_PRODUCT_BAK.LOW_SHOP_PRICE, GOODS_SPEC_PRODUCT_BAK.PRD_IMG
            , GOODS_SPEC_PRODUCT_BAK.PRICE_FLAG, GOODS_SPEC_PRODUCT_BAK.CREATE_TIME, GOODS_SPEC_PRODUCT_BAK.UPDATE_TIME, GOODS_SPEC_PRODUCT_BAK.PRD_WEIGHT)
            .select(db.select(GOODS_SPEC_PRODUCT.PRD_ID, GOODS_SPEC_PRODUCT.SHOP_ID, GOODS_SPEC_PRODUCT.GOODS_ID
                , GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.PRD_MARKET_PRICE, GOODS_SPEC_PRODUCT.PRD_COST_PRICE, GOODS_SPEC_PRODUCT.PRD_NUMBER
                , GOODS_SPEC_PRODUCT.PRD_SN, GOODS_SPEC_PRODUCT.PRD_CODES, GOODS_SPEC_PRODUCT.PRD_SPECS, GOODS_SPEC_PRODUCT.PRD_DESC
                , GOODS_SPEC_PRODUCT.SELF_FLAG, GOODS_SPEC_PRODUCT.LOW_SHOP_PRICE, GOODS_SPEC_PRODUCT.PRD_IMG
                , GOODS_SPEC_PRODUCT.PRICE_FLAG, GOODS_SPEC_PRODUCT.CREATE_TIME, GOODS_SPEC_PRODUCT.UPDATE_TIME, GOODS_SPEC_PRODUCT.PRD_WEIGHT).from(GOODS_SPEC_PRODUCT)
                .where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId)).and(GOODS_SPEC_PRODUCT.PRD_ID.notIn(prdIds)))
            .execute();

        //真删除sku内数据
        db.deleteFrom(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId))
            .and(GOODS_SPEC_PRODUCT.PRD_ID.notIn(prdIds)).execute();
    }

    /**
     * 插入商品sku之前预处理器prdSpecs字段，目前用于用户填写了自己的sku
     *
     * @param goodsSpecProducts 规格属性
     * @param goodsSpecs        规格名值
     * @param goodsId           商品id
     */
    public void insertForUpdate(List<GoodsSpecProduct> goodsSpecProducts, List<GoodsSpec> goodsSpecs, Integer goodsId) {

        // 使用了默认规格
        if (goodsSpecs == null || goodsSpecs.size() == 0) {

            //修改前也是使用的默认规格，修改后还是默认规格，则不需要插入默认规格
            if (goodsSpecProducts.size() == 0) {
                return;
            }

            GoodsSpecProduct goodsSpecProduct = goodsSpecProducts.get(0);

            //修改之前为自定义规格，修改后改为了默认规格
            goodsSpecProduct.setGoodsId(goodsId);
            insert(goodsSpecProduct);
        } else {
            //修改前为可以为自定义规格和默认规格量情况，后者较为简单可以按照全部新增处理
            //对于前者可能存在删除修改新增了规格项，则在插入规格项和值的时候进行特殊处理
            //比如规格名没变但是新增了一个规格值，这时候规格名不需要新增，但是规格值要新增
            //而产生的新的sku项的prdSpec则需要根据已有的规格名id和新产生的规格值id进行组装

            // 先插入规格名和规格值信息
            Map<String, Map<String, Integer>> goodsSpecsMap = goodsSpecService
                .insertSpecAndSpecValWithPrepareResultForGoodsUpdate(goodsSpecs, goodsId);
            //插入规格项
            insertAndSetPrdSpec(goodsSpecProducts, goodsSpecsMap, goodsId);
        }
    }

    /**
     * 商品excel导入-更新-更新对应规格信息
     * @param goodsSpecProducts
     */
    public void updateSpecPrdForGoodsImport(List<GoodsSpecProduct> goodsSpecProducts) {
        List<GoodsSpecProductRecord> records = new ArrayList<>();
        for (GoodsSpecProduct goodsSpecProduct : goodsSpecProducts) {
            GoodsSpecProductRecord record = new GoodsSpecProductRecord();
            record.setPrdId(goodsSpecProduct.getPrdId());
            record.setPrdPrice(goodsSpecProduct.getPrdPrice());
            record.setPrdMarketPrice(goodsSpecProduct.getPrdMarketPrice());
            record.setPrdCodes(goodsSpecProduct.getPrdCodes());
            record.setPrdWeight(goodsSpecProduct.getPrdWeight());
            records.add(record);
        }
        db().batchUpdate(records).execute();
    }
    /**
     * 根据商品id集合查询出商品id和规格项的对应分组映射
     *
     * @param goodsIds 商品id集合
     * @return map key: 商品id value:规格项集合
     */
    public Map<Integer, List<GoodsSpecProduct>> selectGoodsSpecPrdGroup(List<Integer> goodsIds) {
        return db().selectFrom(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.in(goodsIds))
            .fetch().intoGroups(GOODS_SPEC_PRODUCT.GOODS_ID, GoodsSpecProduct.class);
    }

    /**
     * 根据商品id查询出所有规格id和规格的详细信息map映射
     *
     * @param goodsId 商品id
     * @return map key:规格id，value:规格详情
     */
    public Map<Integer, GoodsSpecProductRecord> selectSpecPrdIdMap(Integer goodsId) {
        return db().selectFrom(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId)).fetchMap(GOODS_SPEC_PRODUCT.PRD_ID);
    }

    /**
     * 根据规格id查询规格明细
     *
     * @return
     */
    public Map<Integer, GoodsSpecProductRecord> selectSpecByProIds(List<Integer> proIds) {
        return db().selectFrom(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_ID.in(proIds)).fetchMap(GOODS_SPEC_PRODUCT.PRD_ID);
    }

    /**
     * 根据规格id查询规格明细 一跳规格
     *
     * @return
     */
    public GoodsSpecProductRecord selectSpecByProId(Integer proId) {
        return db().selectFrom(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_ID.eq(proId)).fetchAny();
    }

    /**
     * 下单查询规格
     *
     * @param proIds
     * @param storeId
     * @return
     */
    public Map<Integer, GoodsSpecProductRecord> selectSpecByProIds(List<Integer> proIds, Integer storeId) {
        //商品规格信息
        Map<Integer, GoodsSpecProductRecord> products = selectSpecByProIds(proIds);
        if (storeId == null || storeId.equals(NumberUtils.INTEGER_ZERO)) {
            return products;
        }
        //门店商品规格信息
        Map<Integer, StoreGoodsListQueryVo> storeProducts = db().
            select(STORE_GOODS.PRODUCT_PRICE, STORE_GOODS.PRODUCT_NUMBER, STORE_GOODS.PRD_ID).
            from(STORE_GOODS).
            where(STORE_GOODS.IS_ON_SALE.eq(StoreGoodsService.ON_SALE))
                .and(STORE_GOODS.IS_SYNC.eq((byte) 1))
                .and(STORE_GOODS.STORE_ID.eq(storeId))
                .and(STORE_GOODS.PRD_ID.in(proIds))
                .fetchMap(STORE_GOODS.PRD_ID, StoreGoodsListQueryVo.class);
        for (GoodsSpecProductRecord product : products.values()) {
            StoreGoodsListQueryVo storeProduct = storeProducts.get(product.getPrdId());
            if (storeProduct != null) {
                //同步之后的规格价格、库存
                product.setPrdPrice(storeProduct.getProductPrice());
                product.setPrdNumber(storeProduct.getProductNumber());
            }
        }
        return products;
    }

    /**
     * 根据id
     *
     * @param goodsId 商品id
     * @return 规则 record
     */
    public Result<Record> getAllProductListByGoodsId(Integer goodsId) {
        Result<Record> recordResult = db().select()
            .from(GOODS_SPEC_PRODUCT)
            .where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId))
            .fetch();
        return recordResult;
    }

    /**
     * 获取门店商品信息
     *
     * @param productIds
     * @param storeId
     * @return
     */
    public Result<? extends Record> getStoreProductAll(List<Integer> productIds, Integer storeId) {
        Result<Record4<BigDecimal, Integer, Integer, Integer>> products = db().select(GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.GOODS_ID, GOODS_SPEC_PRODUCT.PRD_NUMBER, GOODS_SPEC_PRODUCT.PRD_ID)
            .from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_ID.in(productIds)).fetch();
        if (storeId != null && storeId > 0) {
            Result<Record3<BigDecimal, Integer, Integer>> storeGoods = db().select(STORE_GOODS.PRODUCT_PRICE, STORE_GOODS.PRODUCT_NUMBER, STORE_GOODS.PRD_ID).from(STORE_GOODS)
                .where(STORE_GOODS.IS_ON_SALE.eq((byte) 1)).and(STORE_GOODS.IS_SYNC.eq((byte) 1))
                .and(STORE_GOODS.STORE_ID.eq(storeId)).and(STORE_GOODS.PRD_ID.in(productIds)).fetch();
            Map<Integer, BigDecimal> storeGoodsPrices = storeGoods.intoMap(STORE_GOODS.PRD_ID, STORE_GOODS.PRODUCT_PRICE);
            Map<Integer, Integer> storeGoodsNumbers = storeGoods.intoMap(STORE_GOODS.PRD_ID, STORE_GOODS.PRODUCT_NUMBER);
            products.stream().forEach(pro -> {
                if (storeGoods.getValues(STORE_GOODS.PRD_ID).contains(pro.get(GOODS_SPEC_PRODUCT.PRD_ID))) {
                    pro.set(GOODS_SPEC_PRODUCT.PRD_PRICE, storeGoodsPrices.get(STORE_GOODS.PRD_ID));
                    pro.set(GOODS_SPEC_PRODUCT.PRD_NUMBER, storeGoodsNumbers.get(STORE_GOODS.PRD_ID));
                }
            });
        }
        return products;
    }

    /**
     * 根据规格id集合获取对应的规格信息
     *
     * @param prdIds
     * @return GoodsPageListVo
     */
    public List<GoodsPageListVo> getProductsByProductIds(List<Integer> prdIds) {
        List<GoodsPageListVo> goodsPageListVos = db().select(GOODS_SPEC_PRODUCT.GOODS_ID, GOODS_SPEC_PRODUCT.PRD_ID, GOODS_SPEC_PRODUCT.PRD_SN,
            GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.PRD_NUMBER, GOODS_SPEC_PRODUCT.PRD_DESC, GOODS_SPEC_PRODUCT.PRD_IMG,
            GOODS.GOODS_NAME, GOODS.GOODS_IMG, SORT.SORT_NAME, GOODS_BRAND.BRAND_NAME)
            .from(GOODS_SPEC_PRODUCT).innerJoin(GOODS).on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID))
            .leftJoin(SORT).on(GOODS.SORT_ID.eq(SORT.SORT_ID)).leftJoin(GOODS_BRAND).on(GOODS.BRAND_ID.eq(GOODS_BRAND.ID))
            .where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .and(GOODS_SPEC_PRODUCT.PRD_ID.in(prdIds))
            .fetchInto(GoodsPageListVo.class);
        GoodsPageListParam pageListParam = new GoodsPageListParam();
        pageListParam.setSelectType(GoodsPageListParam.GOODS_PRD_LIST);
        saas().getShopApp(getShopId()).goods.disposeGoodsPageListVo(goodsPageListVos, pageListParam);
        return goodsPageListVos;
    }

    /**
     * 根据门店获取商品信息
     *
     * @param productId
     * @param storeId
     * @return
     */
    public GoodsSpecProductRecord getStoreProductByProductIdAndStoreId(Integer productId, Integer storeId) {
        GoodsSpecProductRecord goodsSpecproduct = db().select(GOODS_SPEC_PRODUCT.PRD_NUMBER, GOODS_SPEC_PRODUCT.GOODS_ID)
            .from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_ID.eq(productId)).fetchOneInto(GoodsSpecProductRecord.class);
        if (goodsSpecproduct != null && storeId > 0) {
            // 如果有门店,门店价格和数量替换商品价格数量
            StoreGoodsRecord storeGoods = db().selectFrom(STORE_GOODS)
                .where(STORE_GOODS.PRD_ID.eq(productId))
                .and(STORE_GOODS.IS_ON_SALE.eq((byte) 1))
                .and(STORE_GOODS.IS_SYNC.eq((byte) 1))
                .and(STORE_GOODS.STORE_ID.eq(storeId)).fetchOne();
            if (storeGoods != null) {
                goodsSpecproduct.setPrdPrice(storeGoods.getProductPrice());
                goodsSpecproduct.setPrdNumber(storeGoods.getProductNumber());
            }
        }
        return goodsSpecproduct;
    }


    /**
     * 转化规格商品格式 好物圈用
     *
     * @param prdDesc
     * @return
     */
    public List<SkuAttrList> getSkuAttrList(String prdDesc) {
        List<SkuAttrList> list = new ArrayList<SkuAttrList>();
        if (StringUtils.isEmpty(prdDesc)) {
            list.add(new SkuAttrList("", ""));
        } else {
            String[] split = prdDesc.split(PRD_VAL_DELIMITER);
            list.add(new SkuAttrList(split[0], split[1]));
        }
        return list;
    }

    /**
     * 取规格库存
     *
     * @param prdId
     * @return
     */
    public int getPrdNumberByPrdId(int prdId) {
        return db().select(GOODS_SPEC_PRODUCT.PRD_NUMBER).from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_ID.eq(prdId)).fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 获取商品的所有规格信息
     *
     * @param goodsId
     * @return
     */
    public List<GoodsSpecProductRecord> getGoodsDetailPrds(Integer goodsId) {
        return db().select(GOODS_SPEC_PRODUCT.PRD_ID, GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.PRD_MARKET_PRICE, GOODS_SPEC_PRODUCT.PRD_NUMBER,
            GOODS_SPEC_PRODUCT.PRD_SPECS, GOODS_SPEC_PRODUCT.PRD_DESC)
            .from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId)).orderBy(GOODS_SPEC_PRODUCT.PRD_ID)
            .fetchInto(GoodsSpecProductRecord.class);
    }

    /**
     * 取同商品最大的一个规格价
     *
     * @param goodsId
     * @return
     */
    public BigDecimal getMaxPrdPrice(int goodsId) {
        return db().select(DSL.max(GOODS_SPEC_PRODUCT.PRD_PRICE)).from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId)).fetchOptionalInto(BigDecimal.class).orElse(BigDecimal.ZERO);
    }

    /**
     * 取单规格商品的规格ID
     *
     * @param goodsId
     * @return
     */
    public int getDefaultPrdId(int goodsId) {
        return db().select(GOODS_SPEC_PRODUCT.PRD_ID).from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId)).fetchOptionalInto(Integer.class).orElse(0);
    }


    /**
     * 查询传入的prdSn集合中哪些是数据库中已经存在的
     *
     * @param prdSn
     * @return
     */
    public List<String> findSkuPrdSnExist(List<String> prdSns) {
        return db().select(GOODS_SPEC_PRODUCT.PRD_SN).from(GOODS_SPEC_PRODUCT)
            .where(GOODS_SPEC_PRODUCT.PRD_SN.in(prdSns))
            .fetch(GOODS_SPEC_PRODUCT.PRD_SN);
    }

    /**
     * 查询传入的prdCodes集合中哪些是数据库中已经存在的
     *
     * @param prdCodes
     * @return
     */
    public List<String> findSkuPrdCodesExist(List<String> prdCodes) {
        return db().select(GOODS_SPEC_PRODUCT.PRD_CODES).from(GOODS_SPEC_PRODUCT)
            .where(GOODS_SPEC_PRODUCT.PRD_CODES.in(prdCodes))
            .fetch(GOODS_SPEC_PRODUCT.PRD_CODES);
    }
    /**
     * 判断商品规格名和规格值是否内部自重复
     *
     * @param specs 商品规格
     * @return {@link JsonResult#getError()}!=0表示存在重复
     */
    public boolean isSpecNameOrValueRepeat(List<GoodsSpec> specs) {
        //在选择默认规格的情况下该字段可以是空
        if (specs == null || specs.size() == 0) {
            return true;
        }

        Map<String, Object> specNameRepeatMap = new HashMap<>(specs.size());

        for (GoodsSpec goodsSpec : specs) {

            specNameRepeatMap.put(goodsSpec.getSpecName(), null);
            //检查同一规格下规格值是否重复
            List<GoodsSpecVal> goodsSpecVals = goodsSpec.getGoodsSpecVals();
            if (goodsSpecVals == null || goodsSpecVals.size() == 0) {
                continue;
            }
            Map<String, Object> specValRepeatMap = new HashMap<>(goodsSpecVals.size());
            for (GoodsSpecVal goodsSpecVal : goodsSpecVals) {
                specValRepeatMap.put(goodsSpecVal.getSpecValName(), null);
            }
            if (specValRepeatMap.size() != goodsSpecVals.size()) {
                return false;
            }
        }
        if (specs.size() != specNameRepeatMap.size()) {
            return false;
        }

        return true;
    }


    /**
     * 验证输入的商品规格属性和商品规格键值的正确性，
     * 验证方式是动态计算{@link GoodsSpecProduct#}的值是否和{@link GoodsSpec}计算出来的值一致
     *
     * @param goodsSpecProducts 商品规格属性
     * @param goodsSpecs        商品规格键值
     * @return JsonResultCode
     */
    public boolean isGoodsSpecProductDescRight(List<GoodsSpecProduct> goodsSpecProducts, List<GoodsSpec> goodsSpecs) {

        //判断是否是默认sku
        boolean isDefaultSku = goodsSpecProducts.size() == 1 &&
            org.apache.commons.lang3.StringUtils.isBlank(goodsSpecProducts.get(0).getPrdDesc()) &&
            (goodsSpecs == null || goodsSpecs.size() == 0);

        //是默认sku直接返回
        if (isDefaultSku) {
            return true;
        }

        //根据商品规格值计算出应该有多少规格数据，计算笛卡尔积
        int cartesianNum = 1;
        for (GoodsSpec goodsSpec : goodsSpecs) {
            //商品写了规格名称但是未设置规格值
            if (goodsSpec.getGoodsSpecVals() == null || goodsSpec.getGoodsSpecVals().size() == 0) {
                return false;
            }
            //笛卡尔积计算
            cartesianNum *= goodsSpec.getGoodsSpecVals().size();
        }

        //传入的规格属性条目和根据规格名值计算出来的数据不对应
        if (cartesianNum != goodsSpecProducts.size()) {
            return false;
        }

        //验证传入的prdDesc的正确性，拆解prdDesc，检查对应的名和值是否咋goodsSpec中都存在
        List<String> specDescs = goodsSpecProducts.stream().map(GoodsSpecProduct::getPrdDesc).collect(Collectors.toList());
        Map<String, List<GoodsSpecVal>> specs = goodsSpecs.stream().collect(Collectors.toMap(GoodsSpec::getSpecName, GoodsSpec::getGoodsSpecVals));

        for (String prdDesc : specDescs) {
            if (prdDesc == null) {
                return false;
            }
            String[] splits = prdDesc.split(GoodsSpecProductService.PRD_DESC_DELIMITER);

            for (String split : splits) {
                String[] s = split.split(GoodsSpecProductService.PRD_VAL_DELIMITER);

                if (s.length < 2 || org.apache.commons.lang3.StringUtils.isBlank(s[0]) || org.apache.commons.lang3.StringUtils.isBlank(s[1])) {
                    return false;
                }

                String speck = s[0], specv = s[1];

                //检查规格名称是否存在
                List<GoodsSpecVal> goodsSpecVals = specs.get(speck);
                //规格名称不存在
                if (goodsSpecVals == null) {
                    return false;
                }

                boolean b = goodsSpecVals.stream().anyMatch(goodsSpecVal -> org.apache.commons.lang3.StringUtils.equals(specv, goodsSpecVal.getSpecValName()));

                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }
    public List<GoodsSpecProductRecord> getGoodsSpecPrdBySn(Collection<String> prdSn) {
        return db().selectFrom(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_SN.in(prdSn)).fetchInto(GoodsSpecProductRecord.class);
    }

    /**
     *  erp-ekb对接外部系统使用-返回商品附属的规格信息
     * @param goodsIds 商品id集合
     * @return
     */
    public Map<Integer,List<GoodsSpecProductRecord>> apiGetGoodsSpecPrdMapByGoodsIds(List<Integer> goodsIds) {
       return db().select(GOODS_SPEC_PRODUCT.GOODS_ID,GOODS_SPEC_PRODUCT.PRD_ID,GOODS_SPEC_PRODUCT.PRD_SN,GOODS_SPEC_PRODUCT.PRD_PRICE,GOODS_SPEC_PRODUCT.PRD_NUMBER,GOODS_SPEC_PRODUCT.PRD_DESC,GOODS_SPEC_PRODUCT.PRD_IMG)
            .from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.in(goodsIds))
            .fetchGroups(GOODS_SPEC_PRODUCT.GOODS_ID,GoodsSpecProductRecord.class);
    }

    /**
     * erp-ekb对接外部系统使用-返回商品附属的已删除的规格信息
     * 如果删除删除了，那么在规格表内是不存在的(返回的结果肯定大于等于商品真实sku,需要和erp后期沟通再)
     * @param goodsId
     * @return
     */
    public List<GoodsSpecProductBakRecord> apiGetGoodsSpecPrdDeletedMbyGoodsId(Integer goodsId) {
        return db().select(GOODS_SPEC_PRODUCT_BAK.GOODS_ID, GOODS_SPEC_PRODUCT_BAK.PRD_ID, GOODS_SPEC_PRODUCT_BAK.PRD_SN, GOODS_SPEC_PRODUCT_BAK.PRD_PRICE, GOODS_SPEC_PRODUCT_BAK.PRD_NUMBER, GOODS_SPEC_PRODUCT_BAK.PRD_DESC, GOODS_SPEC_PRODUCT_BAK.PRD_IMG)
            .from(GOODS_SPEC_PRODUCT_BAK).where(GOODS_SPEC_PRODUCT_BAK.GOODS_ID.eq(goodsId)).fetchInto(GoodsSpecProductBakRecord.class);
    }

    /**
     * erp-ekb对接外部系统使用-根据skuId 返回其所属商品的所有sku
     * @param prdId
     * @return
     */
    public Map<Integer,GoodsSpecProductRecord> apiGetGoodsSpecPrdByPrdId(Integer prdId){
        Integer goodsId = db().select(GOODS_SPEC_PRODUCT.GOODS_ID).from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_ID.eq(prdId))
            .fetchAny(GOODS_SPEC_PRODUCT.GOODS_ID);
        if (goodsId == null) {
            return null;
        }
        return db().select(GOODS_SPEC_PRODUCT.GOODS_ID,GOODS_SPEC_PRODUCT.PRD_ID,GOODS_SPEC_PRODUCT.PRD_NUMBER)
                .from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId))
                .fetchMap(GOODS_SPEC_PRODUCT.PRD_ID,GoodsSpecProductRecord.class);
    }
    //******************goods_spec_product_bak************************************//
    /**
     * 查询商品规格
     */
    public ProductSmallInfoVo getProductBakByPrdId(Integer prdId){
        return db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS_SPEC_PRODUCT_BAK.PRD_DESC, GOODS.GOODS_IMG,
            GOODS_SPEC_PRODUCT_BAK.PRD_NUMBER, GOODS_SPEC_PRODUCT_BAK.PRD_PRICE, GOODS.IS_ON_SALE)
            .from(GOODS_SPEC_PRODUCT_BAK)
            .leftJoin(GOODS).on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT_BAK.GOODS_ID))
            .where(GOODS_SPEC_PRODUCT_BAK.PRD_ID.eq(prdId)).fetchOneInto(ProductSmallInfoVo.class);
    }


}
