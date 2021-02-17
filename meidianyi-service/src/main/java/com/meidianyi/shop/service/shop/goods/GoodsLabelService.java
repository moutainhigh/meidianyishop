package com.meidianyi.shop.service.shop.goods;


import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.GoodsChronicCoupleDo;
import com.meidianyi.shop.dao.shop.goods.GoodsChronicCoupleDao;
import com.meidianyi.shop.dao.shop.label.GoodsLabelDao;
import com.meidianyi.shop.db.shop.tables.records.GoodsLabelRecord;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam;
import com.meidianyi.shop.service.pojo.shop.goods.label.*;
import com.meidianyi.shop.service.shop.goods.es.EsDataUpdateMqService;
import com.meidianyi.shop.service.shop.goods.es.goods.label.EsGoodsLabelSearchService;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectSeekStep2;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.GOODS_LABEL;
import static com.meidianyi.shop.db.shop.Tables.GOODS_LABEL_COUPLE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

/**
 * @author 黄荣刚
 * @date 2019年7月4日
 */
@Service

public class GoodsLabelService extends ShopBaseService {

    @Autowired
    public GoodsLabelCoupleService goodsLabelCoupleService;

    @Autowired
    public GoodsService goodsService;

    @Autowired
    private EsDataUpdateMqService esDataUpdateMqService;
    @Autowired
    private EsGoodsLabelSearchService esGoodsLabelSearchService;
    @Autowired
    private GoodsLabelDao goodsLabelDao;
    @Autowired
    private GoodsChronicCoupleDao goodsChronicCoupleDao;

    public static final Byte POINT_GOODS = 0;
    public static final Byte ALL_GOODS = 1;
    public static final Byte NONE_GOODS =2;
    public static final Byte IS_CHRONIC = 1;


    public PageResult<GoodsLabelPageListVo> getPageList(GoodsLabelPageListParam param) {
        Condition condition = buildCondition(param);
        SelectSeekStep2<Record, Short, Timestamp> select = db().select().from(GOODS_LABEL).where(condition).orderBy(GOODS_LABEL.LEVEL.desc(), GOODS_LABEL.CREATE_TIME.desc());
        PageResult<GoodsLabelPageListVo> resultPage = getPageResult(select, param.getCurrentPage(), param.getPageRows(), GoodsLabelPageListVo.class);
//        if( !resultPage.getDataList().isEmpty() ){
//            assemblyGoodsNumberForEs(resultPage.getDataList());
//        }

        return resultPage;
    }
    private void    assemblyGoodsNumberForEs(List<GoodsLabelPageListVo> dataList){
        List<Integer> ids = dataList.stream().map(GoodsLabelPageListVo::getId).collect(Collectors.toList());
        try {
            Map<Integer,Integer> numberMap = esGoodsLabelSearchService.getLabelForGoodsNumber(ids);
            dataList.forEach(x->{
                x.setGoodsNum(numberMap.getOrDefault(x.getId(),0));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Condition buildCondition(GoodsLabelPageListParam param) {
        Condition condition = GOODS_LABEL.DEL_FLAG.eq(DelFlag.NORMAL.getCode());

        if (param.getLabelName() != null) {
            condition = condition.and(GOODS_LABEL.NAME.like(likeValue(param.getLabelName())));
        }
        return condition;
    }

    /**
     * 新增商品标签
     *
     * @param param {@link com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelAddAndUpdateParam}
     */
    public void insert(GoodsLabelAddAndUpdateParam param) {
        transaction(() -> {
            GoodsLabelRecord record = db().newRecord(GOODS_LABEL, param);
            record.insert();
            param.setId(record.getId());

            // 不是 '不添加商品选项'
            if (!GoodsLabelAddAndUpdateParam.NONE_GOODS.equals(param.getIsNone())) {
                insertGoodsLabelCouple(param);
            }
        });
        //update elasticSearch data
        try {
            esDataUpdateMqService.updateGoodsLabelByLabelId(getShopId(), DBOperating.INSERT,
                null,Collections.singletonList(param.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据商品标签id删除
     * @param id 标签id
     */
    public void delete(Integer id) {
        if (id == null) {
            return;
        }
        transaction(() -> {
            db().update(GOODS_LABEL)
                .set(GOODS_LABEL.DEL_FLAG, DelFlag.DISABLE.getCode())
                .where(GOODS_LABEL.ID.eq(id))
                .execute();
            goodsLabelCoupleService.deleteByGoodsLabelId(id);
        });

        //update elasticSearch data
        try {
            esDataUpdateMqService.updateGoodsLabelByLabelId(getShopId(), DBOperating.DELETE,
                null,Collections.singletonList(id));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询标签详情
     * @param id 标签id
     * @return {@link com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelVo}
     */
    public GoodsLabelVo selectGoodsLabelById(Integer id) {
        if (id == null) {
            return null;
        }
        Record record = db().select().from(GOODS_LABEL).where(GOODS_LABEL.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).and(GOODS_LABEL.ID.eq(id))
            .fetchAny();
        if (record == null) {
            return null;
        }

        GoodsLabelVo vo = record.into(GoodsLabelVo.class);

        if (!GoodsLabelTypeEnum.ALL.getCode().equals(vo.getIsAll())) {
            Map<Byte, List<Integer>> byteListMap = goodsLabelCoupleService.selectGatIdsByLabelIds(Collections.singletonList(id));
            vo.setGoodsIds(byteListMap.getOrDefault(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode(),new ArrayList<>()));
            vo.setSortIds(byteListMap.getOrDefault(GoodsLabelCoupleTypeEnum.SORTTYPE.getCode(),new ArrayList<>()));
            vo.setCatIds(byteListMap.getOrDefault(GoodsLabelCoupleTypeEnum.CATTYPE.getCode(),new ArrayList<>()));
        }

        return vo;
    }

    /**
     * 修改商品标签
     * @param {@link com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelAddAndUpdateParam}
     */
    public void update(GoodsLabelAddAndUpdateParam param) {
        transaction(() -> {
            GoodsLabelRecord record = db().newRecord(GOODS_LABEL);
            FieldsUtil.assign(param,record);
            record.update();
            goodsLabelCoupleService.deleteByGoodsLabelId(param.getId());
            // 不是 '不添加商品选项'
            if (!GoodsLabelAddAndUpdateParam.NONE_GOODS.equals(param.getIsNone())) {
                insertGoodsLabelCouple(param);
            }
        });

        //update elasticSearch data
        try {
            esDataUpdateMqService.updateGoodsLabelByLabelId(getShopId(), DBOperating.UPDATE,
                null,Collections.singletonList(param.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**将标签未选任何商品状态复位*/
    public void resetLabelIsNone(List<Integer> lableIds){
        db().update(GOODS_LABEL).set(GOODS_LABEL.IS_ALL,POINT_GOODS)
            .where(GOODS_LABEL.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS_LABEL.ID.in(lableIds)).and(GOODS_LABEL.IS_ALL.eq(NONE_GOODS)))
            .execute();
    }
    /**
     * 插入标签关联的商品，平台，商家分类数据
     * @param param
     */
    private void insertGoodsLabelCouple(GoodsLabelDetail param) {
        transaction(()->{
            if (GoodsLabelTypeEnum.ALL.getCode().equals(param.getIsAll())) {
                goodsLabelCoupleService.insertAllGoodsLabelCouple(param.getId());
            } else {
                goodsLabelCoupleService.batchInsertGoodsTypeGoodsLabelCouple(param.getId(), param.getGoodsIds());
                goodsLabelCoupleService.batchInsertSortTypeGoodsLabelCouple(param.getId(), param.getSortIds());
                goodsLabelCoupleService.batchInsertCatTypeGoodsLabelCouple(param.getId(), param.getCatIds());
            }
        });
    }

    /**
     * 判断标签名是否重复
     *
     * @param id   标签id
     * @param name 标签名
     * @return true 存在，false 不存在
     */
    public boolean isLabelNameExist(Integer id, String name) {
        Condition condition = GOODS_LABEL.NAME.eq(name).and(GOODS_LABEL.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        if (id != null) {
            condition = condition.and(GOODS_LABEL.ID.ne(id));
        }

        int count = db().fetchCount(GOODS_LABEL, condition);

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据标签的类型和gta集合查找结果，产生和gta对应的分组数据。
     *
     * @param gtas 要筛选的key值集合
     * @param type map key:type对应类型的id值，value 标签列表
     * @return
     * @author 李晓冰
     */
    public Map<Integer, List<GoodsLabelSelectListVo>> getGtaLabelMap(List<Integer> gtas, GoodsLabelCoupleTypeEnum type) {
        return db().select(GOODS_LABEL_COUPLE.GTA_ID, GOODS_LABEL.ID, GOODS_LABEL.NAME)
            .from(GOODS_LABEL).innerJoin(GOODS_LABEL_COUPLE).on(GOODS_LABEL.ID.eq(GOODS_LABEL_COUPLE.LABEL_ID))
            .where(GOODS_LABEL_COUPLE.GTA_ID.in(gtas)).and(GOODS_LABEL_COUPLE.TYPE.eq(type.getCode()))
            .orderBy(GOODS_LABEL.LEVEL.desc(),GOODS_LABEL.CREATE_TIME.desc())
            .fetch()
            .intoGroups(GOODS_LABEL_COUPLE.GTA_ID, GoodsLabelSelectListVo.class);
    }

    /**
     * 获取绑定为全部商品的标签
     * @return 标签集合
     */
    public  List<GoodsLabelSelectListVo> getAllGoodsLabels(){
       return db().select(GOODS_LABEL_COUPLE.GTA_ID, GOODS_LABEL.ID, GOODS_LABEL.NAME)
            .from(GOODS_LABEL).innerJoin(GOODS_LABEL_COUPLE).on(GOODS_LABEL.ID.eq(GOODS_LABEL_COUPLE.LABEL_ID))
            .where(GOODS_LABEL_COUPLE.TYPE.eq(GoodsLabelCoupleTypeEnum.ALLTYPE.getCode()))
            .orderBy(GOODS_LABEL.LEVEL.desc(),GOODS_LABEL.CREATE_TIME.desc())
            .fetchInto(GoodsLabelSelectListVo.class);
    }

    /**
     * 获取所有标签列表
     *
     * @return
     */
    public List<GoodsLabelSelectListVo> getGoodsLabelSelectList() {
        List<GoodsLabelRecord> labelRecords = getGoodsLabelsDao();
        return labelRecords.stream().map(x -> x.into(GoodsLabelSelectListVo.class)).collect(Collectors.toList());
    }

    private List<GoodsLabelRecord> getGoodsLabelsDao() {
        return db().select().from(GOODS_LABEL)
            .where(GOODS_LABEL.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .fetch().into(GoodsLabelRecord.class);
    }

    /**
     * 获取当前店铺所有未删除的标签的相关信息
     *
     * @return List<GoodsLabelRecord>
     */
    public List<GoodsLabelRecord> getByCondition(Condition condition) {
        return db()
            .select(
                GOODS_LABEL.ID,
                GOODS_LABEL.NAME,
                GOODS_LABEL.LIST_PATTERN,
                GOODS_LABEL.GOODS_DETAIL,
                GOODS_LABEL.GOODS_LIST,
                GOODS_LABEL.GOODS_SELECT,
                GOODS_LABEL.CREATE_TIME,
                GOODS_LABEL.LEVEL
            )
            .from(GOODS_LABEL)
            .where(condition)
            .fetch().into(GOODS_LABEL);
    }

    /**
     * 根据商品id、商家分类、平台分类和包含所有商品的商品标签</p>
     * 来倒推处这些商品的商品标签
     *
     * @param goodsIds    商品id
     * @param sortIds     商家分类
     * @param categoryIds 平台分类
     * @return Map:K->标签类型,V->标签信息
     */
    public Map<Byte, List<GoodsLabelAndCouple>> getGoodsLabelByFilter(List<Integer> goodsIds, List<Integer> sortIds,
                                                                      List<Integer> categoryIds) {
        return db()
            .select(
                GOODS_LABEL_COUPLE.TYPE,
                GOODS_LABEL_COUPLE.LABEL_ID,
                GOODS_LABEL_COUPLE.GTA_ID,
                GOODS_LABEL.LEVEL,
                GOODS_LABEL.CREATE_TIME
             )
            .from(GOODS_LABEL_COUPLE)
            .leftJoin(GOODS_LABEL).on(GOODS_LABEL.ID.eq(GOODS_LABEL_COUPLE.LABEL_ID))
            .where(assemblyGoodsLabelFilter(goodsIds, sortIds, categoryIds))
            .and(GOODS_LABEL.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .fetchGroups(GOODS_LABEL_COUPLE.TYPE, GoodsLabelAndCouple.class);
    }

    /**
     * 封装不同类型的标签id以及他的类型
     *
     * @param goodsIds    商品id
     * @param sortIds     商家分类
     * @param categoryIds 平台分类
     * @return Condition 标签的类型以及指定商品相关的过滤条件
     */
    private Condition assemblyGoodsLabelFilter(List<Integer> goodsIds,
                                               List<Integer> sortIds,
                                               List<Integer> categoryIds) {
        Condition condition = GOODS_LABEL_COUPLE.TYPE.eq(GoodsLabelCoupleTypeEnum.ALLTYPE.getCode());
        for (GoodsLabelCoupleTypeEnum e : GoodsLabelCoupleTypeEnum.values()) {
            List<Integer> ids;
            if (e.getCode().equals(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode())) {
                ids = goodsIds;
            } else if (e.getCode().equals(GoodsLabelCoupleTypeEnum.SORTTYPE.getCode())) {
                ids = sortIds;
            } else if (e.getCode().equals(GoodsLabelCoupleTypeEnum.CATTYPE.getCode())) {
                ids = categoryIds;
            } else {
                break;
            }
            condition = condition.or(GOODS_LABEL_COUPLE.TYPE.eq(e.getCode())
                .and(GOODS_LABEL_COUPLE.GTA_ID.in(ids)));
        }
        return condition;
    }

    public boolean exist(Integer id) {
        return db().fetchExists(GOODS_LABEL, GOODS_LABEL.ID.eq(id).and(GOODS_LABEL.DEL_FLAG.eq(BYTE_ZERO)));
    }

    public void insertOnly(GoodsLabelAddAndUpdateParam param) {
        goodsLabelDao.insertOnly(param);
    }

    public void insertChronicLabelData(){
        List<String> chronicNames = goodsChronicCoupleDao.listChronicNames();
        GoodsLabelAddAndUpdateParam goodsLabelAddAndUpdateParam = new GoodsLabelAddAndUpdateParam();
        goodsLabelAddAndUpdateParam.setIsChronic(IS_CHRONIC);
        for(String chronicName:chronicNames) {
            goodsLabelAddAndUpdateParam.setName(chronicName);
            insertOnly(goodsLabelAddAndUpdateParam);
        }
        List<GoodsLabelBase> chronicLabels = goodsLabelDao.listChronicLabels();
        List<Integer> goodsIds = new ArrayList<>();
        for (GoodsLabelBase chronicLabel:chronicLabels) {
            goodsIds = goodsChronicCoupleDao.listGoodsChronicGoodsCouples(chronicLabel.getName());
            goodsLabelAddAndUpdateParam.setId(chronicLabel.getId());
            goodsLabelAddAndUpdateParam.setGoodsIds(goodsIds);
            insertGoodsLabelCouple(goodsLabelAddAndUpdateParam);
        }
    }

    public List<GoodsLabelBase> listChronicLabels(){
        return goodsLabelDao.listChronicLabels();
    }

    public GoodsLabelBase getLabelInfo(Integer labelId){
        return goodsLabelDao.getLabelInfo(labelId);
    }
}
