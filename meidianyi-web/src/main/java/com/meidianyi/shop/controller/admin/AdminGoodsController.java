package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionRecommendGoodsParam;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.goods.*;
import com.meidianyi.shop.service.pojo.shop.goods.goodsanalysis.GoodsAnalysisListParam;
import com.meidianyi.shop.service.pojo.shop.goods.goodsanalysis.GoodsAnalysisListVo;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpec;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecProduct;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecVal;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsGroupListMpParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpVo;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2019年07月08日
 */
@RestController
public class AdminGoodsController extends AdminBaseController {

    /**
     * 获取全品牌，全部标签，商家分类数据,平台分类数据
     * param.needXX=true表示需要查询对应的数据，否则对应数不会返回
     * param.needGoodsNum = true 表示需要根据商品过滤平台和商家分类，并计算分类对应的商品数量
     * param.selectType : 1 以商品为统计对象，2以商品规格为统计对象
     * param.isOnSale : 1在售，0下架
     * param.isSaleOut : true 查询售罄商品
     *
     * @param {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsFilterItemInitParam}
     * @return JsonResult
     */
    @PostMapping("/api/admin/goods/filterItem/list")
    public JsonResult getGoodsFilterItem(@RequestBody GoodsFilterItemInitParam param) {
        param.setNeedSysCategory(false);
        GoodsFilterItemInitVo vo = shop().goods.getGoodsFilterItem(param);
        return success(vo);
    }

    /**
     * 根据平台分类id获取其所有的祖先节点数据，
     * （修改商品回显商品平台分类数据的时候使用到）
     *
     * @param catId 平台分类id
     * @return LinkedList:有序链表，按照祖先顺序排列，
     */
    @GetMapping("/api/admin/goods/getSysCatParents")
    public JsonResult getSysCatParents(Integer catId) {
        LinkedList<Integer> parentIds = saas.sysCate.findParentIdsByChildId(catId);
        return success(parentIds);
    }

    /**
     * 商品分页查询
     *
     * @param param 过滤条件
     * @return
     */
    @PostMapping("/api/admin/goods/list")
    public JsonResult getPageList(@RequestBody GoodsPageListParam param) {
        param.setSelectType(GoodsPageListParam.GOODS_LIST);
        PageResult<GoodsPageListVo> pageList = shop().goods.getPageList(param);
        return success(pageList);
    }

    /**
     * 商品选择弹窗，根据过滤条件查询对应商品id集合
     *
     * @param param
     * @return
     */
    @PostMapping("/api/admin/goods/listAllIds")
    public JsonResult getGoodsIdsListAll(@RequestBody GoodsPageListParam param) {
        param.setSelectType(GoodsPageListParam.GOODS_LIST);
        List<Integer> goodsIds = shop().goods.getGoodsIdsListAll(param);
        return success(goodsIds);
    }

    /**
     * 商品规格分页查询
     */
    @PostMapping("/api/admin/goods/product/list")
    public JsonResult getProductPageList(@RequestBody GoodsPageListParam param) {
        param.setSelectType(GoodsPageListParam.GOODS_PRD_LIST);
        return success(shop().goods.getProductPageList(param));
    }

    /**
     * 商品规格分页查询id
     * @param param
     * @return
     */
    @PostMapping("/api/admin/goods/product/listAllIds")
    public JsonResult getProductIdsListAll(@RequestBody GoodsPageListParam param) {
        param.setSelectType(GoodsPageListParam.GOODS_PRD_LIST);
        return success(shop().goods.getProductIdsListAll(param));
    }

    /**
     * 查询商品和其所有下属规格信息
     *
     * @param goodsIdParams
     * @return
     */
    @PostMapping("/api/admin/goods/info/list")
    public JsonResult getGoodsAndProductsByIds(@RequestBody GoodsIdParams goodsIdParams) {
        return success(shop().goods.getGoodsAndProductsByGoodsIds(goodsIdParams.getGoodsIds()));
    }

    /**
     * 通过规格1ds查询规格信息
     *
     * @param goodsIdParams guigeids
     * @return
     */
    @PostMapping("/api/admin/goods/product/info/list")
    public JsonResult getProductsByIds(@RequestBody GoodsIdParams goodsIdParams) {
        return success(shop().goods.goodsSpecProductService.getProductsByProductIds(goodsIdParams.getProductId()));
    }

    /**
     * 查询商品所有规则信息
     *
     * @param goodsId 商品ID
     * @return 规则信息
     */
    @GetMapping("/api/admin/goods/product/all/{goodsId}")
    public JsonResult getAllProductListByGoodsId(@PathVariable("goodsId") Integer goodsId) {
        List<GoodsProductVo> productVos = shop().goods.getAllProductListByGoodsId(goodsId);
        return success(productVos);
    }

    /**
     * 查询店铺商品新增时相关的通用配置
     * @return
     */
    @GetMapping("/api/admin/goods/common/cfg")
    public JsonResult selectGoodsCommonCfg(){
        return success(shop().config.shopCommonConfigService.getGoodsCommonConfig());
    }
    /**
     * 商品新增
     *
     * @param goods 商品参数
     * @return 操作结果
     */
    @PostMapping("/api/admin/goods/add")
    public JsonResult insert(@RequestBody Goods goods) {
        //如果商品使用默认的规格形式，也需要根据默认形式设置一个GoodsSpecProducts参数
        if (goods.getGoodsSpecProducts() == null || goods.getGoodsSpecProducts().size() == 0) {
            return fail(JsonResultCode.GOODS_SPEC_ATTRIBUTE_SPEC_K_V_CONFLICT);
        }

        //判断商品名称是否为空
        if (StringUtils.isBlank(goods.getGoodsName())) {
            return fail(JsonResultCode.GOODS_NAME_IS_NULL);
        }

        //判断平台分类是否为空
//        if (goods.getCatId() == null) {
//            return fail(JsonResultCode.GOODS_SORT_ID_IS_NULL);
//        }
        goods.setCatId(0);

        //判断商品主图是否为空
        if (StringUtils.isBlank(goods.getGoodsImg())) {
            return fail(JsonResultCode.GOODS_SORT_NAME_IS_NULL);
        }

        GoodsService goodsService = shop().goods;
        //判断商品特定等级会员卡的价格是否存在大于对应规格价钱的情况
        if (!goodsService.isGradePrdPriceOk(goods)) {
            return fail(JsonResultCode.GOODS_NAME_IS_NULL);
        }

        GoodsDataIIllegalEnum code = shop().goodsWrap.insertWithLock(shopId(),goods);
        if (GoodsDataIIllegalEnum.GOODS_NAME_EXIST.equals(code)) {
            return fail(JsonResultCode.GOODS_NAME_EXIST);
        }
        if (GoodsDataIIllegalEnum.GOODS_SN_EXIST.equals(code)) {
            return fail(JsonResultCode.GOODS_SN_EXIST);
        }
        if (GoodsDataIIllegalEnum.GOODS_PRD_SN_EXIST.equals(code)) {
            return fail(JsonResultCode.GOODS_SPEC_PRD_SN_EXIST);
        }
        if (GoodsDataIIllegalEnum.GOODS_NUM_FETCH_LIMIT_NUM.equals(code)) {
            return fail(JsonResultCode.GOODS_NUM_FETCH_LIMIT_NUM);
        }
        if (GoodsDataIIllegalEnum.GOODS_PRD_CODES_EXIST.equals(code)) {
            return fail(JsonResultCode.GOODS_PRD_CODES_EXIST);
        }

        if (GoodsDataIIllegalEnum.GOODS_OK.equals(code)) {
            return success();
        } else {
            return fail();
        }
    }

    /**
     * 查询字段值在数据库内是否重复
     *
     * @param goodsColumnCheckExistParam
     * @return {@link com.meidianyi.shop.common.foundation.data.JsonResult}
     */
    @PostMapping("/api/admin/goods/columns/exist")
    public JsonResult isColumnValueExist(@RequestBody GoodsColumnCheckExistParam goodsColumnCheckExistParam) {
        boolean isExist = shop().goods.isColumnValueExist(goodsColumnCheckExistParam);
        if (isExist) {
            return success();
        } else {
            return fail();
        }
    }

    /**
     * 商品批处理操作
     * @param param
     * @return
     */
    @PostMapping("/api/admin/goods/batch")
    public JsonResult batchOperate(@RequestBody GoodsBatchOperateParam param) {
        // 上下价处理，调用的方法同步es时会进行同步处理，而其他的批量处理是异步的
        if (param.getIsOnSale() != null) {
            shop().goods.batchIsOnSaleOperate(param);
        } else {
            shop().goods.batchOperate(param);
        }
        return success();
    }

    /**
     * 单独修改规格对应的价格或数量
     * @param param PrdPriceNumberParam
     * @return JsonResult
     */
    @PostMapping("/api/admin/goodsPrd/updatePriceNumber")
    public JsonResult updateGoodsPrdPriceNumbers(@RequestBody PrdPriceNumberParam param) {
        shop().goods.updateGoodsPrdPriceNumbers(param);
        return success();
    }

    /**
     * 多规格商品修改数量
     * @param param
     * @return
     */
    @PostMapping("/api/admin/goodsPrd/updateGoodsPrdNumbers")
    public JsonResult updateGoodsPrdNumbers(@RequestBody GoodsPrdNumEditParam param) {
        shop().goods.updateGoodsPrdNumbers(param);
        return success();
    }

    @PostMapping("/api/admin/goods/delete")
    public JsonResult delete(@RequestBody GoodsBatchOperateParam param) {
        if (param.getGoodsIds() == null || param.getGoodsIds().size() == 0) {
            return fail(JsonResultCode.GOODS_ID_IS_NULL);
        }

        shop().goods.delete(param);
        return success();
    }

    @PostMapping("/api/admin/goods/update")
    public JsonResult update(@RequestBody Goods goods) {
        if (goods.getGoodsSpecProducts() == null || goods.getGoodsSpecProducts().size() == 0) {
            return fail(JsonResultCode.GOODS_SPEC_ATTRIBUTE_SPEC_K_V_CONFLICT);
        }

        //判断商品名称是否为空
        if (StringUtils.isBlank(goods.getGoodsName())) {
            return fail(JsonResultCode.GOODS_NAME_IS_NULL);
        }

        //判断平台分类是否为空
//        if (goods.getCatId() == null) {
//            return fail(JsonResultCode.GOODS_SORT_ID_IS_NULL);
//        }
        goods.setCatId(0);

        //判断商品主图是否为空
        if (StringUtils.isBlank(goods.getGoodsImg())) {
            return fail(JsonResultCode.GOODS_SORT_NAME_IS_NULL);
        }
        //ps:此处省略规格组，规格名值,因为加上后出现过操作超时的现象
        GoodsDataIIllegalEnum code = shop().goodsWrap.updateWithLock(shopId(),goods);
        if (GoodsDataIIllegalEnum.GOODS_NAME_EXIST.equals(code)) {
            return fail(JsonResultCode.GOODS_NAME_EXIST);
        }
        if (GoodsDataIIllegalEnum.GOODS_SN_EXIST.equals(code)) {
            return fail(JsonResultCode.GOODS_SN_EXIST);
        }
        if (GoodsDataIIllegalEnum.GOODS_PRD_SN_EXIST.equals(code)) {
            return fail(JsonResultCode.GOODS_SPEC_PRD_SN_EXIST);
        }
        if (GoodsDataIIllegalEnum.GOODS_PRD_CODES_EXIST.equals(code)) {
            return fail(JsonResultCode.GOODS_PRD_CODES_EXIST);
        }

        if (GoodsDataIIllegalEnum.GOODS_OK.equals(code)) {
            return success();
        } else {
            return fail();
        }
    }

    /**
     * 根据id值查询商品信息
     *
     * @param goods 商品信息
     * @return {@link com.meidianyi.shop.common.foundation.data.JsonResult}
     */
    @PostMapping("/api/admin/goods/select")
    public JsonResult select(@RequestBody Goods goods) {
        if (goods.getGoodsId() == null) {
            return fail(JsonResultCode.GOODS_ID_IS_NULL);
        }

        GoodsVo goodsVo = shop().goods.select(goods.getGoodsId());


        return success(goodsVo);
    }
    /**
     * 根据id值集合分页查询商品信息
     *
     * @param param
     * @return
     */
    @PostMapping("/api/admin/goods/selectPage")
    public JsonResult selectPage(@RequestBody DistributionRecommendGoodsParam param) {
        if (StringUtils.isBlank(param.getRecommendGoodsId())) {
            return fail(JsonResultCode.GOODS_ID_IS_NULL);
        }

        PageResult<GoodsVo> result = shop().goods.selectPage(param);

        return success(result);
    }

    /**
     * 获得商品的小程序跳转图片
     *
     * @param goodsId 商品id
     * @return 图片绝对地址和跳转链接相对地址
     */
    @GetMapping("/api/admin/goods/qrCode/get")
    public JsonResult getQrCode(Integer goodsId) {
        if (goodsId == null) {
            return fail(JsonResultCode.GOODS_ID_IS_NULL);
        }
        GoodsQrCodeVo vo = shop().goods.getGoodsQrCode(goodsId);
        return success(vo);
    }

    /**
     * 更新数据时判断传入的商品名称、货号，sku码和规格名值是否重复。
     *
     * @param goods 商品
     * @return {@link JsonResult#getError()}!=0表示存在重复
     */
    private JsonResult columnValueExistCheckForUpdate(Goods goods) {
        GoodsService goodsService = shop().goods;

        GoodsColumnCheckExistParam gcep = new GoodsColumnCheckExistParam();
        gcep.setColumnCheckFor(GoodsColumnCheckExistParam.ColumnCheckForEnum.E_GOODS);

        //检查商品名称是否重复
        gcep.setGoodsName(goods.getGoodsName());
        gcep.setGoodsId(goods.getGoodsId());
        if (goodsService.isColumnValueExist(gcep)) {
            return fail(JsonResultCode.GOODS_NAME_EXIST);
        }
        gcep.setGoodsName(null);
        gcep.setGoodsId(null);

        //用户输入了商品货号则进行检查是否重复
        if (goods.getGoodsSn() != null) {
            gcep.setGoodsSn(goods.getGoodsSn());
            gcep.setGoodsId(goods.getGoodsId());
            if (goodsService.isColumnValueExist(gcep)) {
                return fail(JsonResultCode.GOODS_SN_EXIST);
            }
            gcep.setGoodsSn(null);
            gcep.setGoodsId(null);
        }

        gcep.setColumnCheckFor(GoodsColumnCheckExistParam.ColumnCheckForEnum.E_GOODS_SPEC_PRODUCTION);
        //检查sku sn是否重复
        for (GoodsSpecProduct goodsSpecProduct : goods.getGoodsSpecProducts()) {
            if (!StringUtils.isBlank(goodsSpecProduct.getPrdSn())) {
                gcep.setPrdSn(goodsSpecProduct.getPrdSn());
                gcep.setPrdId(goodsSpecProduct.getPrdId());
                if (goodsService.isColumnValueExist(gcep)) {
                    return fail(JsonResultCode.GOODS_SPEC_PRD_SN_EXIST);
                }
                gcep.setPrdId(null);
            }
        }

        //检查规格名称和值是否存在重复
        return isSpecNameOrValueRepeat(goods.getGoodsSpecs());
    }

    /**
     * 判断商品规格名和规格值是否内部自重复
     * @param specs 商品规格
     * @return {@link JsonResult#getError()}!=0表示存在重复
     */
    private JsonResult isSpecNameOrValueRepeat(List<GoodsSpec> specs) {
        //在选择默认规格的情况下该字段可以是空
        if (specs == null) {
            return success();
        }

        Map<String, Object> specNameRepeatMap = new HashMap<>(specs.size());

        for (GoodsSpec goodsSpec : specs) {

            specNameRepeatMap.put(goodsSpec.getSpecName(), null);
            //检查同一规格下规格值是否重复
            List<GoodsSpecVal> goodsSpecVals = goodsSpec.getGoodsSpecVals();
            if (goodsSpecVals == null) {
                continue;
            }
            Map<String, Object> specValRepeatMap = new HashMap<>(goodsSpecVals.size());
            for (GoodsSpecVal goodsSpecVal : goodsSpecVals) {
                specValRepeatMap.put(goodsSpecVal.getSpecValName(), null);
            }
            if (specValRepeatMap.size() != goodsSpecVals.size()) {
                return fail(JsonResultCode.GOODS_SPEC_VAL_REPETITION);
            }
        }
        if (specs.size() != specNameRepeatMap.size()) {
            return fail(JsonResultCode.GOODS_SPEC_NAME_REPETITION);
        }

        return success();
    }

    /**
     * 取将要导出的列数
     */
    @PostMapping("/api/admin/goods/export/rows")
    public JsonResult getExportTotalRows(@RequestBody @Valid GoodsPageListParam param) {
        return success(shop().goods.getExportGoodsListSize(param));
    }

    @PostMapping("/api/admin/goods/export")
    public void export(@RequestBody @Valid GoodsExportParam param, HttpServletResponse response) {
        shop().config.goodsCfg.setGoodsExportList(param.getColumns());
        Workbook workbook = shop().goods.exportGoodsList(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.GOODS_EXPORT_FILE_NAME, "excel", "excel") + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook, fileName, response);
//        test(param,response);
    }

    public void test(GoodsExportParam param, HttpServletResponse response){
        long startTime = System.currentTimeMillis();
        String[] imgs = new String[]{"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1590215985441&di=43f423f1d30c4a0c8db2c03c75715aa0&imgtype=0&src=http%3A%2F%2Fa1.att.hudong.com%2F05%2F00%2F01300000194285122188000535877.jpg","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1590215985442&di=d4da00b6b455fa70cae9567bbcf055b6&imgtype=0&src=http%3A%2F%2Fb2-q.mafengwo.net%2Fs5%2FM00%2F91%2F06%2FwKgB3FH_RVuATULaAAH7UzpKp6043.jpeg","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1590215985442&di=e92aeec7f8af1fa8773c7724264e2854&imgtype=0&src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201409%2F08%2F20140908130732_kVXzh.jpeg","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1590215985442&di=3c452179f0b8e5a947cc43f28a4a82aa&imgtype=0&src=http%3A%2F%2Fa0.att.hudong.com%2F64%2F76%2F20300001349415131407760417677.jpg","https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1590215985442&di=bc61e47414f3bcdd7ea1ec6a868c89ee&imgtype=0&src=http%3A%2F%2Fa2.att.hudong.com%2F36%2F48%2F19300001357258133412489354717.jpg"};

        List<String> imgList = Arrays.stream(imgs).collect(Collectors.toList());

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(getLang(), workbook);
        List<GoodsExportVo> goodsList = new ArrayList<>();
        int count = shop().goods.selectGoodsCount();
        String timeStr = DateUtils.getLocalDateFullTightFormat();
        int max = 2000;
        for (int i = 1; i <= max; i++) {
            GoodsExportVo vo = new GoodsExportVo();
            vo.setSortNameParent("日化用品");
            vo.setSortNameChild("面部护肤");
            vo.setGoodsName("导入测试2-"+i);
            vo.setBrandName("迪奥");
            vo.setGoodsSn(String.format("G%s-%08d",timeStr,count+i));
            vo.setPrdDesc(null);
            vo.setPrdSn(String.format("Prd%s-%08d",timeStr,count+i));
            vo.setGoodsAd("广告");
            vo.setPrdNumber(10);
            vo.setPrdMarketPrice(BigDecimal.valueOf(100));
            vo.setShopPrice(BigDecimal.valueOf(50));
            vo.setPrdCostPrice(BigDecimal.valueOf(20));
            vo.setIsOnSale((byte) 1);
            vo.setLimitBuyNum(1);
            vo.setGoodsWeight(BigDecimal.valueOf(1));
            vo.setUnit("瓶");
            vo.setGoodsImg(imgs[i%imgs.length]);
            vo.setImgUrl(String.join(";",imgList));
            vo.setPrdCodes(String.format("code%s-%08d",timeStr,count+i));
            vo.setDeliverPlace("北京");
            goodsList.add(vo);
        }
        long excelStartTime = System.currentTimeMillis();
        excelWriter.writeModelList(goodsList, GoodsExportVo.class, param.getColumns());
        export2Excel(workbook, "goods2", response);
        long endTime = System.currentTimeMillis();
        System.out.println("excel 转换时间："+(endTime - excelStartTime)/1000);
        System.out.println("总时间："+(endTime-startTime)/1000);
    }

    /**
     * 根据条件获取商品数量
     * @param param 商品数量获取条件
     * @return 商品数量
     */
    @PostMapping("/api/admin/goods/num")
    public JsonResult getGoodsNumByCondition(@RequestBody GoodsNumCountParam param){
        return success(shop().goods.getGoodsNum(param));
    }

    @PostMapping("/api/admin/goods/nums")
    public JsonResult getGoodsNumByCondition(@RequestBody GoodsNumCountParamModel param) {
        return success(shop().goods.getGoodsNum(param.getGoodsNumCountParams()));
    }


    /**
     * 小程序装修商品列表模块数据接口
     * @param goodsListMpParam
     */
    @PostMapping("/api/admin/goods/mp/list")
    public JsonResult getGoodsList(@RequestBody GoodsListMpParam goodsListMpParam) {
        goodsListMpParam.setFromPage(EsGoodsConstant.GOODS_LIST_PAGE);
        PageResult<GoodsListMpBo> pageIndexGoodsList = shop().goodsMp.getPageIndexGoodsList(goodsListMpParam, null);
        List<? extends GoodsListMpVo> goodsList = pageIndexGoodsList.getDataList();
        return success(goodsList);
    }

    /**
     * 装修-商品分组-获取数据接口
     * @param param 分组过滤条件
     * @return JsonResult
     */
    @PostMapping("/api/admin/goods/mp/group/list")
    public JsonResult getGoodsGroupList(@RequestBody GoodsGroupListMpParam param) {
        if (param.getSortGroupArr() == null || param.getSortGroupArr().size() == 0) {
            return fail();
        }
        param.setUserId(null);
        if (!GoodsConstant.GOODS_GROUP_LIST_TOP_POSITION.equals(param.getPositionStyle())||!GoodsConstant.GOODS_GROUP_LIST_SHOW_ALL_COLUMN.equals(param.getGroupDisplay())) {
            param.setSortGroupArr(param.getSortGroupArr().subList(0,1));
        }

        return success(shop().goodsMp.goodsGroupMpService.getGoodsGroupList(param));
    }

    /**
     * 小程序test
     *
     */
    @PostMapping("/api/admin/goods/test")
    public void test() {
        shop().shopTaskService.wechatTaskService.beginDailyTask();
    }

    /**
     * 热销商品统计列表
     * @param param
     * @return
     */
    @PostMapping("/api/admin/goods/summary/list")
    public JsonResult goodsSummaryList(@RequestBody GoodsAnalysisListParam param) {
        PageResult<GoodsAnalysisListVo> goodsSummaryList = shop().goodsAnalysisService.getGoodsAnalysisPageList(param);
        return this.success(goodsSummaryList);
    }
}
