package com.meidianyi.shop.service.shop.goods.goodsimport;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelReader;
import com.meidianyi.shop.common.foundation.excel.exception.handler.IllegalExcelBinder;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.RegexUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.GoodsImportDetailRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.UploadedImageRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLock;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockKeys;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionNumConfig;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.goods.Goods;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsDataIIllegalEnum;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsDataIllegalEnumWrap;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsSharePostConfig;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.GoodsExcelImportBase;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.ImportResultCodeWrap;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuExcelImportBo;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuExcelImportModel;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuExcelImportMqParam;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuExcelImportParam;
import com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsNormalSortParam;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpec;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecProduct;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecVal;
import com.meidianyi.shop.service.pojo.shop.image.DownloadImageBo;
import com.meidianyi.shop.service.shop.goods.GoodsBrandService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.GoodsSortService;
import com.meidianyi.shop.service.shop.goods.GoodsSpecProductService;
import com.meidianyi.shop.service.shop.image.ImageService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 商品导入
 * @author 李晓冰
 * @date 2020年03月18日
 */
@Service
public class GoodsImportService extends ShopBaseService {

    final static Pattern BODY_PATTERN = Pattern.compile("<body>([\\s\\S]*)</body>");
    final static Pattern SCRIPT_PATTERN = Pattern.compile("(<script[\\s\\S]*?/>)|(?:<script[^/>]*?>[\\s\\S]*?<(\\s)*?/script(\\s\\S)*?>)");
    final static Pattern STYLE_PATTERN = Pattern.compile("<style[^>]*>[\\s\\S]*</style>");
    final static Pattern DOCUMENT_PATTERN = Pattern.compile("<!DOCTYPE[^>]*>");
    final static Pattern LINE_FEED_PATTERN = Pattern.compile("[\\r\\n]*");
    final static Pattern MEDIA_PATTERN = Pattern.compile("(<audio[^>]*>[\\s\\S]*</audio>)|(<video[^>]*>[\\s\\S]*</video>)|(<object[^>]*>[\\s\\S]*</object>)|(<embed[^>]*?/>)");


    @Autowired
    ImageService imageService;
    @Autowired
    GoodsImportRecordService importRecordService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    GoodsSortService goodsSortService;
    @Autowired
    GoodsBrandService goodsBrandService;
    /**
     * 最大导入数量
     */
    final Integer MAX_IMPORT_NUM = 2000;

    /**
     * 微铺宝商品excel模板导入
     * @param param 导入参数
     * @return 导入直接
     */
    public ImportResultCodeWrap goodsVpuExcelImport(GoodsVpuExcelImportParam param) {
        ImportResultCodeWrap codeWrap = new ImportResultCodeWrap();
        Workbook workbook = null;
        String filePath;
        try (InputStream in1 = param.getFile().getInputStream(); InputStream in2 = param.getFile().getInputStream()) {
            workbook = ExcelFactory.createWorkbook(in1, param.getExcelTypeEnum());
            filePath = createFilePath(getShopId(), param.getFile().getOriginalFilename());
            try {
                logger().debug("微铺宝excel商品导入excel上传upYun开始");
                imageService.getUpYunClient().writeFile(filePath, in2, true, null);
                logger().debug("微铺宝excel商品导入excel上传upYun结束");
            } catch (Exception e) {
                logger().debug("微铺宝excel商品导入excel上传upYun失败：" + e.getMessage());
                codeWrap.setResultCode(JsonResultCode.GOODS_EXCEL_UPLOAD_UPYUN_WRONG);
                return codeWrap;
            }
        } catch (IOException e) {
            logger().debug("微铺宝excel商品导入创建workbook失败：" + e.getMessage());
            codeWrap.setResultCode(JsonResultCode.GOODS_EXCEL_IMPORT_WORKBOOK_CREATE_FAIL);
            return codeWrap;
        }

        // 创建handler读取对应的excel数据
        GoodsExcelIllegalFormatterHandler handler = new GoodsExcelIllegalFormatterHandler();
        ExcelReader excelReader = new ExcelReader(param.getLang(), workbook, handler);
        List<GoodsVpuExcelImportModel> goodsVpuExcelImportModels = excelReader.readModelList(GoodsVpuExcelImportModel.class);

        IllegalExcelBinder wrongBinderInfo = handler.getWrongBinderInfo();
        JsonResultCode code;
        if (wrongBinderInfo != null) {
            switch (wrongBinderInfo.getIllegalExcelEnum()) {
                case ILLEGEL_SHEET_POSITION: // sheet位置错误
                    code = JsonResultCode.GOODS_EXCEL_IMPORT_SHEET_HEADER_WRONG_INDEX;
                    break;
                case SHEET_HEAD_NULL:// sheet header位置错误
                    code = JsonResultCode.GOODS_EXCEL_IMPORT_SHEET_HEADER_WRONG_INDEX;
                    break;
                case ILLEGAL_SHEET_HEAD:// sheet 数据列和model定义的列位置不一致或列名错误
                    code = JsonResultCode.GOODS_EXCEL_IMPORT_SHEET_COLUMN_NOT_MAP_POJO;
                    break;
                default:
                    code = JsonResultCode.CODE_FAIL;
            }
            codeWrap.setResultCode(code);
        } else if (goodsVpuExcelImportModels.size() > MAX_IMPORT_NUM) {
            codeWrap.setResultCode(JsonResultCode.GOODS_EXCEL_IMPORT_NUM_OUT_OF_SIZE);
            return codeWrap;
        } else {
            code = JsonResultCode.CODE_SUCCESS;
            codeWrap.setResultCode(code);

            Integer batchId = importRecordService.insertGoodsImportInfo(goodsVpuExcelImportModels.size(), filePath, param.getIsUpdate());
            codeWrap.setBatchId(batchId);
            /**excel model 对象转换为对应的业务对象*/
            List<GoodsVpuExcelImportBo> goodsList = goodsVpuExcelImportModels.stream().map(GoodsVpuExcelImportBo::new).collect(Collectors.toList());
            GoodsVpuExcelImportMqParam mqParam = new GoodsVpuExcelImportMqParam(goodsList, param.getLang(), param.getIsUpdate(), batchId, getShopId(), null);
            // 调用消息队列
            saas.taskJobMainService.dispatchImmediately(mqParam, GoodsVpuExcelImportMqParam.class.getName(), getShopId(),
                TaskJobsConstant.TaskJobEnum.GOODS_VPU_EXCEL_IMPORT.getExecutionType());
//            try {
//                goodsVpuExcelImportMqCallback(mqParam);
//            }catch (Exception e){
//                logger().warn("商品导入操作失败："+e.getMessage());
//                code = JsonResultCode.CODE_FAIL;
//            }
        }
        return codeWrap;
    }

    /**
     * 微铺宝商品excel模板导入mq 消费者回调方法
     * @param param
     */
    public void goodsVpuExcelImportMqCallback(GoodsVpuExcelImportMqParam param) {
        logger().debug("商品导入回调函数开始执行");
        List<GoodsVpuExcelImportBo> readyToImportGoodsList = Optional.ofNullable(param.getGoodsVpuExcelImportBos()).orElse(new ArrayList<>());
        Integer shopId = getShopId();
        // 辅助数据
        Integer limitNum = saas.getShopApp(shopId).version.getLimitNum(VersionNumConfig.GOODSNUM);
        Integer goodsCount = goodsService.selectGoodsCount();
        GoodsImportAssistParam assistParam = new GoodsImportAssistParam();
        assistParam.setBatchId(param.getBatchId());
        assistParam.setLimitNum(limitNum);
        assistParam.setGoodsCount(goodsCount);
        assistParam.setUpdate(param.isUpdate());

        List<Integer> goodsIds = new ArrayList<>(readyToImportGoodsList.size() / 2);
        List<GoodsImportDetailRecord> successGoodsList = new ArrayList<>(readyToImportGoodsList.size() / 2);
        List<GoodsImportDetailRecord> illegalGoodsList = new ArrayList<>(readyToImportGoodsList.size() / 2);

        if (!assistParam.isUpdate()) {
            logger().debug("商品导入-插入操作");
            goodsVpuExcelImportForInsert(shopId, readyToImportGoodsList, successGoodsList, goodsIds, illegalGoodsList, assistParam);
            importRecordService.updateGoodsImportSuccessNum(successGoodsList.size(), assistParam.getBatchId());
            successGoodsList.addAll(illegalGoodsList);
            importRecordService.insertGoodsImportDetailBatch(successGoodsList);
            if (goodsIds.size() > 0) {
                logger().debug("商品导入-更新es goodsIds:"+ goodsIds.toString());
                goodsService.updateEs(goodsIds);
                logger().debug("商品导入-更新es 完成:");
            }
        }else {
            filterIllegalGoodsListForNull(readyToImportGoodsList,assistParam.getBatchId(),assistParam.isUpdate());
            goodsImportIterateOperate(readyToImportGoodsList,illegalGoodsList,assistParam);
        }

        importRecordService.updateImportFinish(assistParam.getBatchId());
        logger().debug("商品导入-完成:");
    }

    @RedisLock(prefix = JedisKeyConstant.GOODS_LOCK)
    private void goodsVpuExcelImportForInsert(@RedisLockKeys Integer shopId, List<GoodsVpuExcelImportBo> readyToImportGoodsList, List<GoodsImportDetailRecord> successGoodsList, List<Integer> goodsIds, List<GoodsImportDetailRecord> illegalGoodsList, GoodsImportAssistParam assistParam) {
        HashMap<String, List<GoodsVpuExcelImportBo>> goodsSnMapBos = new HashMap<>(readyToImportGoodsList.size() / 4);
        // 预处理插入数据
        preFilterRepeatedGoodsDatasForInsert(readyToImportGoodsList, illegalGoodsList, goodsSnMapBos, assistParam);
        // 统一处理商品描述内容，这样正则解析器速度稍快
        disposeGoodsDesc(goodsSnMapBos);
        // 获取品牌映射
        Map<String, Integer> brandMap = goodsBrandService.getNormalBrandMap();
        // 获取分类映射
        Map<String, Integer> sortMap = goodsSortService.getSortMap();

        logger().debug("商品导入-执行插入数据库");
        for (Map.Entry<String, List<GoodsVpuExcelImportBo>> entry : goodsSnMapBos.entrySet()) {
            List<GoodsVpuExcelImportBo> bos = entry.getValue();
            if (isGoodsNumOversize(assistParam)) {
                illegalGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(bos, GoodsDataIIllegalEnum.GOODS_NUM_FETCH_LIMIT_NUM, assistParam.getBatchId()));
                continue;
            }

            Goods goods = convertGoodsExcelImportBosToGoodsWithSku(bos);
            // 执行真正的插入操作
            GoodsDataIllegalEnumWrap codeWrap = realInsert(bos, goods, brandMap, sortMap);
            if (!GoodsDataIIllegalEnum.GOODS_OK.equals(codeWrap.getIllegalEnum())) {
                illegalGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(bos, codeWrap.getIllegalEnum(), assistParam.getBatchId()));
            } else {
                assistParam.setGoodsCount(assistParam.getGoodsCount() + 1);
                goodsIds.add(codeWrap.getGoodsId());
                successGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(bos, codeWrap.getIllegalEnum(), assistParam.getBatchId(), true));
            }
        }
        logger().debug("商品导入-执行插入完成");
    }


    /**
     * 商品导入预处理-过滤存在（内部或数据库）重复字段的数据
     * prdSn,prdCode导入数据内部自重复处理
     * goodsName,goodsSn,prdSn,prdCodes是否数据库内存在重复
     * @param readyToImportGoodsList 待导入数据
     * @param assistParam            商品导入辅助参数数据
     * @param goodsSnMapBos          存放最终可用的商品数据
     */
    private void preFilterRepeatedGoodsDatasForInsert(List<GoodsVpuExcelImportBo> readyToImportGoodsList, List<GoodsImportDetailRecord> illegalGoodsList, HashMap<String, List<GoodsVpuExcelImportBo>> goodsSnMapBos, GoodsImportAssistParam assistParam) {
        boolean isUpdate = assistParam.isUpdate();
        Integer batchId = assistParam.getBatchId();

        //商品数量达到上限
        if (!isUpdate && isGoodsNumOversize(assistParam)) {
            importRecordService.convertVpuExcelImportBosToImportDetails(readyToImportGoodsList, GoodsDataIIllegalEnum.GOODS_NUM_FETCH_LIMIT_NUM, batchId);
            readyToImportGoodsList.clear();
            return;
        }

        // 分组prdSn和prdCode,检查是否内部数据存在重复（prdCode=null是合法的检查时需要特殊处理）
        // 商品名称和goodsSn不用内部重复检查，如果重复了则表明是同一个商品的不同sku,所以对商品名和goodsSn的分组房子了数据库查重的时候，尽量延迟内存的占用
        HashMap<String, List<GoodsVpuExcelImportBo>> prdSnMapBos = new HashMap<>(readyToImportGoodsList.size() / 2);
        HashMap<String, List<GoodsVpuExcelImportBo>> prdCodeMapBos = new HashMap<>(readyToImportGoodsList.size() / 2);
        // 移除必填字段为null的情况
        readyToImportGoodsList.removeIf(bo -> {
            // 检查基础字段是否为null
            GoodsDataIIllegalEnum illegalEnum = isBoBaseColumnNull(bo, assistParam.isUpdate());
            if (!GoodsDataIIllegalEnum.GOODS_OK.equals(illegalEnum)) {
                illegalGoodsList.add(importRecordService.convertVpuExcelImportBoToImportDetail(bo, illegalEnum, assistParam.getBatchId()));
                return true;
            }
            //prdSn分组
            List<GoodsVpuExcelImportBo> bos = prdSnMapBos.computeIfAbsent(bo.getPrdSn(), k -> new ArrayList<>(2));
            bos.add(bo);
            // prdCode分组
            bos = prdCodeMapBos.computeIfAbsent(bo.getPrdCodes(), k -> new ArrayList<>(2));
            bos.add(bo);

            return false;
        });
        // 不用考虑prdCode为null的情况
        prdCodeMapBos.remove(null);
        // prdSn,prdCode导入数据内部自重复处理
        isBoBaseColumnInnerRepeated(readyToImportGoodsList, illegalGoodsList, prdSnMapBos, prdCodeMapBos, batchId);
        // goodsName,goodsSn,prdSn,prdCodes是否数据库内存在重复
        isBoBaseColumnDbRepeatedForInsert(readyToImportGoodsList, illegalGoodsList, goodsSnMapBos, prdSnMapBos, prdCodeMapBos, batchId);
    }

    /**
     * 插入操作判断字段是否数据库内部重复
     * @param readyToImportGoodsList
     * @param illegalGoodsList
     * @param goodsSnMapBos
     * @param prdSnMapBos
     * @param prdCodeMapBos
     * @param batchId
     */
    private void isBoBaseColumnDbRepeatedForInsert(List<GoodsVpuExcelImportBo> readyToImportGoodsList, List<GoodsImportDetailRecord> illegalGoodsList, HashMap<String, List<GoodsVpuExcelImportBo>> goodsSnMapBos, HashMap<String, List<GoodsVpuExcelImportBo>> prdSnMapBos, HashMap<String, List<GoodsVpuExcelImportBo>> prdCodeMapBos, Integer batchId) {
        int baseNum = 500;
        // 找出在数据库内已经存在的值
        List<String> goodsSnsForDb = new ArrayList<>(readyToImportGoodsList.size() / 2);
        List<String> goodsNameForDb = new ArrayList<>(readyToImportGoodsList.size() / 2);
        List<String> prdSnsForDb = new ArrayList<>(readyToImportGoodsList.size() / 2);
        List<String> prdCodesForDb = new ArrayList<>(readyToImportGoodsList.size() / 2);

        List<String> goodsSnsRepeated = new ArrayList<>();
        List<String> goodsNamesRepeated = new ArrayList<>();
        List<String> prdSnsRepeated = new ArrayList<>(readyToImportGoodsList.size() / 4);
        List<String> prdCodesRepeated = new ArrayList<>(readyToImportGoodsList.size() / 4);

        Map<String, List<GoodsVpuExcelImportBo>> goodsNameMapBos = new HashMap<>(goodsSnMapBos.size());

        for (int i = 1; i <= readyToImportGoodsList.size(); i++) {
            if (i % baseNum == 0) {
                goodsSnsRepeated.addAll(goodsService.findGoodsSnExist(goodsSnsForDb));
                goodsNamesRepeated.addAll(goodsService.findGoodsNameExist(goodsNameForDb));
                prdSnsRepeated.addAll(goodsService.goodsSpecProductService.findSkuPrdSnExist(prdSnsForDb));
                prdCodesRepeated.addAll(goodsService.goodsSpecProductService.findSkuPrdCodesExist(prdCodesForDb));
                goodsSnsForDb.clear();
                goodsNameForDb.clear();
                prdSnsForDb.clear();
                prdCodesForDb.clear();
            }
            GoodsVpuExcelImportBo bo = readyToImportGoodsList.get(i - 1);
            goodsSnsForDb.add(bo.getGoodsSn());
            goodsNameForDb.add(bo.getGoodsName());
            prdSnsForDb.add(bo.getPrdSn());
            if (bo.getPrdCodes() != null) {
                prdCodesForDb.add(bo.getPrdCodes());
            }

            //goodsSn分组
            List<GoodsVpuExcelImportBo> bos = goodsSnMapBos.computeIfAbsent(bo.getGoodsSn(), k -> new ArrayList<>(2));
            bos.add(bo);
            //goodsName分组
            bos = goodsNameMapBos.computeIfAbsent(bo.getGoodsName(), k -> new ArrayList<>(2));
            bos.add(bo);
        }
        goodsSnsRepeated.addAll(goodsService.findGoodsSnExist(goodsSnsForDb));
        goodsNamesRepeated.addAll(goodsService.findGoodsNameExist(goodsNameForDb));
        prdSnsRepeated.addAll(goodsService.goodsSpecProductService.findSkuPrdSnExist(prdSnsForDb));
        prdCodesRepeated.addAll(goodsService.goodsSpecProductService.findSkuPrdCodesExist(prdCodesForDb));

        // 将已存在的从对应map移除，那么map里面存放的是暂时合法的数据
        for (String goodsSn : goodsSnsRepeated) {
            List<GoodsVpuExcelImportBo> illegal = goodsSnMapBos.remove(goodsSn);
            if (illegal != null) {
                illegalGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(illegal, GoodsDataIIllegalEnum.GOODS_SN_EXIST, batchId));
            }
        }
        for (String goodsName : goodsNamesRepeated) {
            List<GoodsVpuExcelImportBo> illegal = goodsNameMapBos.remove(goodsName);
            if (illegal != null) {
                illegalGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(illegal, GoodsDataIIllegalEnum.GOODS_NAME_EXIST, batchId));
            }
        }
        for (String prdSn : prdSnsRepeated) {
            List<GoodsVpuExcelImportBo> illegal = prdSnMapBos.remove(prdSn);
            if (illegal != null) {
                illegalGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(illegal, GoodsDataIIllegalEnum.GOODS_PRD_SN_EXIST, batchId));
            }
        }
        for (String prdCode : prdCodesRepeated) {
            List<GoodsVpuExcelImportBo> illegal = prdCodeMapBos.remove(prdCode);
            if (illegal != null) {
                illegalGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(illegal, GoodsDataIIllegalEnum.GOODS_PRD_CODES_EXIST, batchId));
            }
        }
        // 直接移除不合法的
        readyToImportGoodsList.removeIf(bo -> {
            if (goodsSnMapBos.get(bo.getGoodsSn()) == null) {
                return true;
            }
            if (goodsNameMapBos.get(bo.getGoodsName()) == null) {
                return true;
            }
            if (prdSnMapBos.get(bo.getPrdSn()) == null) {
                return true;
            }
            if (bo.getPrdCodes() != null && prdCodeMapBos.get(bo.getPrdCodes()) == null) {
                return true;
            }
            return false;
        });
        goodsSnsForDb = null;
        goodsNameForDb = null;
        prdSnsForDb = null;
        prdCodesForDb = null;
        goodsSnsRepeated = null;
        goodsNamesRepeated = null;
        prdSnsRepeated = null;
        prdCodesRepeated = null;

        // goodsSnMapBos中存在的数据可能由于prdSn、goodsName、prdCodes三种条件重复而被从readyToImportGoodsList中移除
        goodsSnMapBos.clear();

        readyToImportGoodsList.forEach(bo -> {
            List<GoodsVpuExcelImportBo> bos = goodsSnMapBos.get(bo.getGoodsSn());
            if (bos == null) {
                bos = new ArrayList<>(2);
                goodsSnMapBos.put(bo.getGoodsSn(), bos);
            } else {
                // 处理商品名称以，第一条数据为准
                GoodsVpuExcelImportBo b = bos.get(0);
                bo.setGoodsName(b.getGoodsName());
            }
            bos.add(bo);
        });

        // 插入后序操作都以goodsSnMapBos为数据源，readyToImportGoodsList的作用已结束，所以就不需要进行其它操作
        readyToImportGoodsList.clear();
    }

    @SuppressWarnings("all")
    private GoodsDataIllegalEnumWrap realInsert(List<GoodsVpuExcelImportBo> importBos, Goods goods, Map<String, Integer> brandMap, Map<String, Integer> sortMap) {
        GoodsDataIllegalEnumWrap resultCode = new GoodsDataIllegalEnumWrap();
        // 检查规格名称是否存在重复
        boolean isOk = goodsService.goodsSpecProductService.isSpecNameOrValueRepeat(goods.getGoodsSpecs());
        if (!isOk) {
            resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_SPEC_K_V_REPEATED);
            return resultCode;
        }
        // 校验输入的规格组是否正确
        isOk = goodsService.goodsSpecProductService.isGoodsSpecProductDescRight(goods.getGoodsSpecProducts(), goods.getGoodsSpecs());
        if (!isOk) {
            resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_PRD_DESC_WRONG);
            return resultCode;
        }
        String prdDesc = goods.getGoodsSpecProducts().get(0).getPrdDesc();
        // 处理是否是单规格数据
        if (goods.getGoodsSpecProducts().size() == 1 && StringUtils.isBlank(prdDesc)) {
            goods.setIsDefaultProduct((byte) 1);
        } else {
            goods.setIsDefaultProduct((byte) 0);
        }

        // 处理图片
        List<DownloadImageBo> downloadImageBos = filterGoodsImages(importBos);
        if (downloadImageBos.size() == 0) {
            resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_IMG_IS_WRONG);
            return resultCode;
        }
        GoodsVpuExcelImportBo bo = importBos.get(0);
        // 处理商家分类品牌
        try {
            // 处理
            if (StringUtils.isNotBlank(bo.getFirstSortName()) || StringUtils.isNotBlank(bo.getSecondSortName())) {
                Integer sortId = disposeSortName(bo.getFirstSortName(), bo.getSecondSortName(), sortMap);
                goods.setSortId(sortId);
            }

            if (StringUtils.isNotBlank(bo.getBrandName())) {
                Integer integer = disposeBrandName(bo.getBrandName(), brandMap);
                goods.setBrandId(integer);
            }
        } catch (Exception e) {
            logger().warn("商品excel导入-品牌商家分类插入-失败:" + e.getMessage());
            resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_FAIL);
            return resultCode;
        }
        // 这个地方是为了避免报异常
        goods.setCatId(0);
        try {
            transaction(() -> {
                // 处理用户指定的商品图片
                List<String> imgs = imageService.addImageToDbBatch(downloadImageBos);
                goods.setGoodsImg(imgs.remove(0));
                goods.setGoodsImgs(imgs);

                GoodsDataIllegalEnumWrap insertResult = goodsService.insert(goods);
                resultCode.setIllegalEnum(insertResult.getIllegalEnum());
                resultCode.setGoodsId(insertResult.getGoodsId());
            });
        } catch (Exception e) {
            logger().debug("商品excel导入：" + e.getMessage());
            resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_FAIL);
        }
        return resultCode;
    }

    /**
     * 处理商家分类
     * @param firstSortName  一级分类名
     * @param secondSortName 二级分类名
     * @param sortMap        分类映射
     * @return 分类id
     */
    private Integer disposeSortName(String firstSortName, String secondSortName, Map<String, Integer> sortMap) {
        Integer returnId = null;
        if (StringUtils.isNotBlank(firstSortName)) {
            returnId = sortMap.get(firstSortName);
            if (returnId == null) {
                GoodsNormalSortParam param = new GoodsNormalSortParam();
                param.setParentId(GoodsConstant.ROOT_PARENT_ID);
                param.setSortName(firstSortName);
                param.setLevel((short) 0);
                returnId = goodsSortService.insertNormal(param);
                sortMap.put(firstSortName, returnId);
            }
        }

        if (StringUtils.isNotBlank(secondSortName)) {
            Integer secondId = sortMap.get(secondSortName);
            if (secondId == null) {
                GoodsNormalSortParam param = new GoodsNormalSortParam();
                param.setParentId(GoodsConstant.ROOT_PARENT_ID);
                param.setSortName(secondSortName);
                param.setLevel((short) 0);
                if (returnId != null) {
                    param.setParentId(returnId);
                    param.setLevel((short) 1);
                }
                secondId = goodsSortService.insertNormal(param);
                sortMap.put(secondSortName, secondId);
            }
            returnId = secondId;
        }
        return returnId;
    }

    /**
     * 处理品牌名称
     * @param brandName
     * @param brandMap
     * @return
     */
    private Integer disposeBrandName(String brandName, Map<String, Integer> brandMap) {
        Integer brandId = brandMap.get(brandName);
        if (brandId == null) {
            brandId = goodsBrandService.addBrand(brandName);
            brandMap.put(brandName, brandId);
        }
        return brandId;
    }

    /**
     * 通过正则表达式过滤商品描述
     * @param goodsSnMapBos
     */
    private void disposeGoodsDesc(HashMap<String, List<GoodsVpuExcelImportBo>> goodsSnMapBos) {

        for (Map.Entry<String, List<GoodsVpuExcelImportBo>> entry : goodsSnMapBos.entrySet()) {
            List<GoodsVpuExcelImportBo> bos = entry.getValue();
            String goodsDesc = bos.get(0).getGoodsDesc();
            if (StringUtils.isBlank(goodsDesc)) {
                continue;
            }
            Matcher matcher = BODY_PATTERN.matcher(goodsDesc);
            goodsDesc = matcher.replaceAll(goodsDesc);
            matcher = SCRIPT_PATTERN.matcher(goodsDesc);
            goodsDesc = matcher.replaceAll(goodsDesc);
            matcher = STYLE_PATTERN.matcher(goodsDesc);
            goodsDesc = matcher.replaceAll(goodsDesc);
            matcher = DOCUMENT_PATTERN.matcher(goodsDesc);
            goodsDesc = matcher.replaceAll(goodsDesc);
            matcher = LINE_FEED_PATTERN.matcher(goodsDesc);
            goodsDesc = matcher.replaceAll(goodsDesc);
            matcher = MEDIA_PATTERN.matcher(goodsDesc);
            goodsDesc = matcher.replaceAll(goodsDesc);

            for (GoodsVpuExcelImportBo bo : bos) {
                bo.setGoodsDesc(goodsDesc);
            }
        }


    }

    /**
     * 商品导入预处理-判断基础数据是否为null
     * @param bo       待导入数据
     * @param isUpdate 是否是更新操作
     */
    private GoodsDataIIllegalEnum isBoBaseColumnNull(GoodsVpuExcelImportBo bo, boolean isUpdate) {
        if (StringUtils.isBlank(bo.getGoodsName())) {
            return GoodsDataIIllegalEnum.GOODS_NAME_NULL;
        }
        if (StringUtils.isBlank(bo.getGoodsSn())) {
            return GoodsDataIIllegalEnum.GOODS_SN_NULL;
        }
        if (StringUtils.isBlank(bo.getPrdSn())) {
            return GoodsDataIIllegalEnum.GOODS_PRD_SN_NULL;
        }
        // 插入操作特殊校验
        if (!isUpdate && StringUtils.isBlank(bo.getGoodsImgsStr())) {
            return GoodsDataIIllegalEnum.GOODS_IMG_IS_WRONG;
        }
        if (!isUpdate && bo.getShopPrice() == null) {
            return GoodsDataIIllegalEnum.GOODS_SHOP_PRICE_IS_NULL;
        }
        if (!isUpdate && bo.getCostPrice() == null) {
            return GoodsDataIIllegalEnum.GOODS_COST_PRICE_IS_NULL;
        }

        return GoodsDataIIllegalEnum.GOODS_OK;
    }

    /**
     * 校验
     * @param readyToImportGoodsList
     * @param illegalGoodsList
     * @param prdSnMapBos
     * @param prdCodeMapBos
     * @param batchId
     */
    private void isBoBaseColumnInnerRepeated(List<GoodsVpuExcelImportBo> readyToImportGoodsList, List<GoodsImportDetailRecord> illegalGoodsList, HashMap<String, List<GoodsVpuExcelImportBo>> prdSnMapBos, HashMap<String, List<GoodsVpuExcelImportBo>> prdCodeMapBos, Integer batchId) {
        readyToImportGoodsList.removeIf(bo -> {
            List<GoodsVpuExcelImportBo> bos = prdSnMapBos.get(bo.getPrdSn());
            // bo的prdSn在对应的prdSnMapBos肯定存在，如果不存在证明在之前已经由于存在重复prdSn项而被移除
            if (bos == null) {
                return true;
            }
            if (bos.size() > 1) {
                illegalGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(bos, GoodsDataIIllegalEnum.GOODS_PRD_SN_INNER_REPEATED, batchId));
                // 防止被错误数据被重复加入
                prdSnMapBos.remove(bo.getPrdSn());
                return true;
            }

            // prdCode 可以为null,但是不可以重复
            if (bo.getPrdCodes() != null) {
                bos = prdCodeMapBos.get(bo.getPrdCodes());
                if (bos == null) {
                    return true;
                }
                if (bos.size() > 1) {
                    illegalGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(bos, GoodsDataIIllegalEnum.GOODS_PRD_CODES_INNER_REPEATED, batchId));
                    // 防止被错误数据被重复加入
                    prdCodeMapBos.remove(bo.getPrdCodes());
                    return true;
                }
            }
            return false;
        });
    }


    /********以上为新方法********/


    /**
     * 初步根据非空字段，将不合法的数据剔除，会修改readyToImportGoodsList集合
     * @param readyToImportGoodsList 待插入数据，方法会将不合法数据项剔除
     * @param batchId                批量处理id
     * @return 不合法的数据集合
     */
    private List<GoodsImportDetailRecord> filterIllegalGoodsListForNull(List<GoodsVpuExcelImportBo> readyToImportGoodsList, Integer batchId, boolean isUpdate) {
        List<GoodsImportDetailRecord> illegalGoods = new ArrayList<>(10);

        readyToImportGoodsList.removeIf(goodsBo -> {
            if (StringUtils.isBlank(goodsBo.getGoodsSn())) {
                illegalGoods.add(importRecordService.convertVpuExcelImportBoToImportDetail(goodsBo, GoodsDataIIllegalEnum.GOODS_SN_NULL, batchId));
                return true;
            }
            if (StringUtils.isBlank(goodsBo.getPrdSn())) {
                illegalGoods.add(importRecordService.convertVpuExcelImportBoToImportDetail(goodsBo, GoodsDataIIllegalEnum.GOODS_PRD_SN_NULL, batchId));
                return true;
            }
            if (StringUtils.isBlank(goodsBo.getGoodsName())) {
                illegalGoods.add(importRecordService.convertVpuExcelImportBoToImportDetail(goodsBo, GoodsDataIIllegalEnum.GOODS_NAME_NULL, batchId));
                return true;
            }
            if (!isUpdate && StringUtils.isBlank(goodsBo.getGoodsImgsStr())) {
                illegalGoods.add(importRecordService.convertVpuExcelImportBoToImportDetail(goodsBo, GoodsDataIIllegalEnum.GOODS_IMG_IS_WRONG, batchId));
                return true;
            }
            if (goodsBo.getShopPrice() == null) {
                illegalGoods.add(importRecordService.convertVpuExcelImportBoToImportDetail(goodsBo, GoodsDataIIllegalEnum.GOODS_SHOP_PRICE_IS_NULL, batchId));
                return true;
            }
            if (goodsBo.getCostPrice() == null) {
                illegalGoods.add(importRecordService.convertVpuExcelImportBoToImportDetail(goodsBo, GoodsDataIIllegalEnum.GOODS_SHOP_PRICE_IS_NULL, batchId));
                return true;
            }
            return false;
        });

        return illegalGoods;
    }


    /**
     * 商品待导入数据进行迭代处理
     * @param readyToImportGoodsList 准备被处理的目标数据集合
     * @param illegalGoodsList       不合法的数据集合
     * @param assistParam            商品导入辅助参数数据
     */
    private void goodsImportIterateOperate(List<GoodsVpuExcelImportBo> readyToImportGoodsList, List<GoodsImportDetailRecord> illegalGoodsList, GoodsImportAssistParam assistParam) {
        Map<String, List<GoodsVpuExcelImportBo>> goodsMap = readyToImportGoodsList.stream().collect(Collectors.groupingBy(GoodsVpuExcelImportBo::getGoodsSn));
        List<GoodsImportDetailRecord> successGoodsList = new ArrayList<>(readyToImportGoodsList.size() / 2);
        List<Integer> goodsIds = new ArrayList<>(successGoodsList.size());
        Integer shopId = getShopId();

        for (Map.Entry<String, List<GoodsVpuExcelImportBo>> entry : goodsMap.entrySet()) {
            List<GoodsVpuExcelImportBo> value = entry.getValue();
            Integer goodsId = goodsImportOperate(shopId, value, successGoodsList, illegalGoodsList, assistParam);
            if (goodsId != null) {
                goodsIds.add(goodsId);
            }
        }

        importRecordService.updateGoodsImportSuccessNum(successGoodsList.size(), assistParam.getBatchId());
        successGoodsList.addAll(illegalGoodsList);
        importRecordService.insertGoodsImportDetailBatch(successGoodsList);
        if (goodsIds.size() > 0) {
            goodsService.updateEs(goodsIds);
        }
    }

    /**
     * 导入每一个数据时琐lock:goods:shopId，避免admin端商品新增和修改
     * 单条商品数据处理，对应多条sku
     * @param shopId
     * @param goodsSkus
     * @param illegalGoodsList
     * @return 返回受影响商品id
     */
    @SuppressWarnings("unchecked")
    @RedisLock(prefix = JedisKeyConstant.GOODS_LOCK)
    public Integer goodsImportOperate(@RedisLockKeys Integer shopId, List<GoodsVpuExcelImportBo> goodsSkus, List<GoodsImportDetailRecord> successGoodsList, List<GoodsImportDetailRecord> illegalGoodsList, GoodsImportAssistParam assistParam) {
        // 判断是否超出上限
        if (isGoodsNumOversize(assistParam)) {
            List<GoodsImportDetailRecord> overSizeRecord = importRecordService.convertVpuExcelImportBosToImportDetails(goodsSkus, GoodsDataIIllegalEnum.GOODS_NUM_FETCH_LIMIT_NUM, assistParam.getBatchId());
            illegalGoodsList.addAll(overSizeRecord);
            return null;
        }

        // 检查prdSn码是否会有内部自重复的
        List<? extends GoodsExcelImportBase> illegalPrdSnRepeated = getSkuPrdSnRepeated(goodsSkus);
        List<GoodsImportDetailRecord> recordsPrdSn = importRecordService.convertVpuExcelImportBosToImportDetails((List<GoodsVpuExcelImportBo>) illegalPrdSnRepeated, GoodsDataIIllegalEnum.GOODS_PRD_SN_INNER_REPEATED, assistParam.getBatchId());
        illegalGoodsList.addAll(recordsPrdSn);
        // 无有效数据直接返回
        if (goodsSkus.size() == 0) {
            return null;
        }
        // 检查prdCodes码是否会有内部自重复的
        List<? extends GoodsExcelImportBase> illegalPrdCodesRepeated = getSkuPrdCodesRepeated(goodsSkus);
        List<GoodsImportDetailRecord> recordsPrdCodes = importRecordService.convertVpuExcelImportBosToImportDetails((List<GoodsVpuExcelImportBo>) illegalPrdCodesRepeated, GoodsDataIIllegalEnum.GOODS_PRD_CODES_INNER_REPEATED, assistParam.getBatchId());
        illegalGoodsList.addAll(recordsPrdCodes);
        // 无有效数据直接返回
        if (goodsSkus.size() == 0) {
            return null;
        }

        Integer goodsIds = null;
        if (!assistParam.isUpdate()) {
            goodsIds = goodsImportInsert(goodsSkus, successGoodsList, illegalGoodsList, assistParam);
        } else {
            // 查看goodsSn是否存在
            GoodsRecord goodsRecord = goodsService.getGoodsRecordByGoodsSn(goodsSkus.get(0).getGoodsSn());
            //不存在进行商品插入操作
            if (goodsRecord == null) {
                goodsIds = goodsImportInsert(goodsSkus, successGoodsList, illegalGoodsList, assistParam);
            } else {
                // 更新操作
                goodsIds = goodsImportUpdate(goodsRecord, goodsSkus, successGoodsList, illegalGoodsList, assistParam.getBatchId());
            }

        }
        return goodsIds;
    }

    /**
     * 剔除掉带插入的同一商品内部规格编码重复的数据，此方法会修改goodsSkus对象本身
     * @param goodsSkus 同一个商品的sku的集合
     * @return
     */
    private List<? extends GoodsExcelImportBase> getSkuPrdSnRepeated(List<? extends GoodsExcelImportBase> goodsSkus) {
        Map<String, List<GoodsExcelImportBase>> prdSnMap = goodsSkus.stream().collect(Collectors.groupingBy(GoodsExcelImportBase::getPrdSn));
        List<GoodsExcelImportBase> returnList = new ArrayList<>(2);
        goodsSkus.removeIf(bo -> {
            if (prdSnMap.get(bo.getPrdSn()).size() > 1) {
                returnList.addAll(prdSnMap.get(bo.getPrdSn()));
                return true;
            } else {
                return false;
            }
        });
        return returnList;
    }

    /**
     * 剔除掉带插入的同一商品内部商品条码重复的数据，此方法会修改goodsSkus对象本身
     * @param goodsSkus 同一个商品的sku的集合
     * @return
     */
    private List<? extends GoodsExcelImportBase> getSkuPrdCodesRepeated(List<? extends GoodsExcelImportBase> goodsSkus) {
        Map<String, List<GoodsExcelImportBase>> prdCodesMap = goodsSkus.stream().filter(prd -> StringUtils.isNotBlank(prd.getPrdCodes()))
            .collect(Collectors.groupingBy(GoodsExcelImportBase::getPrdCodes));
        List<GoodsExcelImportBase> returnList = new ArrayList<>(2);
        goodsSkus.removeIf(bo -> {
            List<GoodsExcelImportBase> prd = prdCodesMap.get(bo.getPrdCodes());
            if (prd != null && prd.size() > 1) {
                returnList.addAll(prdCodesMap.get(bo.getPrdCodes()));
                return true;
            } else {
                return false;
            }
        });
        return returnList;
    }

    /**
     * 判断规格编码是否在数据库中重复
     * @param goodsSkus
     * @return
     */
    private List<? extends GoodsExcelImportBase> getSkuPrdSnDbExist(List<? extends GoodsExcelImportBase> goodsSkus) {
        List<GoodsExcelImportBase> returnList = new ArrayList<>();
        List<String> prdSnList = goodsSkus.stream().map(GoodsExcelImportBase::getPrdSn).collect(Collectors.toList());

        List<String> existPrdsn = goodsService.goodsSpecProductService.findSkuPrdSnExist(prdSnList);
        if (existPrdsn.size() != 0) {
            goodsSkus.removeIf(sku -> {
                if (existPrdsn.contains(sku.getPrdSn())) {
                    returnList.add(sku);
                    return true;
                } else {
                    return false;
                }
            });
        }
        return returnList;
    }

    /**
     * 判断商品条码是否在数据库中重复
     * @param goodsSkus
     * @return
     */
    private List<? extends GoodsExcelImportBase> getSkuPrdCodesDbExist(List<? extends GoodsExcelImportBase> goodsSkus) {
        List<GoodsExcelImportBase> returnList = new ArrayList<>();
        List<String> prdCodesList = goodsSkus.stream().filter(prd -> StringUtils.isNotBlank(prd.getPrdCodes())).map(GoodsExcelImportBase::getPrdCodes).collect(Collectors.toList());

        List<String> existPrdCodes = goodsService.goodsSpecProductService.findSkuPrdCodesExist(prdCodesList);
        if (existPrdCodes.size() != 0) {
            goodsSkus.removeIf(sku -> {
                if (existPrdCodes.contains(sku.getPrdCodes())) {
                    returnList.add(sku);
                    return true;
                } else {
                    return false;
                }
            });
        }
        return returnList;
    }

    /**
     * 判断商品数量是否超过版本可使用商品数量上限
     * @param param
     * @return
     */
    private boolean isGoodsNumOversize(GoodsImportAssistParam param) {
        if (!param.isUpdate() && param.getLimitNum() != -1 && param.getGoodsCount() >= param.getLimitNum()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 商品更新
     * @param importGoodsSkus
     * @param successGoodsList
     * @param illegalGoodsList
     * @param batchId
     * @return
     */
    private Integer goodsImportUpdate(GoodsRecord goodsRecord, List<GoodsVpuExcelImportBo> importGoodsSkus, List<GoodsImportDetailRecord> successGoodsList, List<GoodsImportDetailRecord> illegalGoodsList, Integer batchId) {
        GoodsVpuExcelImportBo bo = importGoodsSkus.get(0);
        boolean goodsNameExist = goodsService.isGoodsNameExist(goodsRecord.getGoodsId(), bo.getGoodsName());
        // 不合法直接退出
        if (goodsNameExist) {
            illegalGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(importGoodsSkus, GoodsDataIIllegalEnum.GOODS_NAME_EXIST, batchId));
            return null;
        }
        List<GoodsSpecProduct> goodsSpecProducts = disposeGoodsPrdForUpdate(goodsRecord.getGoodsSn(), importGoodsSkus, illegalGoodsList, batchId);
        // excel所有信息和数据库已有规格匹配不上
        if (importGoodsSkus.size() == 0) {
            return null;
        }
        GoodsDataIllegalEnumWrap codeWrap = goodsRealUpdate(goodsRecord, goodsSpecProducts, importGoodsSkus);
        // 更新操作失败
        if (!GoodsDataIIllegalEnum.GOODS_OK.equals(codeWrap.getIllegalEnum())) {
            illegalGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(importGoodsSkus, codeWrap.getIllegalEnum(), batchId));
            return null;
        } else {
            successGoodsList.addAll(importRecordService.convertVpuExcelImportBosToImportDetails(importGoodsSkus, GoodsDataIIllegalEnum.GOODS_OK, batchId));
            return codeWrap.getGoodsId();
        }
    }

    /**
     * 根据导入的sku信息，更新商品原有的sku内容
     * @param goodsSn
     * @param importGoodsSkus
     * @param illegalGoodsList
     * @param batchId
     * @return
     */
    private List<GoodsSpecProduct> disposeGoodsPrdForUpdate(String goodsSn, List<GoodsVpuExcelImportBo> importGoodsSkus, List<GoodsImportDetailRecord> illegalGoodsList, Integer batchId) {
        // 处理规格信息
        List<GoodsSpecProduct> goodsSpecProducts = goodsService.goodsSpecProductService.selectByGoodsSn(goodsSn);
        Map<String, GoodsSpecProduct> goodsSpecPrdMap = goodsSpecProducts.stream().collect(Collectors.toMap(GoodsSpecProduct::getPrdSn, Function.identity(),(oldVal,newVal)->newVal));

        importGoodsSkus.removeIf(sku -> {
            GoodsSpecProduct prd = goodsSpecPrdMap.get(sku.getPrdSn());
            if (prd == null) {
                // 数据库没有该prdSn对应的规格信息
                illegalGoodsList.add(importRecordService.convertVpuExcelImportBoToImportDetail(sku, GoodsDataIIllegalEnum.GOODS_PRD_SN_NOT_EXIT_WITH_GOODS_SN, batchId));
                return true;
            } else {
                prd.setPrdPrice(sku.getShopPrice());
                prd.setPrdMarketPrice(sku.getMarketPrice());
                prd.setPrdCostPrice(sku.getCostPrice());
                prd.setPrdNumber(sku.getStock());
                return false;
            }
        });

        return goodsSpecProducts;
    }

    private GoodsDataIllegalEnumWrap goodsRealUpdate(GoodsRecord goodsRecord, List<GoodsSpecProduct> goodsSpecProducts, List<GoodsVpuExcelImportBo> bos) {
        Integer goodsNum = 0;
        BigDecimal shopPrice = BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal marketPrice = BigDecimal.valueOf(Double.MIN_VALUE);
        BigDecimal costPrice = BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal goodsWeight = BigDecimal.valueOf(Double.MAX_VALUE);
        for (GoodsSpecProduct prd : goodsSpecProducts) {
            goodsNum += (prd.getPrdNumber() == null ? 0 : prd.getPrdNumber());
            if (shopPrice.compareTo(prd.getPrdPrice()) > 0) {
                shopPrice = prd.getPrdPrice();
            }
            if (prd.getPrdMarketPrice() != null && marketPrice.compareTo(prd.getPrdMarketPrice()) < 0) {
                marketPrice = prd.getPrdMarketPrice();
            }
            if (costPrice.compareTo(prd.getPrdCostPrice()) > 0) {
                costPrice = prd.getPrdCostPrice();
            }
        }
        goodsRecord.setGoodsName(bos.get(0).getGoodsName());
        goodsRecord.setGoodsAd(bos.get(0).getGoodsAd());
        goodsRecord.setGoodsNumber(goodsNum);
        goodsRecord.setShopPrice(shopPrice);
        goodsRecord.setMarketPrice(BigDecimal.valueOf(Double.MIN_VALUE).equals(marketPrice) ? null : marketPrice);
        goodsRecord.setCostPrice(costPrice);
        goodsRecord.setGoodsWeight(BigDecimal.valueOf(Double.MIN_VALUE).equals(goodsWeight) ? null : goodsWeight);

        GoodsDataIllegalEnumWrap resultCode = new GoodsDataIllegalEnumWrap();
        // 商品goodsImg待处理图片
        List<DownloadImageBo> goodsImgsDownloadBo = filterGoodsImages(bos);

        // 处理描述信息
        GoodsVpuExcelImportBo bo = bos.get(0);
        List<DownloadImageBo> descDownloadBo = new ArrayList<>();
        if (StringUtils.isNotBlank(bo.getGoodsDesc())) {
            String desc = filterGoodsDesc(bo.getGoodsDesc(), descDownloadBo);
            goodsRecord.setGoodsDesc(desc);
        }
        // 处理商家分类品牌
        try {
            // 此处为放入事务内，如果后面事务异常了那新增的品牌和商家分类不进行回滚
            int sortId = goodsSortService.fixGoodsImportGoodsSort(bo.getFirstSortName(), bo.getSecondSortName());
            goodsRecord.setSortId(sortId);
            if (StringUtils.isNotBlank(bo.getBrandName())) {
                Integer brandId = goodsBrandService.getOrAddBrand(bo.getBrandName());
                goodsRecord.setBrandId(brandId);
            }
        } catch (Exception e) {
            logger().warn("商品excel导入-更新-失败:" + e.getMessage());
            resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_FAIL);
            return resultCode;
        }


        transaction(() -> {
            try {
                // 处理商品描述符图片
//                imageService.addImageToDbBatch(descDownloadBo);

                // 处理商品图片
                List<String> imgs = imageService.addImageToDbBatch(goodsImgsDownloadBo);
                if (imgs.size() > 0) {
                    goodsRecord.setGoodsImg(imgs.get(0));
                    imgs.remove(0);
                    if (imgs.size() > 0) {
                        goodsService.updateGoodsChildImgsForImport(goodsRecord.getGoodsId(), imgs);
                    }
                }
                goodsService.updateGoodsForImport(goodsRecord);
                goodsService.goodsSpecProductService.updateSpecPrdForGoodsImport(goodsSpecProducts);
            } catch (Exception e) {
                logger().warn("商品excel导入-更新-失败:" + e.getMessage());
                resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_FAIL);
            }
        });
        return resultCode;
    }

    /**
     * 导入商品插入操作
     * @param goodsSkus        待插入数据
     * @param successGoodsList 成功数据集合
     * @param illegalGoodsList 失败数据集合
     * @param assistParam      辅助参数
     * @return 插入成功后产生的商品id
     */
    @SuppressWarnings("unchecked")
    private Integer goodsImportInsert(List<GoodsVpuExcelImportBo> goodsSkus, List<GoodsImportDetailRecord> successGoodsList, List<GoodsImportDetailRecord> illegalGoodsList, GoodsImportAssistParam assistParam) {
        // 检查prdSn码数据库是否存在 只有在插入操做的时候需要这样检测，速度会稍微快一些
        List<? extends GoodsExcelImportBase> illegalPrdSnDbExist = getSkuPrdSnDbExist(goodsSkus);
        List<GoodsImportDetailRecord> recordsPrdSn = importRecordService.convertVpuExcelImportBosToImportDetails((List<GoodsVpuExcelImportBo>) illegalPrdSnDbExist, GoodsDataIIllegalEnum.GOODS_PRD_SN_EXIST, assistParam.getBatchId());
        illegalGoodsList.addAll(recordsPrdSn);
        // 如果不存在可用sku直接退出
        if (goodsSkus.size() == 0) {
            return null;
        }
        // 检查prdCodes码数据库是否存在 只有在插入操做的时候需要这样检测，速度会稍微快一些
        List<? extends GoodsExcelImportBase> illegalPrdCodesDbExist = getSkuPrdCodesDbExist(goodsSkus);
        List<GoodsImportDetailRecord> recordsPrdCodes = importRecordService.convertVpuExcelImportBosToImportDetails((List<GoodsVpuExcelImportBo>) illegalPrdCodesDbExist, GoodsDataIIllegalEnum.GOODS_PRD_CODES_EXIST, assistParam.getBatchId());
        illegalGoodsList.addAll(recordsPrdCodes);
        // 如果不存在可用sku直接退出
        if (goodsSkus.size() == 0) {
            return null;
        }

        // 执行真正的插入操作
        GoodsDataIllegalEnumWrap codeWrap = goodsRealInsert(goodsSkus);
        if (!GoodsDataIIllegalEnum.GOODS_OK.equals(codeWrap.getIllegalEnum())) {
            recordsPrdCodes = importRecordService.convertVpuExcelImportBosToImportDetails(goodsSkus, codeWrap.getIllegalEnum(), assistParam.getBatchId());
            illegalGoodsList.addAll(recordsPrdCodes);
        } else {
            recordsPrdCodes = importRecordService.convertVpuExcelImportBosToImportDetails(goodsSkus, codeWrap.getIllegalEnum(), assistParam.getBatchId(), true);
            assistParam.setGoodsCount(assistParam.getGoodsCount() + 1);
            successGoodsList.addAll(recordsPrdCodes);
        }
        return codeWrap.getGoodsId();
    }

    /**
     * 商品插入操作
     * @return
     */
    private GoodsDataIllegalEnumWrap goodsRealInsert(List<GoodsVpuExcelImportBo> importBos) {
        GoodsDataIllegalEnumWrap resultCode = new GoodsDataIllegalEnumWrap();

        Goods goods = convertGoodsExcelImportBosToGoodsWithSku(importBos);
        // 检查规格名称是否存在重复
        boolean isOk = goodsService.goodsSpecProductService.isSpecNameOrValueRepeat(goods.getGoodsSpecs());
        if (!isOk) {
            resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_SPEC_K_V_REPEATED);
            return resultCode;
        }
        // 校验输入的规格组是否正确
        isOk = goodsService.goodsSpecProductService.isGoodsSpecProductDescRight(goods.getGoodsSpecProducts(), goods.getGoodsSpecs());
        if (!isOk) {
            resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_PRD_DESC_WRONG);
            return resultCode;
        }
        // 处理图片
        List<DownloadImageBo> downloadImageBos = filterGoodsImages(importBos);
        if (downloadImageBos.size() == 0) {
            resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_IMG_IS_WRONG);
            return resultCode;
        }
        GoodsVpuExcelImportBo bo = importBos.get(0);
        List<DownloadImageBo> descDownloadImageBos = new ArrayList<>(6);
        if (StringUtils.isNotBlank(bo.getGoodsDesc())) {
            String desc = filterGoodsDesc(bo.getGoodsDesc(), descDownloadImageBos);
            goods.setGoodsDesc(desc);
        }

        // 处理商家分类品牌
        try {
            // 此处为放入事务内，如果后面事务异常了那新增的品牌和商家分类不进行回滚
            int sortId = goodsSortService.fixGoodsImportGoodsSort(bo.getFirstSortName(), bo.getSecondSortName());
            goods.setSortId(sortId);
            if (StringUtils.isNotBlank(bo.getBrandName())) {
                Integer brandId = goodsBrandService.getOrAddBrand(bo.getBrandName());
                goods.setBrandId(brandId);
            }
        } catch (Exception e) {
            logger().warn("商品excel导入-插入-失败:" + e.getMessage());
            resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_FAIL);
            return resultCode;
        }

        // 这个地方是为了避免报异常
        goods.setCatId(0);

        transaction(() -> {
            try {

                // 处理用户指定的商品图片
                List<String> imgs = imageService.addImageToDbBatch(downloadImageBos);
                goods.setGoodsImg(imgs.remove(0));
                goods.setGoodsImgs(imgs);

                GoodsDataIllegalEnumWrap insertResult = goodsService.insert(goods);
                resultCode.setIllegalEnum(insertResult.getIllegalEnum());
                resultCode.setGoodsId(insertResult.getGoodsId());
            } catch (Exception e) {
                logger().debug("商品excel导入：" + e.getMessage());
                resultCode.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_FAIL);
            }
        });

        return resultCode;
    }

    /**
     * 将GoodsVpuExcelImportBo转换为Goods 准备进行插入或更新操作
     * @param importBos
     * @return
     */
    private Goods convertGoodsExcelImportBosToGoodsWithSku(List<GoodsVpuExcelImportBo> importBos) {
        GoodsVpuExcelImportBo goodsInfo = importBos.get(0);
        // 提取商品信息
        Goods goods = convertGoodsExcelImportBoToGoods(goodsInfo);
        // 提取sku信息
        List<GoodsSpecProduct> goodsSpecProducts = convertGoodsExcelImportBosToSku(importBos);
        goods.setGoodsSpecProducts(goodsSpecProducts);
        // 提取规格组规格值
        List<GoodsSpec> goodsSpecs = convertGoodsExcelImportBosToGoodsSpecs(importBos);
        goods.setGoodsSpecs(goodsSpecs);

        return goods;
    }

    /**
     * 转换为对应的sku
     * @param importBos
     * @return
     */
    private List<GoodsSpecProduct> convertGoodsExcelImportBosToSku(List<GoodsVpuExcelImportBo> importBos) {
        List<GoodsSpecProduct> skuList = new ArrayList<>(importBos.size());
        for (GoodsVpuExcelImportBo importBo : importBos) {
            GoodsSpecProduct product = new GoodsSpecProduct();
            product.setPrdPrice(importBo.getShopPrice());
            product.setPrdMarketPrice(importBo.getMarketPrice());
            product.setPrdCostPrice(importBo.getCostPrice());
            product.setPrdNumber(importBo.getStock());
            product.setPrdSn(importBo.getPrdSn());
            product.setPrdWeight(importBo.getPrdWeight());
            product.setPrdCodes(importBo.getPrdCodes());
            if (StringUtils.isNotBlank(importBo.getPrdDesc())) {
                importBo.setPrdDesc(importBo.getPrdDesc().replaceAll("：", GoodsSpecProductService.PRD_VAL_DELIMITER).replaceAll("；", GoodsSpecProductService.PRD_DESC_DELIMITER));
            }
            product.setPrdDesc(importBo.getPrdDesc());
            skuList.add(product);
        }
        return skuList;
    }

    /**
     * 转换对应的GoodsSpec
     * @param importBos
     * @return
     */
    private List<GoodsSpec> convertGoodsExcelImportBosToGoodsSpecs(List<GoodsVpuExcelImportBo> importBos) {
        List<GoodsSpec> goodsSpecs = new ArrayList<>(3);
        GoodsVpuExcelImportBo base = importBos.get(0);
        // 使用的是默认规格
        if (StringUtils.isBlank(base.getPrdDesc())) {
            return goodsSpecs;
        }

        // 解析对应的规格组K
        String[] specKvs = base.getPrdDesc().split(GoodsSpecProductService.PRD_DESC_DELIMITER);
        if (specKvs.length ==0) {
            return goodsSpecs;
        }

        for (String specKv : specKvs) {
            String[] kvs = specKv.split(GoodsSpecProductService.PRD_VAL_DELIMITER);
            if (kvs.length != 2) {
                return goodsSpecs;
            }
            GoodsSpec spec = new GoodsSpec(kvs[0], new ArrayList<>());
            goodsSpecs.add(spec);
        }
        Map<String, GoodsSpec> goodsSpecsMap = goodsSpecs.stream().collect(Collectors.toMap(GoodsSpec::getSpecName, Function.identity(),(oldVal,newVal)->newVal));
        // 解析规格组V
        for (GoodsVpuExcelImportBo importBo : importBos) {
            String desc = importBo.getPrdDesc();
            String[] kvStrs = desc.split(GoodsSpecProductService.PRD_DESC_DELIMITER);
            for (String kvStr : kvStrs) {
                String[] kv = kvStr.split(GoodsSpecProductService.PRD_VAL_DELIMITER);
                if (kv.length != 2) {
                    continue;
                }
                GoodsSpec goodsSpec = goodsSpecsMap.get(kv[0]);
                if (goodsSpec == null) {
                    continue;
                }
                boolean b = goodsSpec.getGoodsSpecVals().stream().anyMatch(specVal -> StringUtils.equals(specVal.getSpecValName(), kv[1]));
                if (!b) {
                    goodsSpec.getGoodsSpecVals().add(new GoodsSpecVal(kv[1]));
                }
            }
        }
        return goodsSpecs;
    }

    /**
     * GoodsVpuExcelImportBo提取商品表使用信息，并转换为Goods类
     * @param goodsInfo GoodsVpuExcelImportBo
     * @return
     */
    private Goods convertGoodsExcelImportBoToGoods(GoodsVpuExcelImportBo goodsInfo) {
        Goods goods = new Goods();
        goods.setShareConfig(Util.toJson(new GoodsSharePostConfig()));
        goods.setGoodsName(goodsInfo.getGoodsName());
        goods.setGoodsSn(goodsInfo.getGoodsSn());
        goods.setGoodsAd(goodsInfo.getGoodsAd());
        goods.setIsOnSale(goodsInfo.getIsOnSale());
        if (GoodsConstant.OFF_SALE.equals(goodsInfo.getIsOnSale())) {
            goods.setSaleType(GoodsConstant.IN_STOCK);
        }
        goods.setLimitBuyNum(goodsInfo.getLimitBuyNum());
        goods.setUnit(goodsInfo.getUnit());
        goods.setGoodsImg(goodsInfo.getGoodsImgsStr());
        goods.setDeliverPlace(goodsInfo.getDeliverPlace());
        goods.setGoodsDesc(goodsInfo.getGoodsDesc());
        return goods;
    }

    /**
     * 处理外链图片集
     * @param importBos 待处理导入对象
     * @return 待入库
     */
    private List<DownloadImageBo> filterGoodsImages(List<GoodsVpuExcelImportBo> importBos) {
        List<DownloadImageBo> downloadImageBos = new ArrayList<>(5);
        List<String> imgNames = new ArrayList<>(downloadImageBos.size());
        Map<String, String> imgNameUrlMap = new HashMap<>(downloadImageBos.size());

        // 判断哪些在数据库里面已经存在
        for (GoodsVpuExcelImportBo importBo : importBos) {
            String goodsImgsStr = importBo.getGoodsImgsStr();
            if (goodsImgsStr == null) {
                continue;
            }
            String[] imgUrls = goodsImgsStr.replaceAll("；", ";").split(";");
            for (String imgUrl : imgUrls) {
                if (imgUrl.lastIndexOf("/") == -1) {
                    continue;
                }
                String imgName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
                imgNames.add(imgName);
                imgNameUrlMap.put(imgName, imgUrl);
            }
        }
        List<UploadedImageRecord> imgsByImgOrgNames = imageService.getImgsByImgOrgNames(imgNames);
        for (UploadedImageRecord record : imgsByImgOrgNames) {
            imgNameUrlMap.remove(record.getImgOrigFname());
            DownloadImageBo bo = new DownloadImageBo();
            bo.setAlreadyHas(true);
            bo.setRelativeFilePath(record.getImgPath());
            downloadImageBos.add(bo);
        }

        for (String url : imgNameUrlMap.values()) {
            DownloadImageBo bo = downLoadImg(url);
            if (bo != null) {
                downloadImageBos.add(bo);
            }
        }
        if (downloadImageBos.size() > 5) {
            downloadImageBos = downloadImageBos.subList(0, 5);
        }

        return downloadImageBos;
    }

    /**
     * 过滤商品描述信息
     * @param goodsDesc 商品描述信息
     * @return
     */
    private String filterGoodsDesc(String goodsDesc, List<DownloadImageBo> downloadImageBos) {
        String retStr = RegexUtil.cleanBodyContent(goodsDesc);
        if (retStr == null) {
            return retStr;
        }
//        retStr = filterImgTag(goodsDesc, downloadImageBos);
//        retStr = filterBgUrlImg(retStr, downloadImageBos);
        return retStr;
    }

    /**
     * 过滤img标签
     * @param goodsDesc
     * @param downloadImageBos
     * @return
     */
    private String filterImgTag(String goodsDesc, List<DownloadImageBo> downloadImageBos) {
        Pattern imgTagPattern = RegexUtil.getImgTagPattern();
        Matcher matcher = imgTagPattern.matcher(goodsDesc);
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while (matcher.find()) {
            int groupStart = matcher.start();
            int groupEnd = matcher.end();
            String outerImgLink = matcher.group(1);
            String imgTagStr = matcher.group();

            if (outerImgLink != null) {
                DownloadImageBo bo = downLoadImg(outerImgLink);
                if (bo != null) {
                    downloadImageBos.add(bo);
                    imgTagStr = imgTagStr.replace(outerImgLink, bo.getImgUrl());
                } else {
                    imgTagStr = "";
                }
            } else {
                imgTagStr = "";
            }
            sb.append(goodsDesc, index, groupStart);
            sb.append(imgTagStr);
            index = groupEnd;
        }
        sb.append(goodsDesc, index, goodsDesc.length());
        return sb.toString();
    }

    /**
     * 过滤background-image 和background 的 url(xxxx)
     * @param goodsDesc
     * @param downloadImageBos
     * @return
     */
    private String filterBgUrlImg(String goodsDesc, List<DownloadImageBo> downloadImageBos) {
        Pattern bgUrlPattern = RegexUtil.getBgUrlPattern();
        Matcher matcher = bgUrlPattern.matcher(goodsDesc);
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while (matcher.find()) {
            int groupStart = matcher.start();
            int groupEnd = matcher.end();
            String bgUrlStr = matcher.group();
            String outerImgLink = matcher.group(1);
            if (outerImgLink == null) {
                outerImgLink = matcher.group(2);
            }
            if (outerImgLink != null) {
                DownloadImageBo bo = downLoadImg(outerImgLink);
                if (bo != null) {
                    downloadImageBos.add(bo);
                    bgUrlStr = bgUrlStr.replace(outerImgLink, bo.getImgUrl());
                } else {
                    bgUrlStr = "";
                }
            } else {
                bgUrlStr = "";
            }
            sb.append(goodsDesc, index, groupStart);
            sb.append(bgUrlStr);
            index = groupEnd;
        }
        sb.append(goodsDesc, index, goodsDesc.length());
        return sb.toString();
    }

    private final Integer IMAGE_WIDTH = 800;
    private final Integer IMAGE_HEIGHT = 800;

    /**
     * 下载并上传外链图片
     * @param imgUrl 外链地址
     * @return null 无法处理
     */
    private DownloadImageBo downLoadImg(String imgUrl) {
        if (StringUtils.isBlank(imgUrl)) {
            return null;
        }
        imgUrl = imgUrl.replaceAll("\\n", "").trim();
        try {
            return imageService.downloadImgAndUpload(imgUrl, IMAGE_WIDTH, IMAGE_HEIGHT);
        } catch (Exception e) {
            logger().warn("商品excel导入-下载外链图片-失败:" + e.getMessage());
        }
        return null;
    }

    /**
     * 生成文件存储在yupYun上的文件位置
     * @param shopId   店铺id
     * @param fileName 文件名
     * @return 文件相对位置
     */
    private String createFilePath(Integer shopId, String fileName) {
        return new StringBuilder().append("upload/").append("excel/").append(shopId).append("/")
            .append(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE))
            .append("_").append(fileName).toString();
    }

    @Data
    private class GoodsImportAssistParam {
        private Integer batchId;
        private Integer limitNum;
        private Integer goodsCount;
        private boolean isUpdate;
    }
}
