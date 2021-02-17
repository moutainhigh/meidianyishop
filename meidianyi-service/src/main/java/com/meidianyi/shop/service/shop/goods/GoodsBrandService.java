package com.meidianyi.shop.service.shop.goods;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.ChineseToPinYinUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.BrandClassifyRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsBrandRecord;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.jedis.data.GoodsBrandDataHelper;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.brand.*;
import com.meidianyi.shop.service.pojo.wxapp.goods.brand.GoodsBrandClassifyNameMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.brand.GoodsBrandMpPinYinVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.brand.GoodsBrandMpVo;
import com.meidianyi.shop.service.shop.goods.es.EsDataUpdateMqService;
import com.meidianyi.shop.service.shop.image.ImageService;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record8;
import org.jooq.SelectSeekStep2;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.*;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

/**
 * 商品品牌
 *
 * @author 李晓冰
 * @date 2019年6月25日
 */
@Service
public class GoodsBrandService extends ShopBaseService {

    private final static String BRAND_NUM = "brand_num";

    private final static int DEFAULT_GOODS_BRAND_ID = 0;

    @Autowired
    ImageService imageService;
    @Autowired
    GoodsBrandDataHelper goodsBrandDataHelper;
    @Autowired
    GoodsService goodsService;
    @Autowired
    private EsDataUpdateMqService esDataUpdateMqService;
    /**
     * 分页获取品牌信息
     * @param param 分页参数
     * @return 分页信息
     */
    public PageResult<GoodsBrandPageListVo> getPageList(GoodsBrandPageListParam param) {
        Condition condition = buildOptions(param);
        SelectSeekStep2<Record8<Integer, String, String, Byte, Timestamp, String, Short, Byte>, Byte, Timestamp> select = db().select(GOODS_BRAND.ID, GOODS_BRAND.BRAND_NAME, GOODS_BRAND.LOGO, GOODS_BRAND.FIRST, GOODS_BRAND.CREATE_TIME,
            BRAND_CLASSIFY.CLASSIFY_NAME, BRAND_CLASSIFY.FIRST.as("classify_first"), GOODS_BRAND.IS_RECOMMEND)
            .from(GOODS_BRAND).leftJoin(BRAND_CLASSIFY).on(GOODS_BRAND.CLASSIFY_ID.eq(BRAND_CLASSIFY.CLASSIFY_ID))
            .where(condition).and(BRAND_CLASSIFY.DEL_FLAG.eq(DelFlag.NORMAL.getCode()).or(BRAND_CLASSIFY.DEL_FLAG.isNull()))
            .orderBy(GOODS_BRAND.FIRST.desc(), GOODS_BRAND.CREATE_TIME.desc());

        PageResult<GoodsBrandPageListVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), GoodsBrandPageListVo.class);

        List<GoodsBrandPageListVo> dataList = pageResult.dataList;
        // 处理商品数量和图片路径
        if (dataList != null && dataList.size() > 0) {
            List<Integer> brandIds = dataList.stream().map(GoodsBrandPageListVo::getId).collect(Collectors.toList());
            Map<Integer, Long> goodsMap = db().select(GOODS_BRAND.ID, GOODS.GOODS_ID).from(GOODS).innerJoin(GOODS_BRAND).on(GOODS.BRAND_ID.eq(GOODS_BRAND.ID))
                .where(GOODS_BRAND.ID.in(brandIds)).fetch().stream().collect(Collectors.groupingBy(x -> x.get(GOODS_BRAND.ID), Collectors.counting()));
            dataList.forEach(brand-> {
                brand.setGoodsNum(goodsMap.getOrDefault(brand.getId(),0L).intValue());
                brand.setLogo(getImgFullUrlUtil(brand.getLogo()));
            });
        }

        return pageResult;
    }

    /**
     * 根据过滤条件构造对应的sql语句
     * @param param 分页参数
     * @return 分页过滤条件
     */
    private Condition buildOptions(GoodsBrandPageListParam param) {
        Condition condition = GOODS_BRAND.DEL_FLAG.eq(DelFlag.NORMAL.getCode());

        if (!StringUtils.isBlank(param.getBrandName())) {
            condition = condition.and(GOODS_BRAND.BRAND_NAME.like(this.likeValue(param.getBrandName())));
        }

        if (param.getStartAddTime() != null) {
            condition = condition.and(GOODS_BRAND.CREATE_TIME.ge(param.getStartAddTime()));
        }

        if (param.getEndAddTime() != null) {
            condition = condition.and(GOODS_BRAND.CREATE_TIME.le(param.getEndAddTime()));
        }

        if (param.getClassifyId() != null) {
            condition = condition.and(GOODS_BRAND.CLASSIFY_ID.eq(param.getClassifyId()));
        }

        if (param.getIsRecommend() != null) {
            condition = condition.and(GOODS_BRAND.IS_RECOMMEND.eq(param.getIsRecommend()));
        }

        return condition;
    }


    public List<GoodsBrandMpVo> getGoodsBrandVoById(List<Integer> ids){
        Objects.requireNonNull(ids);
        if( ids.isEmpty() ){
            return null;
        }
        Condition condition;
        if ( ids.size() == 1 ){
            condition = GOODS_BRAND.ID.eq(ids.get(0));
        }else{
            condition = GOODS_BRAND.ID.in(ids);
        }
        return db().select(GOODS_BRAND.ID,GOODS_BRAND.BRAND_NAME,GOODS_BRAND.LOGO).
            from(GOODS_BRAND).
            where(condition).
            fetchInto(GoodsBrandMpVo.class);
    }

    /**
     * 添加品牌
     * @param goodsBrand
     * @return 数据库受影响行数
     */
    public void insert(GoodsBrandAddParam goodsBrand) {
        transaction(() -> {
            GoodsBrandRecord goodsBrandRecord = db().newRecord(GOODS_BRAND);
            assign(goodsBrand, goodsBrandRecord);
            goodsBrandRecord.insert();
            if (goodsBrand.getGoodsIds() != null && goodsBrand.getGoodsIds().size() > 0) {
                db().update(GOODS).set(GOODS.BRAND_ID, goodsBrandRecord.getId())
                    .where(GOODS.GOODS_ID.in(goodsBrand.getGoodsIds()))
                    .execute();
            }
            //cache update
        });
        if( !CollectionUtils.isEmpty(goodsBrand.getGoodsIds()) ){
            esDataUpdateMqService.addEsGoodsIndex(goodsBrand.getGoodsIds(),getShopId(),DBOperating.UPDATE);
        }
        goodsBrandDataHelper.update(Collections.singletonList(goodsBrand.getId()));
    }

    /**
     * 假删除指定品牌
     * @param brandId
     */
    public void delete(Integer brandId) {
        transaction(
            () ->
        {
            deleteGoodsBrand(Collections.singletonList(brandId));

        });
        esDataUpdateMqService.updateEsGoodsIndexByBrandId(brandId,getShopId());
    }

    private void deleteGoodsBrand(List<Integer> brandIds) {
        db().update(GOODS_BRAND)
            .set(GOODS_BRAND.DEL_FLAG, DelFlag.DISABLE.getCode())
            .set(GOODS_BRAND.BRAND_NAME, DSL.concat(DelFlag.DEL_ITEM_PREFIX)
                .concat(GOODS_BRAND.ID)
                .concat(DelFlag.DEL_ITEM_SPLITER)
                .concat(GOODS_BRAND.BRAND_NAME))
            .where(GOODS_BRAND.ID.in(brandIds))
            .execute();
        saas().getShopApp(getShopId()).goods.clearBrandId(brandIds);
        //cache update
        goodsBrandDataHelper.delete(brandIds);
    }

    /**
     * 更新指定商品
     * @param goodsBrand
     * @return
     */
    public void update(GoodsBrandUpdateParam goodsBrand) {
        transaction(() -> {
            GoodsBrandRecord goodsBrandRecord = db().newRecord(GOODS_BRAND);
            assign(goodsBrand, goodsBrandRecord);
            goodsBrandRecord.update();
            if (goodsBrand.getOldGoodsIds() != null && goodsBrand.getOldGoodsIds().size() > 0) {
                db().update(GOODS).set(GOODS.BRAND_ID,DEFAULT_GOODS_BRAND_ID)
                    .where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).and(GOODS.GOODS_ID.in(goodsBrand.getOldGoodsIds()))
                    .execute();
            }

            if (goodsBrand.getGoodsIds() != null && goodsBrand.getGoodsIds().size() > 0) {
                db().update(GOODS).set(GOODS.BRAND_ID, goodsBrand.getId())
                    .where(GOODS.GOODS_ID.in(goodsBrand.getGoodsIds()))
                    .execute();
            }
        });
        Set<Integer> goodsIds = Sets.newHashSet();
        if( CollectionUtils.isNotEmpty(goodsBrand.getOldGoodsIds()) ){
            goodsIds.addAll(goodsBrand.getOldGoodsIds());
        }
        if( CollectionUtils.isNotEmpty(goodsBrand.getGoodsIds()) ){
            goodsIds.addAll(goodsBrand.getGoodsIds());
        }
        esDataUpdateMqService.addEsGoodsIndex(Lists.newArrayList(goodsIds.iterator()),getShopId(),DBOperating.UPDATE);
    }

    /**
     * 查询单个
     * @param id
     * @return
     */
    public GoodsBrandVo select(Integer id) {
        GoodsBrandVo brand = db().select(GOODS_BRAND.ID, GOODS_BRAND.BRAND_NAME, GOODS_BRAND.E_NAME,
            GOODS_BRAND.LOGO, GOODS_BRAND.FIRST, GOODS_BRAND.CLASSIFY_ID, GOODS_BRAND.IS_RECOMMEND)
            .from(GOODS_BRAND).where(GOODS_BRAND.ID.eq(id)).fetchAny().into(GoodsBrandVo.class);

        if (brand == null) {
            return null;
        }
        List<Integer> goodsIds = db().select(GOODS.GOODS_ID).from(GOODS).where(GOODS.BRAND_ID.eq(id)).and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .fetch().into(Integer.class);

        brand.setGoodsIds(goodsIds);

        brand.setFullUrlLogo(getImgFullUrlUtil(brand.getLogo()));
        return brand;
    }

    public Map<String,Integer> getNormalBrandMap(){
        return db().select(GOODS_BRAND.BRAND_NAME, GOODS_BRAND.ID).from(GOODS_BRAND)
            .where(GOODS_BRAND.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).fetchMap(GOODS_BRAND.BRAND_NAME, GOODS_BRAND.ID);
    }

    public Integer addBrand(String brandName) {
        GoodsBrandRecord record = db().newRecord(GOODS_BRAND);
        record.setBrandName(brandName);
        record.insert();
        return record.getId();
    }
    /**
     * 根据品牌名称获取品牌id，如果不存在就新增
     * @param brandName
     * @return
     */
    public Integer getOrAddBrand(String brandName){
        Integer brandId = db().select(GOODS_BRAND.ID).from(GOODS_BRAND)
            .where(GOODS_BRAND.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS_BRAND.BRAND_NAME.eq(brandName)))
            .fetchAny(GOODS_BRAND.ID);
        if (brandId != null) {
            return brandId;
        } else {
            GoodsBrandRecord record = db().newRecord(GOODS_BRAND);
            record.setBrandName(brandName);
            record.insert();
            return record.getId();
        }
    }

    /**
     * 判断商品名称是否存在，新增使用
     * @return
     */
    public boolean isBrandNameExist(Integer brandId,String brandName) {

        Condition condition= GOODS_BRAND.DEL_FLAG.eq(DelFlag.NORMAL.getCode()).and(GOODS_BRAND.BRAND_NAME.eq(brandName));

        if (brandId != null) {
            condition = condition.and(GOODS_BRAND.ID.ne(brandId));
        }
        int count = db().fetchCount(GOODS_BRAND, condition);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据brandId列出所有品牌
     * @return key 品牌id，value 品牌信息
     */
    public Map<Integer, GoodsBrandSelectListVo>  listGoodsBrandNameByIds(List<Integer> ids) {
        return db().select(GOODS_BRAND.ID, GOODS_BRAND.BRAND_NAME)
            .from(GOODS_BRAND)
            .where(GOODS_BRAND.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(GOODS_BRAND.ID.in(ids))
            .fetch().into(GoodsBrandSelectListVo.class)
            .stream().collect(Collectors.toMap(GoodsBrandSelectListVo::getId, x->x));
    }
    /**
     * 列出所有品牌
     * @return 品牌列表信息
     */
    public List<GoodsBrandSelectListVo> getGoodsBrandSelectList() {
        List<GoodsBrandRecord> goodsBrandRecords = getGoodsBrandListDao();
        return goodsBrandRecords.stream().map(x -> x.into(GoodsBrandSelectListVo.class)).collect(Collectors.toList());
    }

    /**
     * 品牌分类列表
     * @return 品牌分类列表
     */
    public List<GoodsBrandClassifyVo> getBrandClassifyList() {

        List<BrandClassifyRecord> goodsBrandRecords = getBrandClassifyListDao();

        return goodsBrandRecords.stream().map(x -> x.into(GoodsBrandClassifyVo.class)).collect(Collectors.toList());
    }

    /**
     * 获取所有品牌分类列表数据
     * @return 品牌分类列表
     */
    private List<BrandClassifyRecord> getBrandClassifyListDao(){
        return db().select()
            .from(BRAND_CLASSIFY)
            .where(BRAND_CLASSIFY.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .fetch().into(BrandClassifyRecord.class);
    }

    /**
     * 获取所有商品品牌列表
     * @return GoodsBrandRecord 列表
     */
    private List<GoodsBrandRecord> getGoodsBrandListDao(){
       return db().select()
            .from(GOODS_BRAND)
            .where(GOODS_BRAND.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .fetch().into(GoodsBrandRecord.class);
    }


    /**
     * 品牌分类分页查询
     * @param param
     * @return
     */
    public PageResult<GoodsBrandClassifyPageListVo> getBrandClassifyPageList(GoodsBrandClassifyPageListParam param) {
        Condition condition = buildBrandClassifyCondition(param);
        SelectSeekStep2<Record, Short, Timestamp> select = db().select().from(BRAND_CLASSIFY).where(condition).orderBy(BRAND_CLASSIFY.FIRST.desc(), BRAND_CLASSIFY.CREATE_TIME.desc());

        PageResult<GoodsBrandClassifyPageListVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), GoodsBrandClassifyPageListVo.class);

        List<GoodsBrandClassifyPageListVo> dataList = pageResult.dataList;
        if (dataList != null && dataList.size() > 0) {
            List<Integer> classifyIds = dataList.stream().map(GoodsBrandClassifyPageListVo::getClassifyId).collect(Collectors.toList());
            Map<Integer, Long> brandNumMap = db().select(GOODS_BRAND.CLASSIFY_ID, GOODS_BRAND.ID).from(GOODS_BRAND)
                .where(GOODS_BRAND.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).and(GOODS_BRAND.CLASSIFY_ID.in(classifyIds))
                .fetch().stream().collect(Collectors.groupingBy(x -> x.get(GOODS_BRAND.CLASSIFY_ID), Collectors.counting()));
            dataList.forEach(x-> x.setBrandNum(brandNumMap.getOrDefault(x.getClassifyId(),0L).intValue()));
        }
        return  pageResult;
    }

    private Condition buildBrandClassifyCondition(GoodsBrandClassifyPageListParam param) {
        Condition condition = BRAND_CLASSIFY.DEL_FLAG.eq(DelFlag.NORMAL.getCode());

        if (!StringUtils.isBlank(param.getClassifyName())) {
            condition = condition.and(BRAND_CLASSIFY.CLASSIFY_NAME.like(this.likeValue(param.getClassifyName())));
        }

        if (param.getStartAddTime() != null) {
            condition = condition.and(BRAND_CLASSIFY.CREATE_TIME.ge(param.getStartAddTime()));
        }

        if (param.getEndAddTime() != null) {
            condition = condition.and(BRAND_CLASSIFY.CREATE_TIME.le(param.getEndAddTime()));
        }
        return condition;
    }


    /**
     * 新增品牌分类
     * @param param 新增品牌分类参数
     */
    public void insertBrandClassify(GoodsBrandClassifyAddUpdateParam param) {
        db().insertInto(BRAND_CLASSIFY, BRAND_CLASSIFY.CLASSIFY_NAME, BRAND_CLASSIFY.FIRST)
            .values(param.getClassifyName(), param.getFirst()).execute();
    }

    /**
     * 修改分类
     *
     * @param param
     */
    public void updateBrandClassify(GoodsBrandClassifyAddUpdateParam param) {
        BrandClassifyRecord brandClassifyRecord = new BrandClassifyRecord();

        brandClassifyRecord.setClassifyId(param.getClassifyId());

        if (param.getClassifyName() != null) {
            brandClassifyRecord.setClassifyName(param.getClassifyName());
        }
        if (param.getFirst() != null) {
            brandClassifyRecord.setFirst(param.getFirst());
        }

        db().executeUpdate(brandClassifyRecord);
    }

    public void deleteBrandClassify(Integer classifyId) {
        transaction(() -> {
            db().update(BRAND_CLASSIFY)
                .set(BRAND_CLASSIFY.DEL_FLAG, DelFlag.DISABLE.getCode())
                .set(BRAND_CLASSIFY.CLASSIFY_NAME, DSL.concat(getDelPrefix(classifyId))
                    .concat(BRAND_CLASSIFY.CLASSIFY_NAME))
                .where(BRAND_CLASSIFY.CLASSIFY_ID.eq(classifyId)).execute();

            List<Integer> brandIds = db().select(GOODS_BRAND.ID)
                .from(GOODS_BRAND)
                .where(GOODS_BRAND.CLASSIFY_ID.eq(classifyId))
                .fetch().into(Integer.class);

            //解除品牌关联关系
           db().update(GOODS_BRAND).set(GOODS_BRAND.CLASSIFY_ID,DEFAULT_GOODS_BRAND_ID).where(GOODS_BRAND.ID.in(brandIds)).execute();
        });
    }

    /**
     * 品牌批量绑定至品牌分类
     * @param param {@link GoodsBrandClassifyBatchBind}
     */
    public void classifyBatchBind(GoodsBrandClassifyBatchBind param) {
        db().update(GOODS_BRAND).set(GOODS_BRAND.CLASSIFY_ID, param.getClassifyId())
            .where(GOODS_BRAND.ID.in(param.getBrandIds()))
            .execute();
    }

    public boolean isClassifyNameExist(Integer classifyId,String classifyName) {
        Condition condition = BRAND_CLASSIFY.DEL_FLAG.eq(DelFlag.NORMAL.getCode()).and(BRAND_CLASSIFY.CLASSIFY_NAME.eq(classifyName));

        if (classifyId != null) {
            condition = condition.and(BRAND_CLASSIFY.CLASSIFY_ID.ne(classifyId));
        }

        int count = db().fetchCount(BRAND_CLASSIFY, condition);

        return count > 0;
    }

    /**
     * 获取关联了商品的品牌数据，按照名称拼音进行组织
     * @return 相同开头名称的处于同一个list集合中。
     */
    public List<GoodsBrandMpPinYinVo> getBrandAssociatedWithGoodsGroupByPinYinNameMp(){
        List<Integer> goodsBrandIds = goodsService.getGoodsBrandIds();
        List<GoodsBrandMpVo> goodsBrandMpVos = db().select(GOODS_BRAND.ID, GOODS_BRAND.BRAND_NAME, GOODS_BRAND.LOGO).from(GOODS_BRAND)
            .where(GOODS_BRAND.DEL_FLAG.eq(DelFlag.NORMAL.getCode()).and(GOODS_BRAND.ID.in(goodsBrandIds)))
            .fetchInto(GoodsBrandMpVo.class);

        return disposeBrandToGroupByPinYinNameMp(goodsBrandMpVos);
    }

    /**
     * 查询所有有效的品牌，按照名称拼音进行组织
     * @return 相同开头名称的处于同一个list集合中。
     */
    public List<GoodsBrandMpPinYinVo> getAllBrandGroupByPinYinNameMp(){
        List<GoodsBrandMpVo> goodsBrandMpVos = db().select(GOODS_BRAND.ID, GOODS_BRAND.BRAND_NAME, GOODS_BRAND.LOGO)
            .from(GOODS_BRAND)
            .where(GOODS_BRAND.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(GOODS_BRAND.FIRST.desc(), GOODS_BRAND.CREATE_TIME)
            .fetchInto(GoodsBrandMpVo.class);

        return disposeBrandToGroupByPinYinNameMp(goodsBrandMpVos);
    }

    /**
     * 将{@link GoodsBrandMpVo} 按照名称拼音（或英文单词）首字母进行分组
     * @param goodsBrandMpVos 待分组品牌列表
     * @return 处理后的分组对象列表{@link GoodsBrandMpPinYinVo}
     */
    private List<GoodsBrandMpPinYinVo> disposeBrandToGroupByPinYinNameMp(List<GoodsBrandMpVo> goodsBrandMpVos){
        // 处理拼音
        TreeMap<String,List<GoodsBrandMpVo>> treeMap = new TreeMap<>();
        for (GoodsBrandMpVo pinYinVo : goodsBrandMpVos) {
            String c = ChineseToPinYinUtil.getStartAlphabet(pinYinVo.getBrandName());
            pinYinVo.setLogo(getImgFullUrlUtil(pinYinVo.getLogo()));

            List<GoodsBrandMpVo> list = treeMap.computeIfAbsent(c, k -> new ArrayList<>());
            list.add(pinYinVo);
        }

        // 处理为返回值
        LinkedList<GoodsBrandMpPinYinVo> retList = new LinkedList<>();
        treeMap.forEach((c,goodsBrandsList)->{
            GoodsBrandMpPinYinVo vo =new GoodsBrandMpPinYinVo();
            vo.setCharacter(c);
            vo.setGoodsBrands(goodsBrandsList);
            retList.add(vo);
        });

        // 处理#符合的问题
        if (retList.peek() != null&&ChineseToPinYinUtil.OTHER_CHARACTER.equals(retList.peek().getCharacter())) {
            GoodsBrandMpPinYinVo t = retList.removeFirst();
            retList.addLast(t);
        }
        return retList;
    }

    /**
     * 获取所有推荐品牌（前端按照品牌列表显示）
     * @return 推荐品牌集合
     */
    public List<GoodsBrandMpVo> getAllRecommendBrandMp(){
        List<GoodsBrandMpVo> goodsBrandMpVos = db().selectFrom(GOODS_BRAND).where(GOODS_BRAND.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(GOODS_BRAND.IS_RECOMMEND.eq(GoodsConstant.RECOMMEND_BRAND)).orderBy(GOODS_BRAND.FIRST.desc(), GOODS_BRAND.CREATE_TIME.desc())
            .fetchInto(GoodsBrandMpVo.class);

        goodsBrandMpVos.forEach(goodsBrandMpVo -> goodsBrandMpVo.setLogo(getImgFullUrlUtil(goodsBrandMpVo.getLogo())));
        return goodsBrandMpVos;
    }

    /**
     * 获取所有推荐品牌（前端按照品牌分类显示）,
     * @return {@link GoodsBrandClassifyNameMpVo} 的list,每一个元素是分类名称和该分类下所有品牌
     */
    public  List<GoodsBrandClassifyNameMpVo> getAllRecommendBrandGroupByClassifyMp(){
        // 先获取所有有效推荐品牌（按照first和createTime排序）和其所属分类id对应的map
        Map<Integer, List<GoodsBrandMpVo>> goodsBrandMap =
            db().select(GOODS_BRAND.ID, GOODS_BRAND.BRAND_NAME, GOODS_BRAND.E_NAME, GOODS_BRAND.LOGO,GOODS_BRAND.CLASSIFY_ID)
            .from(GOODS_BRAND)
            .where(GOODS_BRAND.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).and(GOODS_BRAND.IS_RECOMMEND.eq(GoodsConstant.RECOMMEND_BRAND))
            .orderBy(GOODS_BRAND.FIRST.desc(), GOODS_BRAND.CREATE_TIME.desc())
            .fetchGroups(GOODS_BRAND.CLASSIFY_ID, GoodsBrandMpVo.class);

        // 获取品牌分类
        List<BrandClassifyRecord> brandClassify = db().selectFrom(BRAND_CLASSIFY).where(BRAND_CLASSIFY.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .orderBy(BRAND_CLASSIFY.FIRST.desc(), BRAND_CLASSIFY.CREATE_TIME.desc()).fetchInto(BrandClassifyRecord.class);

        List<GoodsBrandClassifyNameMpVo> retList = new ArrayList<>();
        // 根据分类获取其分类下的所有品牌，并转换为结果数据
        for (BrandClassifyRecord record : brandClassify) {
            Integer classifyId = record.getClassifyId();
            if (goodsBrandMap.get(classifyId) != null) {
                GoodsBrandClassifyNameMpVo vo =new GoodsBrandClassifyNameMpVo();
                vo.setClassifyName(record.getClassifyName());
                // 处理图片地址
                List<GoodsBrandMpVo> goodsBrandMpVos = goodsBrandMap.get(classifyId);
                goodsBrandMpVos.forEach(brand-> brand.setLogo(getImgFullUrlUtil(brand.getLogo())));
                vo.setGoodsBrands(goodsBrandMpVos);

                retList.add(vo);
            }
        }
        if (goodsBrandMap.get(GoodsConstant.NO_CLASSIFY_ID) != null) {
            GoodsBrandClassifyNameMpVo vo =new GoodsBrandClassifyNameMpVo();
            List<GoodsBrandMpVo> goodsBrandMpVos = goodsBrandMap.get(GoodsConstant.NO_CLASSIFY_ID);
            goodsBrandMpVos.forEach(brand-> brand.setLogo(getImgFullUrlUtil(brand.getLogo())));
            vo.setGoodsBrands(goodsBrandMpVos);

            retList.add(vo);
        }
        return retList;
    }

    /**
     * 将相对路劲修改为全路径
     *
     * @param relativePath 相对路径
     * @return null或全路径
     */
    private String getImgFullUrlUtil(String relativePath) {
        if (org.apache.commons.lang3.StringUtils.isBlank(relativePath)) {
            return null;
        } else {
            return imageService.imageUrl(relativePath);
        }
    }

    public boolean exist(Integer id) {
        return db().fetchExists(GOODS_BRAND, GOODS_BRAND.ID.eq(id).and(GOODS_BRAND.DEL_FLAG.eq(BYTE_ZERO)));
    }
    
    public GoodsBrandRecord getBrandById(Integer id) {
    	return db().selectFrom(GOODS_BRAND).where(GOODS_BRAND.ID.eq(id)).fetchAny();
    }
}
