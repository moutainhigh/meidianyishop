package com.meidianyi.shop.service.shop.goods;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.foundation.util.medical.DateFormatStr;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.common.pojo.shop.table.GoodsMedicalInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsExternalDo;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsPageListCondition;
import com.meidianyi.shop.dao.shop.goods.GoodsExternalDao;
import com.meidianyi.shop.dao.shop.goods.GoodsMedicalInfoDao;
import com.meidianyi.shop.dao.shop.sort.SortDao;
import com.meidianyi.shop.dao.shop.store.StoreDao;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLock;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockKeys;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsMatchParam;
import com.meidianyi.shop.service.pojo.shop.medical.brand.vo.GoodsBrandVo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.shop.medical.goods.bo.GoodsMedicalExternalRequestBo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.bo.GoodsMedicalExternalRequestItemBo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.bo.GoodsMedicalExternalStoreRequestBo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.convertor.GoodsConverter;
import com.meidianyi.shop.service.pojo.shop.medical.goods.entity.GoodsEntity;
import com.meidianyi.shop.service.pojo.shop.medical.goods.param.*;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsDetailVo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsPageListVo;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsPrdVo;
import com.meidianyi.shop.service.pojo.shop.medical.label.MedicalLabelConstant;
import com.meidianyi.shop.service.pojo.shop.medical.label.bo.LabelRelationInfoBo;
import com.meidianyi.shop.service.pojo.shop.medical.label.vo.GoodsLabelVo;
import com.meidianyi.shop.service.pojo.shop.medical.sku.entity.GoodsSpecProductEntity;
import com.meidianyi.shop.service.pojo.shop.medical.sku.vo.GoodsSpecProductDetailVo;
import com.meidianyi.shop.service.pojo.shop.medical.sku.vo.GoodsSpecProductGoodsPageListVo;
import com.meidianyi.shop.service.pojo.shop.medical.sku.vo.SpecVo;
import com.meidianyi.shop.service.pojo.shop.medical.sort.vo.GoodsSortVo;
import com.meidianyi.shop.service.pojo.shop.store.goods.StoreGoods;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;
import com.meidianyi.shop.service.shop.goods.aggregate.GoodsAggregate;
import com.meidianyi.shop.service.shop.store.store.StoreGoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Service
public class MedicalGoodsService extends ShopBaseService {

    @Autowired
    private GoodsAggregate goodsAggregate;
    @Autowired
    private GoodsMedicalInfoDao goodsMedicalInfoDao;
    @Autowired
    private SortDao sortDao;
    @Autowired
    private StoreDao storeDao;

    @Autowired
    private MedicalGoodsSpecProductService medicalGoodsSpecProductService;
    @Autowired
    private MedicalGoodsLabelService medicalGoodsLabelService;
    @Autowired
    private MedicalGoodsSortService medicalGoodsSortService;
    @Autowired
    private MedicalGoodsBrandService medicalGoodsBrandService;
    @Autowired
    private MedicalGoodsImageService medicalGoodsImageService;
    @Autowired
    private GoodsSpecProductService goodsSpecProductService;
    @Autowired
    private StoreGoodsService storeGoodsService;
    @Autowired
    private GoodsExternalDao goodsExternalDao;

    /**
     * 新增
     * @param shopId
     * @param goodsEntity
     */
    @RedisLock(prefix = JedisKeyConstant.GOODS_LOCK)
    public void insert(@RedisLockKeys Integer shopId, GoodsEntity goodsEntity) {
        // 验证goodsSn是否重复
        if (StrUtil.isBlank(goodsEntity.getGoodsSn())) {
            goodsEntity.setGoodsSn(generateGoodsSn());
        } else {
            if (goodsAggregate.isGoodsSnExist(goodsEntity.getGoodsSn(), null)) {
                throw new IllegalArgumentException("商品goodsSn重复");
            }
        }
        // 处理自己的价格
        goodsEntity.calculateGoodsPriceWeight();
        goodsAggregate.insert(goodsEntity);
        // 处理sku
        if (goodsEntity.getGoodsSpecs() != null && goodsEntity.getGoodsSpecs().size() > 0) {
            // 先插入规格组，协助sku生成其规格描述id字符串
            medicalGoodsSpecProductService.batchSpecInsert(goodsEntity.getGoodsSpecs(), goodsEntity.getGoodsId());
        }
        goodsEntity.calculateSkuPrdSpecsBySpecs();
        medicalGoodsSpecProductService.batchSkuInsert(goodsEntity.getGoodsSpecProducts(), goodsEntity.getGoodsId());

        // 处理标签、图片额外信息
        List<Integer> labelIds = goodsEntity.getLabelIds();
        if (labelIds != null && labelIds.size() > 0) {
            medicalGoodsLabelService.batchInsertGoodsCouple(labelIds, goodsEntity.getGoodsId());
        }

        // 处理图片
        if (goodsEntity.getImgPaths() != null && goodsEntity.getImgPaths().size() > 0) {
            medicalGoodsImageService.batchInsertGoodsImageRelation(goodsEntity.getImgPaths(), goodsEntity.getGoodsId());
        }
    }

    /**
     * 修改
     * @param shopId
     * @param goodsEntity
     */
    @RedisLock(prefix = JedisKeyConstant.GOODS_LOCK)
    public void update(@RedisLockKeys Integer shopId, GoodsEntity goodsEntity) {
        Integer goodsId = goodsEntity.getGoodsId();
        // 验证goodsSn是否重复
        if (StrUtil.isBlank(goodsEntity.getGoodsSn())) {
            goodsEntity.setGoodsSn(generateGoodsSn());
        } else {
            if (goodsAggregate.isGoodsSnExist(goodsEntity.getGoodsSn(), goodsEntity.getGoodsId())) {
                throw new IllegalArgumentException("商品goodsSn重复");
            }
        }
        // 处理自己的价格
        goodsEntity.calculateGoodsPriceWeight();
        goodsAggregate.update(goodsEntity);

        // 删除旧的spec组
        medicalGoodsSpecProductService.deleteSpecByGoodsId(goodsId);
        // 处理sku
        if (goodsEntity.getGoodsSpecs() != null && goodsEntity.getGoodsSpecs().size() > 0) {
            // 先插入规格组，协助sku生成其规格描述id字符串
            medicalGoodsSpecProductService.batchSpecInsert(goodsEntity.getGoodsSpecs(), goodsId);
        }
        goodsEntity.calculateSkuPrdSpecsBySpecs();
        // 处理需要修改的sku
        List<GoodsSpecProductEntity> goodsSpecProductEntities = GoodsSpecProductEntity.filterSkuForUpdateOrInsert(goodsEntity.getGoodsSpecProducts(), true);
        medicalGoodsSpecProductService.batchSkuUpdate(goodsSpecProductEntities);
        // 处理需要新增的sku
        goodsSpecProductEntities = GoodsSpecProductEntity.filterSkuForUpdateOrInsert(goodsEntity.getGoodsSpecProducts(), false);
        medicalGoodsSpecProductService.batchSkuInsert(goodsSpecProductEntities, goodsId);

        // 处理标签
        medicalGoodsLabelService.deleteGoodsCouples(goodsId);
        List<Integer> labelIds = goodsEntity.getLabelIds();
        if (labelIds != null && labelIds.size() > 0) {
            medicalGoodsLabelService.batchInsertGoodsCouple(labelIds, goodsId);
        }

        // 处理图片
        medicalGoodsImageService.deleteGoodsImageRelation(goodsId);
        if (goodsEntity.getImgPaths() != null && goodsEntity.getImgPaths().size() > 0) {
            medicalGoodsImageService.batchInsertGoodsImageRelation(goodsEntity.getImgPaths(), goodsId);
        }
    }


    /**
     * 删除药品
     * @param goodsId
     */
    public void deleteByGoodsId(Integer goodsId) {
        goodsAggregate.deleteGoodsById(goodsId);
        medicalGoodsSpecProductService.deleteSkuByGoodsId(goodsId);
        medicalGoodsSpecProductService.deleteSpecByGoodsId(goodsId);
        medicalGoodsLabelService.deleteGoodsCouples(goodsId);
        medicalGoodsImageService.deleteGoodsImageRelation(goodsId);
    }

    /**
     * 根据商品id搜索商品信息
     * @param goodsId 商品id
     */
    public GoodsDetailVo getGoodsDetailByGoodsId(Integer goodsId) {
        GoodsDetailVo goodsDetailVo = goodsAggregate.getByGoodsId(goodsId);
        if (goodsDetailVo == null) {
            return null;
        }
        //设置sku
        List<GoodsSpecProductDetailVo> skus = medicalGoodsSpecProductService.listSkuDetailByGoodsId(goodsId);
        goodsDetailVo.setGoodsSpecProducts(skus);

        //设置规格组
        if (!MedicalGoodsConstant.DEFAULT_SKU.equals(goodsDetailVo.getIsDefaultProduct())) {
            List<SpecVo> specVos = medicalGoodsSpecProductService.listSpecByGoodsId(goodsId);
            goodsDetailVo.setGoodsSpecs(specVos);
        }

        // 品牌设置
        GoodsBrandVo goodsBrandVo = medicalGoodsBrandService.getGoodsBrandById(goodsDetailVo.getBrandId());
        if (goodsBrandVo != null) {
            goodsDetailVo.setBrandName(goodsBrandVo.getBrandName());
        }

        List<Integer> parentsSortIds = sortDao.getParentSortIds(goodsDetailVo.getSortId());
        parentsSortIds.add(goodsDetailVo.getSortId());
        // 标签设置
        List<GoodsLabelVo> goodsNormalLabels = medicalGoodsLabelService.listSortIdsRelatedLabels(parentsSortIds);
        List<GoodsLabelVo> allRelatedLabels = medicalGoodsLabelService.listAllRelatedLabels();
        goodsNormalLabels.addAll(allRelatedLabels);
        List<GoodsLabelVo> goodsPointLabels = medicalGoodsLabelService.listGoodsIdRelatedLabels(goodsId);

        goodsDetailVo.setPointLabels(goodsPointLabels);
        goodsDetailVo.setNormalLabels(goodsNormalLabels);

        List<String> imgPaths = medicalGoodsImageService.listGoodsImages(goodsId);
        goodsDetailVo.setGoodsImgs(imgPaths);

        return goodsDetailVo;
    }

    /**
     * 分页查询商品信息
     * @param pageListParam
     */
    public PageResult<GoodsPageListVo> getGoodsPageList(MedicalGoodsPageListParam pageListParam) {
        GoodsPageListCondition goodsPageListCondition = GoodsConverter.convertPageListConditionFromPageListParam(pageListParam);
        if (pageListParam.getSortId() != null) {
            List<Integer> parentsSortIds = sortDao.getParentSortIds(pageListParam.getSortId());
            parentsSortIds.add(pageListParam.getSortId());
            goodsPageListCondition.setSortIds(parentsSortIds);
        }

        /**
         * 标签打在分类上时要单独插入子分类关联
         */
        if (pageListParam.getLabelId() != null) {
            LabelRelationInfoBo labelRelationInfo = medicalGoodsLabelService.getLabelRelationInfo(pageListParam.getLabelId());
            if (labelRelationInfo.getIsAll() != null) {
                if (labelRelationInfo.getSortIds() != null) {
                    goodsPageListCondition.getSortIds().addAll(labelRelationInfo.getSortIds());
                }
                if (labelRelationInfo.getGoodsIds() != null) {
                    goodsPageListCondition.setGoodsIdsLimit(labelRelationInfo.getGoodsIds());
                }
            }
        }
        PageResult<GoodsEntity> goodsEntityPageResult = goodsAggregate.getGoodsPageList(goodsPageListCondition, pageListParam.getCurrentPage(), pageListParam.getPageRows());
        List<GoodsPageListVo> goodsPageListVos = new ArrayList<>(goodsEntityPageResult.getDataList().size());
        List<Integer> goodsIds = new ArrayList<>(goodsEntityPageResult.getDataList().size());
        List<Integer> sortIds = new ArrayList<>(goodsEntityPageResult.getDataList().size());
        List<Integer> storeGoodsIds = new ArrayList<>(goodsEntityPageResult.getDataList().size());
        List<Integer> hisGoodsIds = new ArrayList<>(goodsEntityPageResult.getDataList().size());
        for (GoodsEntity goodsEntity : goodsEntityPageResult.getDataList()) {
            GoodsPageListVo goodsPageListVo = new GoodsPageListVo(goodsEntity);
            goodsPageListVos.add(goodsPageListVo);
            goodsIds.add(goodsEntity.getGoodsId());
            sortIds.add(goodsEntity.getSortId());
            hisGoodsIds.add(goodsEntity.getFromHisId());
            storeGoodsIds.add(goodsEntity.getFromStoreId());
        }
        PageResult<GoodsPageListVo> retPageResult = new PageResult<>();
        retPageResult.setPage(goodsEntityPageResult.getPage());
        retPageResult.setDataList(goodsPageListVos);
        // 准备需要映射的规格信息
        Map<Integer, GoodsExternalDo> externalHisInfoByHisIds = goodsExternalDao.getExternalHisInfoByHisIds(hisGoodsIds);
        Map<Integer, GoodsExternalDo> externalStoreInfoByStoreIds = goodsExternalDao.getExternalStoreInfoByStoreId(storeGoodsIds);
        Map<Integer, List<GoodsSpecProductGoodsPageListVo>> goodsIdSkusMap = medicalGoodsSpecProductService.groupSkuSimpleByGoodsIds(goodsIds);
        //准备标签数据
        Map<Integer, List<GoodsLabelVo>> goodsIdLabelsMap = medicalGoodsLabelService.mapGtaToLabel(goodsIds, MedicalLabelConstant.GTA_GOODS);
        Map<Integer, List<GoodsLabelVo>> sortIdLabelsMap = medicalGoodsLabelService.mapGtaToLabel(sortIds, MedicalLabelConstant.GTA_SORT);
        List<GoodsLabelVo> allGoodsLabels = medicalGoodsLabelService.listAllRelatedLabels();
        // 准备分类数据
        Map<Integer, GoodsSortVo> goodsSortVosIdMap = medicalGoodsSortService.getGoodsSortVosIdMap(sortIds);
        for (GoodsPageListVo goodsPageListVo : goodsPageListVos) {
            List<GoodsSpecProductGoodsPageListVo> goodsSpecProductGoodsPageListVos = goodsIdSkusMap.get(goodsPageListVo.getGoodsId());
            List<GoodsLabelVo> goodsPointLabels = goodsIdLabelsMap.get(goodsPageListVo.getGoodsId());
            List<GoodsLabelVo> goodsSortLabels = sortIdLabelsMap.get(goodsPageListVo.getSortId());
            GoodsSortVo goodsSortVo = goodsSortVosIdMap.get(goodsPageListVo.getSortId());

            goodsPageListVo.setGoodsSpecProducts(goodsSpecProductGoodsPageListVos);

            if (goodsSortVo != null) {
                goodsPageListVo.setSortName(goodsSortVo.getSortName());
            }
            if (goodsPointLabels != null) {
                goodsPageListVo.getGoodsPointLabels().addAll(goodsPointLabels);
            }
            if (goodsSortLabels != null) {
                goodsPageListVo.getGoodsNormalLabels().addAll(goodsSortLabels);
            }
            if (allGoodsLabels != null) {
                goodsPageListVo.getGoodsNormalLabels().addAll(allGoodsLabels);
            }
            if (externalHisInfoByHisIds.get(goodsPageListVo.getFromHisId()) != null) {
                goodsPageListVo.setHisPrice(externalHisInfoByHisIds.get(goodsPageListVo.getFromHisId()).getGoodsPrice());
            }
            if (externalStoreInfoByStoreIds.get(goodsPageListVo.getFromStoreId()) != null) {
                goodsPageListVo.setStorePrice(externalStoreInfoByStoreIds.get(goodsPageListVo.getFromStoreId()).getGoodsPrice());
            }
        }
        return retPageResult;
    }

    /**
     * 当前时间
     * 生成商品GoodsSn
     * @return
     */
    private String generateGoodsSn() {
        int count = goodsAggregate.countAllGoods();
        String nowStr = DateUtil.format(new DateTime(), DateFormatStr.DATE_FORMAT_FULL_COMPACT);
        return String.format("G%s-%08d", nowStr, count);
    }


    public void batchOperate(MedicalGoodsBatchOperateParam param) {
        goodsAggregate.batchOperate(param);
    }

    /**
     * 获取商品医药信息
     * @param goodsId 商品id
     * @return GoodsMedicalInfo or null
     * @author 孔德成
     */
    public GoodsMedicalInfoDo getByGoodsId(Integer goodsId) {
        return goodsMedicalInfoDao.getByGoodsId(goodsId);
    }


    private JsonResult createJsonResultByApiExternalRequestResult(ApiExternalRequestResult apiExternalRequestResult) {
        JsonResult result = new JsonResult();
        result.setError(apiExternalRequestResult.getError());
        result.setMessage(apiExternalRequestResult.getMsg());
        result.setContent(apiExternalRequestResult.getData());
        logger().debug("拉取药品信息错误：error " + apiExternalRequestResult.getError() + ",msg " + apiExternalRequestResult.getMsg());
        return result;
    }

    private JsonResult createJsonResultByApiExternalRequestResult(ApiExternalRequestResult apiExternalRequestResult, String storeCode) {
        JsonResult result = new JsonResult();
        result.setError(apiExternalRequestResult.getError());
        result.setMessage(apiExternalRequestResult.getMsg());
        result.setContent(apiExternalRequestResult.getData());
        logger().error("拉取药房：" + storeCode + " 商品信息错误：error " + apiExternalRequestResult.getError() + ",msg " + apiExternalRequestResult.getMsg());
        return result;
    }

    /**
     * 药品上下架控制方式
     * 如果store_code不为null，则表明是从药房拉取到了的数据，
     * 如果his_staus不为null,则表明是从his拉取到了的数据，
     * 两者都不为null，则表明从药房和his都拉取到了数据并匹配上，
     * store_status字段暂时未使用，具体药房商品状态在门店商品表内存储。
     * @return
     */
    @SuppressWarnings("all")
    public JsonResult fetchExternalMedicalInfo() {
        String appId = ApiExternalRequestConstant.APP_ID_HIS;
        Integer shopId = getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_FETCH_MEDICAL_INFOS;
        Long lastRequestTime = saas().externalRequestHistoryService.getLastRequestTime(ApiExternalRequestConstant.APP_ID_HIS, shopId, ApiExternalRequestConstant.SERVICE_NAME_FETCH_MEDICAL_INFOS);
        MedicalGoodsExternalRequestParam param = new MedicalGoodsExternalRequestParam();
        if (lastRequestTime == null) {
            Timestamp startTime = DateUtils.convertToTimestamp(MedicalGoodsConstant.PULL_START_TIME);
            lastRequestTime = startTime.getTime() / 1000;
        }
        param.setStartTime(null);
        Timestamp now = DateUtils.getLocalDateTime();
        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));
        // 数据拉取错误
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())) {
            saas().externalRequestHistoryService.eraseRequestHistory(appId, shopId, serviceName, now);
            return createJsonResultByApiExternalRequestResult(apiExternalRequestResult);
        }
        String dataJson = apiExternalRequestResult.getData();
        GoodsMedicalExternalRequestBo goodsMedicalExternalRequestBo = Util.parseJson(dataJson, GoodsMedicalExternalRequestBo.class);
        if (goodsMedicalExternalRequestBo == null) {
            logger().error("拉取his反序列化错误，请求参数：" + param + " 商品信息反序列化错误：" + dataJson);
            JsonResult result = new JsonResult();
            result.setError(ApiExternalRequestConstant.ERROR_CODE_PARSE_RETVAL);
            return result;
        }
        Integer pullCount = 0, pageSize = goodsMedicalExternalRequestBo.getPageSize(), totalCount = goodsMedicalExternalRequestBo.getTotalCount();
        Integer requestTryCount = 0;
        for (Integer curPage = 1; curPage <= pageSize; curPage++) {
            logger().debug("拉取his数据：共" + totalCount + "条，当前页：" + curPage);
            param.setCurrentPage(curPage);
            apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));
            // 数据拉取错误
            if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())) {
                if (requestTryCount < 3 && ApiExternalRequestConstant.HIS_NET_ERROR_UNEXPECTED_FILE_CODE.equals(apiExternalRequestResult.getError()) &&
                    ApiExternalRequestConstant.HIS_NET_ERROR_UNEXPECTED_FILE_MSG.equals(apiExternalRequestResult.getMsg())) {
                    try {
                        Thread.sleep(2000);
                        requestTryCount++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                } else {
                    saas().externalRequestHistoryService.eraseRequestHistory(appId, shopId, serviceName, now);
                    return createJsonResultByApiExternalRequestResult(apiExternalRequestResult);
                }
            }
            goodsMedicalExternalRequestBo = Util.parseJson(apiExternalRequestResult.getData(), GoodsMedicalExternalRequestBo.class);
            if (goodsMedicalExternalRequestBo == null) {
                logger().error("拉取his反序列化错误，请求参数：" + param + " 商品信息反序列化错误：" + apiExternalRequestResult.getData());
                continue;
            }
            // 药品数据入库操作
            try {
                saveGoodsMedicalExternalInfo(goodsMedicalExternalRequestBo.getDataList());
            } catch (Exception e) {
                e.printStackTrace();
            }
            pullCount += goodsMedicalExternalRequestBo.getDataList().size();
            pageSize = goodsMedicalExternalRequestBo.getPageSize();
            totalCount = goodsMedicalExternalRequestBo.getTotalCount();
        }
        logger().debug("拉取药品信息结束：共处理" + pullCount + "条");
        return JsonResult.success();
    }

    private void saveGoodsMedicalExternalInfo(List<GoodsMedicalExternalRequestItemBo> goodsMedicalExternalRequestItemBos) {
        // 剔除不合法说句，并处理
        goodsMedicalExternalRequestItemBos = filterHisIllegalData(goodsMedicalExternalRequestItemBos);

        for (GoodsMedicalExternalRequestItemBo bo : goodsMedicalExternalRequestItemBos) {
            GoodsExternalDo existDo = goodsExternalDao.getExternalFromHis(bo.getGoodsKeyComposedByNameQualityEnterprise());
            GoodsExternalDo goodsExternalDo = GoodsConverter.convertGoodsMedicalExternalRequestItemBoToExternalDo(bo);
            if (existDo == null) {
                // 对于数据库不存在，而数据自身状态是删除状态则不入库
                if (BaseConstant.EXTERNAL_ITEM_STATE_DELETE.equals(bo.getState())) {
                    continue;
                }
                goodsExternalDao.insertExternalFromHis(goodsExternalDo);
            } else {
                goodsExternalDo.setId(existDo.getId());
                goodsExternalDao.updateExternalFromHis(goodsExternalDo);
            }
        }
    }

    /**
     * 获取his信息不完整数据
     * @param goodsMedicalExternalRequestItemBos
     * @return
     */
    private List<GoodsMedicalExternalRequestItemBo> filterHisIllegalData(List<GoodsMedicalExternalRequestItemBo> goodsMedicalExternalRequestItemBos) {
        return goodsMedicalExternalRequestItemBos.stream().filter(x -> {
            if (StringUtils.isBlank(x.getGoodsCode())) {
                logger().info("同步药品信息错误：" + getShopId() + ":缺少药品编码-" + x.toString());
                return false;
            }
            if (StringUtils.isBlank(x.getGoodsCommonName())) {
                logger().info("同步药品信息错误：" + getShopId() + ":缺少药品名称-" + x.toString());
                return false;
            }
            if (x.getGoodsPrice() == null || x.getGoodsPrice().equals(BigDecimal.ZERO)) {
                logger().info("同步药品信息错误：" + getShopId() + ":缺少药品价格-" + x.getGoodsPrice());
                return false;
            }
            if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(x.getIsMedical())) {
                if (StringUtils.isBlank(x.getGoodsQualityRatio())) {
                    logger().info("同步药品信息错误：" + getShopId() + ":缺少药品规格系数-" + x.toString());
                    return false;
                }
                if (StringUtils.isBlank(x.getGoodsProductionEnterprise())) {
                    logger().info("同步药品信息错误：" + getShopId() + ":缺少药品生产企业-" + x.toString());
                    return false;
                }
            }

            x.setGoodsCode(x.getGoodsCode().trim());
            x.setGoodsCommonName(x.getGoodsCommonName().replaceAll("\\*", "").trim());

            if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(x.getIsMedical())) {
                x.setGoodsQualityRatio(x.getGoodsQualityRatio().trim().replaceAll("\\*", ""));
                x.setGoodsProductionEnterprise(x.getGoodsProductionEnterprise().trim().replaceAll("\\*", ""));
                x.setGoodsAliasName(x.getGoodsAliasName().trim().replaceAll("\\*", ""));
                String goodsKey = x.getGoodsCommonName() + x.getGoodsQualityRatio() + x.getGoodsProductionEnterprise();
                x.setGoodsKeyComposedByNameQualityEnterprise(goodsKey);
            } else {
                // 普通商品通过名称标识唯一
                x.setGoodsKeyComposedByNameQualityEnterprise(x.getGoodsCommonName());
            }
            if (x.getGoodsApprovalNumber() != null) {
                x.setGoodsApprovalNumber(x.getGoodsApprovalNumber().trim());
            }
            return true;
        }).collect(Collectors.toList());
    }


    /**
     * 测试指定门店和分页信息使用
     * @param param
     * @return
     */
    public ApiExternalRequestResult fetchExternalStoreTest(MedicalGoodsExternalStoreRequestParam param) {
        String appId = ApiExternalRequestConstant.APP_ID_STORE;

        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_PULL_GOODS_INFOS;
        Integer shopId = getShopId();

        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));
        GoodsMedicalExternalStoreRequestBo goodsMedicalExternalStoreRequestBo = Util.parseJson(apiExternalRequestResult.getData(), GoodsMedicalExternalStoreRequestBo.class);
        return apiExternalRequestResult;
    }

    /**
     * 迭代拉取药店商品信息
     * @return
     */
    public void fetchExternalStoresGoodsInfo() {
        String appId = ApiExternalRequestConstant.APP_ID_STORE;
        Integer shopId = getShopId();
        Long lastRequestTime = saas().externalRequestHistoryService.getLastRequestTime(appId, shopId, ApiExternalRequestConstant.SERVICE_NAME_PULL_GOODS_INFOS);
        Timestamp now = DateUtils.getLocalDateTime();
        List<StoreBasicVo> storeInfos = storeDao.listStoreCodes();

        for (StoreBasicVo storeInfo : storeInfos) {
            if (StringUtils.isBlank(storeInfo.getStoreCode())) {
                continue;
            }
            JsonResult jsonResult = fetchExternalStoreGoodsInfo(lastRequestTime, storeInfo, now, appId, shopId, ApiExternalRequestConstant.SERVICE_NAME_PULL_GOODS_INFOS);
            if (!JsonResult.success().equals(jsonResult)) {
                logger().info("门店：" + storeInfo.getStoreCode() + " 药品同步数据失败");
            }
        }
    }

    /**
     * 拉取指定药店药品信息
     * @param lastRequestTime
     * @param storeInfo
     * @param currentPullTime
     * @param appId
     * @param shopId
     * @param serviceName
     * @return
     */
    public JsonResult fetchExternalStoreGoodsInfo(Long lastRequestTime, StoreBasicVo storeInfo, Timestamp currentPullTime, String appId, Integer shopId, String serviceName) {
        MedicalGoodsExternalStoreRequestParam param = new MedicalGoodsExternalStoreRequestParam();
        param.setStartTime(lastRequestTime);
        param.setShopSn(storeInfo.getStoreCode());
        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));
        // 数据拉取错误
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())) {
            saas().externalRequestHistoryService.eraseRequestHistory(appId, shopId, serviceName, currentPullTime);
            return createJsonResultByApiExternalRequestResult(apiExternalRequestResult, storeInfo.getStoreCode());
        }
        String dataJson = apiExternalRequestResult.getData();
        GoodsMedicalExternalStoreRequestBo goodsMedicalExternalStoreRequestBo = Util.parseJson(dataJson, GoodsMedicalExternalStoreRequestBo.class);
        if (goodsMedicalExternalStoreRequestBo == null) {
            logger().error("拉取药房：" + storeInfo.getStoreCode() + " 请求参数：" + param + " 商品信息反序列化错误：" + dataJson);
            JsonResult result = new JsonResult();
            result.setError(ApiExternalRequestConstant.ERROR_CODE_PARSE_RETVAL);
            return result;
        }
        if (goodsMedicalExternalStoreRequestBo.getDataList() == null || goodsMedicalExternalStoreRequestBo.getDataList().size() == 0) {
            return JsonResult.success();
        }
        Integer pullCount = 0, pageSize = goodsMedicalExternalStoreRequestBo.getPageSize(), totalCount = goodsMedicalExternalStoreRequestBo.getTotalCount();
        for (Integer curPage = 1; curPage <= pageSize; curPage++) {
            logger().debug("拉取药房：" + storeInfo.getStoreCode() + ",共" + totalCount + "条，当前页：" + curPage);
            param.setCurrentPage(curPage);
            apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));
            // 数据拉取错误
            if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())) {
                saas().externalRequestHistoryService.eraseRequestHistory(appId, shopId, serviceName, currentPullTime);
                return createJsonResultByApiExternalRequestResult(apiExternalRequestResult, storeInfo.getStoreCode());
            }
            goodsMedicalExternalStoreRequestBo = Util.parseJson(apiExternalRequestResult.getData(), GoodsMedicalExternalStoreRequestBo.class);
            if (goodsMedicalExternalStoreRequestBo == null) {
                logger().error("拉取药房：" + storeInfo.getStoreCode() + " 请求参数：" + param + " 商品信息反序列化错误：" + apiExternalRequestResult.getData());
                continue;
            }
            // 药品数据入库操作
            try {
                saveGoodsMedicalExternalStoreInfo(goodsMedicalExternalStoreRequestBo.getDataList(), storeInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pullCount += goodsMedicalExternalStoreRequestBo.getDataList().size();
            pageSize = goodsMedicalExternalStoreRequestBo.getPageSize();
            totalCount = goodsMedicalExternalStoreRequestBo.getTotalCount();
        }

        logger().debug("拉取药店：" + storeInfo.getStoreCode() + " 商品信息结束：共处理" + pullCount + "条");
        return JsonResult.success();
    }


    @SuppressWarnings("all")
    private void saveGoodsMedicalExternalStoreInfo(List<GoodsMedicalExternalRequestItemBo> goodsMedicalExternalRequestItemBos, StoreBasicVo storeInfo) {
        goodsMedicalExternalRequestItemBos = filterStoreGoodsIllegalData(goodsMedicalExternalRequestItemBos, storeInfo.getStoreCode());

        for (GoodsMedicalExternalRequestItemBo bo : goodsMedicalExternalRequestItemBos) {
            GoodsExternalDo existDo = goodsExternalDao.getExternalFromStore(bo.getGoodsKeyComposedByNameQualityEnterprise());
            GoodsExternalDo goodsExternalDo = GoodsConverter.convertGoodsMedicalExternalRequestItemBoToExternalDo(bo);
            if (existDo == null) {
                // 对于数据库不存在，而数据自身状态是删除状态则不入库
                if (BaseConstant.EXTERNAL_ITEM_STATE_DELETE.equals(bo.getState())) {
                    continue;
                }
                goodsExternalDao.insertExternalFromStore(goodsExternalDo);
            } else {
                goodsExternalDo.setId(existDo.getId());
                goodsExternalDao.updateExternalFromStore(goodsExternalDo);
            }
            StoreGoods storeGoods = GoodsConverter.convertBoToStoreGoods(bo, storeInfo.getStoreId());
            storeGoodsService.saveExternalStoreInfo(storeGoods);
        }
    }

    /**
     * 过滤掉从his拉取的不合法数据
     * @param goodsMedicalExternalRequestItemBos
     * @return
     */
    private List<GoodsMedicalExternalRequestItemBo> filterStoreGoodsIllegalData(List<GoodsMedicalExternalRequestItemBo> goodsMedicalExternalRequestItemBos, String storeCode) {
        return goodsMedicalExternalRequestItemBos.stream().filter(x -> {
            if (StringUtils.isBlank(x.getGoodsCode())) {
                logger().info("同步药房：" + getShopId() + ":缺少药品唯一码-" + x.toString());
                return false;
            }
            if (StringUtils.isBlank(x.getGoodsCommonName())) {
                logger().info("同步药房：" + storeCode + " 药品信息错误：" + getShopId() + ":缺少通用名称-" + x.toString());
                return false;
            }
            if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(x.getIsMedical())) {
                if (StringUtils.isBlank(x.getGoodsQualityRatio())) {
                    logger().info("同步药房：" + storeCode + " 药品信息错误：" + getShopId() + ":缺少规格系数-" + x.toString());
                    return false;
                }
                if (StringUtils.isBlank(x.getGoodsProductionEnterprise())) {
                    logger().info("同步药房：" + storeCode + " 药品信息错误：" + getShopId() + ":缺少药品生产企业-" + x.toString());
                    return false;
                }
            }

            x.setGoodsCommonName(x.getGoodsCommonName().trim());
            if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(x.getIsMedical())) {
                x.setGoodsQualityRatio(x.getGoodsQualityRatio().trim());
                x.setGoodsProductionEnterprise(x.getGoodsProductionEnterprise().trim());
                String key = x.getGoodsCommonName() + x.getGoodsQualityRatio() + x.getGoodsProductionEnterprise();
                x.setGoodsKeyComposedByNameQualityEnterprise(key);
            } else {
                x.setGoodsKeyComposedByNameQualityEnterprise(x.getGoodsCommonName());
            }
            if (x.getGoodsApprovalNumber() != null) {
                // 完全是为了顾及医院数据存在质量问题
                x.setGoodsApprovalNumber(x.getGoodsApprovalNumber().trim());
            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * 获取his或药店中间表分页信息
     * @param param
     * @return
     */
    public PageResult<GoodsExternalDo> getExternalPageList(GoodsExternalPageParam param) {
        return goodsExternalDao.getExternalPageList(param);
    }

    public void batchSaveMatchedGoodsList(List<ExternalMatchedGoodsParam> externalMatchedGoodsParams){
        for (ExternalMatchedGoodsParam externalMatchedGoodsParam : externalMatchedGoodsParams) {
            saveMatchedGoodsList(externalMatchedGoodsParam);
        }
    }

    public void batchUpStoreGoodsList(){
        int startRows = 0;
        int pageRows = 1000;
        List<GoodsExternalDo> goodsExternalDos = goodsExternalDao.listStoreCanUpGoods(startRows,pageRows);
        while (goodsExternalDos.size() > 0) {

        }

    }

    public void saveMatchedGoodsList(ExternalMatchedGoodsParam externalMatchedGoodsParam) {
        transaction(()->{
            if (MedicalGoodsConstant.GOODS_IS_MEDICAL.equals(externalMatchedGoodsParam.getIsMedical())) {
                externalMatchedGoodsParam.setMedicalKey(externalMatchedGoodsParam.getGoodsCommonName() + externalMatchedGoodsParam.getGoodsQualityRatio() + externalMatchedGoodsParam.getGoodsProductionEnterprise());
            } else {
                externalMatchedGoodsParam.setMedicalKey(externalMatchedGoodsParam.getGoodsCommonName());
            }

            GoodsEntity existGoodsEntity = goodsAggregate.getByExternalInfo(externalMatchedGoodsParam.getMedicalKey());
            GoodsEntity goodsEntity = GoodsConverter.convertExternalMatchedGoodsParamToGoodsEntity(externalMatchedGoodsParam);
            if (existGoodsEntity == null) {
                insert(getShopId(),goodsEntity);
            } else {
                goodsEntity.setGoodsId(existGoodsEntity.getGoodsId());
                if (goodsEntity.getGoodsMedicalInfo() != null) {
                    goodsEntity.getGoodsMedicalInfo().setId(existGoodsEntity.getGoodsMedicalInfo()!=null?existGoodsEntity.getGoodsMedicalInfo().getId():null);
                }
                goodsAggregate.update(goodsEntity);
                medicalGoodsSpecProductService.updateExternalSku(goodsEntity.getGoodsId(),goodsEntity.getShopPrice(),goodsEntity.getMarketPrice(),goodsEntity.getCostPrice());
            }
            Integer prdId = goodsEntity.getGoodsSpecProducts().get(0).getPrdId();
            storeGoodsService.updateMatchedExternalStoreGoodsInfos(externalMatchedGoodsParam.getStoreGoodsCode(),externalMatchedGoodsParam.getHisPrice(),goodsEntity.getGoodsId(),prdId);
            goodsExternalDao.updateExternalInfoToMatched(externalMatchedGoodsParam.getFromHisId(),externalMatchedGoodsParam.getFromStoreId());
        });
    }

    public boolean isAlreadyDisposed(List<ExternalMatchedGoodsParam> params){
        if (params == null || params.size() == 0) {
            return false;
        }
        for (ExternalMatchedGoodsParam param : params) {
            boolean alreadyDisposed = goodsExternalDao.isAlreadyDisposed(param);
            if (alreadyDisposed) {
                return true;
            }
        }
        return false;
    }

    public void failMatchGoods(FailMatchedParam param) {
        goodsExternalDao.failMatchGoods(param);
    }

    /**
     * 根据goodsId,goodsCommonName,goodsQualityRatio,productionEnterprise匹配药品信息
     * @param goodsMatchParam
     * @return
     */
    public GoodsPrdVo matchGoodsMedicalDetail(GoodsMatchParam goodsMatchParam) {
        Integer goodsId = goodsAggregate.matchGoodsMedical(goodsMatchParam);
        if (goodsId == null) {
            return null;
        }
        GoodsDetailVo goodsDetail = goodsAggregate.getByGoodsId(goodsId);
        GoodsPrdVo goodsPrd = new GoodsPrdVo();
        FieldsUtil.assign(goodsDetail, goodsPrd);
        Integer prdId = goodsSpecProductService.getDefaultPrdId(goodsDetail.getGoodsId());
        goodsPrd.setPrdId(prdId);
        return goodsPrd;
    }

}
