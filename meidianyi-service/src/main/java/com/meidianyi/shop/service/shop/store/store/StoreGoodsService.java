package com.meidianyi.shop.service.shop.store.store;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.shop.store.StoreGoodsDao;
import com.meidianyi.shop.db.main.tables.records.TaskJobMainRecord;
import com.meidianyi.shop.db.shop.tables.records.StoreGoodsRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.category.SysCatevo;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.pos.PosSyncGoodsPrdParam;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.shop.store.goods.*;
import com.meidianyi.shop.service.saas.categroy.SysCatServiceHelper;
import com.meidianyi.shop.service.saas.schedule.TaskJobMainService;
import org.jooq.*;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.GoodsSpecProduct.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.tables.StoreGoods.STORE_GOODS;

/**
 * @author 王兵兵
 *
 * 2019年7月12日
 */
@Service

public class StoreGoodsService extends ShopBaseService{

    public static final Byte ON_SALE = 1;
    public static final Byte OFF_SALE = 0;
    public static final Byte SYNC = 1;
    public static final Byte NOT_SYNC = 0;
    public static final String UPDATE_IS_COMPILE = "storeGoodsUpdateInfo:";
    private static final Integer EXPIRE_TIME = 60;
    @Autowired
    public JedisManager jedis;
    @Autowired
    protected TaskJobMainService taskJobMainService;
    @Autowired
    private StoreGoodsDao storeGoodsDao;

    /**
     * 从店铺拉取数据更新门店内的商品信息(和pos对接无关)
     */
    public void updateGoodsDataFromShop(StoreGoodsUpdateTimeParam param) {
        Objects.requireNonNull(param.getStoreId(), "[门店商品][商品同步]门店id不能为null");
        String storeId = param.getStoreId().toString();
        String startTime = Optional.ofNullable(param.getUpdateBegin()).isPresent() ? param.getUpdateBegin().toString() : "";
        String endTime = Optional.ofNullable(param.getUpdateEnd()).isPresent() ? param.getUpdateEnd().toString() : "";
        logger().info("[门店商品][商品同步][{}]---开始执行", param.getStoreId());
        List<StoreGoods> storeGoodsList = storeGoodsDao.selectMainGoods(param);
        logger().info("[门店商品][商品同步][{}]---筛选条件{}下当前店铺一共有{}个规格商品",
            param.getStoreId(), startTime + "--" + endTime, storeGoodsList.size());

        List<Integer> goodsPrdIdsForUpdate = storeGoodsDao.selectGoodsPrdIdsForUpdate(param);
        logger().info("[门店商品][商品同步][{}]---当前门店现有{}个规格商品", param.getStoreId(), goodsPrdIdsForUpdate.size());

        // TODO:拉取药房药品信息入库

        List<StoreGoodsRecord> recordsForInsert = new ArrayList<>(storeGoodsList.size());
        List<StoreGoodsRecord> recordsForUpdate = new ArrayList<>(goodsPrdIdsForUpdate.size());

        // TODO:商品入库

    }

    /**
     * 添加更新商品任务到队列
     */
    public void addUpdateGoodsTaskFromShop(StoreGoodsUpdateTimeParam param) {
        UpdateStoreGoodsMqParam mqParam = new UpdateStoreGoodsMqParam();
        mqParam.setShopId(getShopId());
        mqParam.setParam(param);
        //调用消息队列进行执行
        Integer taskJobId = saas.taskJobMainService.dispatchImmediately(mqParam, mqParam.getClass().getName(), getShopId(),
            TaskJobsConstant.TaskJobEnum.STORE_UPDATE_JOB.getExecutionType());
        jedis.set(UPDATE_IS_COMPILE + getShopId() + ":" + param.getStoreId(), taskJobId.toString(), EXPIRE_TIME);
    }

    /**
     * 判断门店商品更新队列是否完成
     */
    public Boolean judgeQueueIsCompile(Integer storeId) {
        String key = UPDATE_IS_COMPILE + getShopId() + ":" + storeId;
        //判断当前门店id是否存在
        String value = jedis.get(key);
        if (value != null && !StringUtils.isBlank(jedis.get(key))) {
            //判断当前门店指定队列是否完成
            TaskJobMainRecord record = taskJobMainService.getTaskJobMainRecordById(Integer.valueOf(jedis.get(key))).into(TaskJobMainRecord.class);
            if (record != null && record.getStatus().equals(TaskJobsConstant.STATUS_COMPLETE)) {
                jedis.delete(key);
                return true;
            }
            return false;
        }
        return true;
    }

	/**
	 * 门店商品列表分页查询
	 * @param param 数据查询过滤条件
	 * @return StorePageListVo 分页结果
	 */
	public PageResult<StoreGoodsListQueryVo> getPageList(StoreGoodsListQueryParam param) {
		SelectWhereStep<? extends Record> select = db().
				select(
				GOODS.GOODS_IMG,GOODS.GOODS_NAME,GOODS.CAT_ID,STORE_GOODS.IS_SYNC,GOODS_SPEC_PRODUCT.PRD_PRICE,GOODS_SPEC_PRODUCT.PRD_NUMBER,
				GOODS_SPEC_PRODUCT.PRD_DESC,STORE_GOODS.IS_ON_SALE,STORE_GOODS.PRODUCT_PRICE,STORE_GOODS.PRODUCT_NUMBER,STORE_GOODS.PRD_ID,STORE_GOODS.PRD_SN,GOODS_SPEC_PRODUCT.PRD_CODES
				).
				from(STORE_GOODS).
				leftJoin(GOODS_SPEC_PRODUCT).on(STORE_GOODS.PRD_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID)).
				leftJoin(GOODS).on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID));
		select = this.buildOptions(select, param);
		select.where(STORE_GOODS.STORE_ID.eq(param.getStoreId())).orderBy(STORE_GOODS.CREATE_TIME);
        PageResult<StoreGoodsListQueryVo> pageResult = getPageResult(select, param.getCurrentPage(), param.getPageRows(), StoreGoodsListQueryVo.class);
        // 处理平台分类信息
        List<SysCatevo> sysCate = SysCatServiceHelper.getAllSysCateVoByCat();
        Map<Integer, String> sysCateMap = sysCate.stream().collect(Collectors.toMap(SysCatevo::getCatId, SysCatevo::getCatName));
        pageResult.dataList.forEach(vo -> {
            vo.setCatName(sysCateMap.get(vo.getCatId()));
            vo.setGoodsImg(getImgFullUrlUtil(vo.getGoodsImg()));
        });

        return pageResult;
    }

	/**
	 * 条件查询
	 * @param select
	 * @param param
	 * @return
	 */
	public SelectWhereStep<? extends Record> buildOptions(SelectWhereStep<? extends  Record> select, StoreGoodsListQueryParam param) {
		if (param == null) {
			return select;
		}
		if (param.getIsOnSale() != null) {
			select.where(STORE_GOODS.IS_ON_SALE.eq(param.getIsOnSale()));
		}
		if (param.getIsSync() != null) {
			select.where(STORE_GOODS.IS_SYNC.eq(param.getIsSync()));
		}
		if (param.getCatId() != null && param.getCatId() > 0) {
			List<Integer> allCatId = saas().sysCate.findChildrenByParentId(param.getCatId());
			select.where(GOODS.CAT_ID.in(allCatId));
		}
		if (!StringUtils.isEmpty(param.getKeywords())) {
			select.where(GOODS.GOODS_NAME.contains(param.getKeywords()).or(GOODS_SPEC_PRODUCT.PRD_CODES.eq(param.getKeywords())));
		}
		return select;
	}

	public void saveExternalStoreInfo(StoreGoods storeGoods){
        StoreGoods existStoreGoods = storeGoodsDao.getStoreGoodsByMedicalKey(storeGoods.getMedicalKey(), storeGoods.getStoreId());

        if (existStoreGoods == null) {
            storeGoodsDao.batchInsert(Collections.singletonList(storeGoods));
        } else {
            storeGoods.setId(existStoreGoods.getId());
            storeGoodsDao.batchUpdate(Collections.singletonList(storeGoods));
        }
    }

    /**
     * 批量同步门店商品数据
     * @param storeGoodsList
     */
    public void batchSyncStoreGoods(List<StoreGoods> storeGoodsList) {
        if (storeGoodsList.size() == 0) {
            return;
        }
        List<Integer> goodsIds = storeGoodsList.stream().map(StoreGoods::getGoodsId).collect(Collectors.toList());
        Set<Integer> existStoreGoodsSet = new HashSet<>(storeGoodsDao.selectExistStoreGoodsIds(goodsIds, storeGoodsList.get(0).getStoreId()));

        List<StoreGoods> readyToInsert = new ArrayList<>();
        List<StoreGoods> readyToUpdate = new ArrayList<>();

        for (StoreGoods storeGoods : storeGoodsList) {
            if (existStoreGoodsSet.contains(storeGoods.getGoodsId())) {
                readyToUpdate.add(storeGoods);
            } else {
                readyToInsert.add(storeGoods);
            }
        }

        storeGoodsDao.batchInsert(readyToInsert);
        storeGoodsDao.batchUpdate(readyToUpdate);
    }
    /**
     * 门店商品-上架
     * @param param
     */
    public void storeGoodsPutOnSale(StoreGoodsUpdateParam param) {
        db().update(STORE_GOODS).set(STORE_GOODS.IS_ON_SALE,ON_SALE).where(STORE_GOODS.PRD_ID.in(param.getPrdId()).and(STORE_GOODS.STORE_ID.eq(param.getStoreId()))).execute();
    }

    /**
     * 门店商品-下架
     * @param param
     */
    public void storeGoodsPutOffSale(StoreGoodsUpdateParam param) {
        db().update(STORE_GOODS).set(STORE_GOODS.IS_ON_SALE,OFF_SALE).where(STORE_GOODS.PRD_ID.in(param.getPrdId()).and(STORE_GOODS.STORE_ID.eq(param.getStoreId()))).execute();
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
            return saas.shop.image.imageUrl(relativePath);
        }
    }

    public void updateMatchedExternalStoreGoodsInfos(String storeCode,BigDecimal price,Integer goodsId,Integer prdId){
        db().update(STORE_GOODS)
            .set(STORE_GOODS.GOODS_ID,goodsId)
            .set(STORE_GOODS.PRD_ID,prdId)
            .set(STORE_GOODS.IS_ON_SALE, MedicalGoodsConstant.ON_SALE)
            .set(STORE_GOODS.PRODUCT_PRICE,price)
            .where(STORE_GOODS.GOODS_STORE_SN.eq(storeCode))
            .execute();
    }

    /**
     * pos 同步数据批量更新
     * @param storeId 门店id
     * @param goodsPrdParams 待修改数据集合
     */
    public void batchUpdateForSyncPosProduct(Integer storeId, List<PosSyncGoodsPrdParam> goodsPrdParams) {
        if (goodsPrdParams.size()==0) {
            return;
        }
        PosSyncGoodsPrdParam item = goodsPrdParams.get(0);
        DSLContext db = db();
        String sql = db.update(STORE_GOODS)
            .set(STORE_GOODS.IS_ON_SALE,item.getIsOnSale())
            .set(STORE_GOODS.PRODUCT_PRICE,item.getPrdPrice())
            .set(STORE_GOODS.IS_SYNC,SYNC).where(STORE_GOODS.STORE_ID.eq(storeId).and(STORE_GOODS.PRD_ID.eq(item.getPrdId())))
            .getSQL();

        Query query = db.query(sql);
        BatchBindStep batchStep = db.batch(query);
        int addedCount = 0;
        final int batchCount = 500;

        for (int i = 0; i < goodsPrdParams.size(); i++) {
            item = goodsPrdParams.get(i);
            addedCount++;
            batchStep = batchStep.bind(item.getIsOnSale(), item.getPrdPrice(),SYNC, storeId, item.getPrdId());

            if (addedCount == batchCount) {
                batchStep.execute();
                batchStep = db.batch(query);
                addedCount = 0;
            }
        }
        batchStep.execute();
    }

    /**
     * 跟新门店商品规格库存
     * @param storeId 门店id
     * @param prdId 规格id
     * @param number 规格数量
     */
    public void updatePrdNumForPosSyncStock(Integer storeId, Integer prdId,Integer number) {
        db().update(STORE_GOODS).set(STORE_GOODS.PRODUCT_NUMBER,number)
            .where(STORE_GOODS.PRD_ID.eq(prdId).and(STORE_GOODS.STORE_ID.eq(storeId)))
            .execute();
    }

    /***************门店后端商品功能代码*****************/

    /**
     * 门店商品分页查询
     * @param param
     * @return
     */
    public PageResult<StoreGoodsListQueryVo> getGoodsPageList(StoreGoodsListQueryParam param) {
        return storeGoodsDao.getGoodsPageList(param);
    }

    /**
     * 查询该商品在哪家门店上架
     * @param storeGoodsBaseCheckInfoList 商品列表
     * @return List<Integer>
     */
    public List<String> checkStoreGoodsIsOnSale(List<StoreGoodsBaseCheckInfo> storeGoodsBaseCheckInfoList) {
        return storeGoodsDao.checkStoreGoodsIsOnSale(storeGoodsBaseCheckInfoList);
    }
}
