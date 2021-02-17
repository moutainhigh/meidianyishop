package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.sort.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2019年07月01日
 */
@RestController
public class AdminGoodsSortController extends AdminBaseController {

    /**
     * 商品分类查询，未分页
     * @param  {@link com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsSortListParam}
     * @return {@link com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsSortListVo}集合
     */
    @PostMapping("/api/admin/goods/sort/list")
    public JsonResult getList(@RequestBody GoodsSortListParam param) {
        if (!GoodsConstant.NORMAL_SORT.equals(param.getType()) && !GoodsConstant.RECOMMEND_SORT.equals(param.getType())) {
            param.setType(GoodsConstant.NORMAL_SORT);
        }
        return success(shop().goods.goodsSort.getSortList(param));
    }

    /**
     * 普通分类选择框下拉列表（非树形数据，仅返回一级数据）
     * @return {@link com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsSortSelectListVo}集合
     */
    @GetMapping("/api/admin/goods/sort/select/list")
    public JsonResult getSelectList(){
        return success(shop().goods.goodsSort.getSelectList());
    }

    /**
     *  普通分类新增
     * @param param {@link GoodsNormalSortParam}
     */
    @PostMapping("/api/admin/goods/sort/add")
    public JsonResult insert(@RequestBody GoodsNormalSortParam param) {
        if (param.getSortName()==null) {
            return fail(JsonResultCode.GOODS_SORT_NAME_IS_NULL);
        }

        boolean isExist = shop().goods.goodsSort.isSortNameExist(null,param.getSortName());
        if (isExist) {
            return fail(JsonResultCode.GOODS_SORT_NAME_EXIST);
        }

        shop().goods.goodsSort.insertNormal(param);

        return success();
    }

    /**
     * 普通分类修改
     * @param param {@link GoodsNormalSortParam}
     */
    @PostMapping("/api/admin/goods/sort/update")
    public JsonResult update(@RequestBody GoodsNormalSortParam param) {
        if (param.getSortId() == null) {
            return fail(JsonResultCode.GOODS_SORT_ID_IS_NULL);
        }
        boolean isExist = shop().goods.goodsSort.isSortNameExist(param.getSortId(),param.getSortName());
        if (isExist) {
            return fail(JsonResultCode.GOODS_SORT_NAME_EXIST);
        }
        shop().goods.goodsSort.updateNormal(param);
        return success();
    }

    /**
     * 根据id获取普通商家分类
     * @param sortId 普通商家分类id
     */
    @GetMapping("/api/admin/goods/sort/get/{sortId}")
    public JsonResult getSort(@PathVariable("sortId") Integer sortId) {
        if (sortId == null) {
            return fail(JsonResultCode.GOODS_SORT_ID_IS_NULL);
        }
        return success(shop().goods.goodsSort.getNormalSort(sortId));
    }

    /**
     *  推荐商家分类批量新增接口
     * @param param {@link com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsRecommendSortParam} 推荐分类
     */
    @PostMapping("/api/admin/goods/sort/recommend/add")
    public JsonResult insertRecommendSort(@RequestBody GoodsRecommendSortParam param) {

        // 推荐分类子分类不可为空
        if (param.getChildren() == null || param.getChildren().size() == 0) {
            return fail(JsonResultCode.GOODS_RECOMMEND_SORT_CHILDREN_NOT_NULL);
        }

        //如果提交的内容内部有重复
        List<String> sortsNames = new ArrayList<>();
        sortsNames.add(param.getSortName());
        if (param.getChildren() != null) {
            sortsNames.addAll(param.getChildren().stream().map(GoodsRecommendSortParam.GoodsRecommendSortChild::getSortName).collect(Collectors.toList()));
        }
        // 判断传入的数据是否存在名称重复
        if (isSortNamesRepeat(sortsNames)) {
            return fail(JsonResultCode.GOODS_SORT_NAME_EXIST);
        }

        //判断内容和数据库内有重复
        if (shop().goods.goodsSort.isSortNameExist(sortsNames)) {
            return fail(JsonResultCode.GOODS_SORT_NAME_EXIST);
        }

        shop().goods.goodsSort.insertRecommendSort(param);
        return success();
    }

    /**
     * 推荐商品批量更新接口
     * @param param {@link com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsRecommendSortParam} 推荐分类
     */
    @PostMapping("/api/admin/goods/sort/recommend/update")
    public JsonResult updateRecommendSort(@RequestBody GoodsRecommendSortParam param){
        if (param.getSortId() == null) {
            return fail(JsonResultCode.GOODS_SORT_ID_IS_NULL);
        }

        //如果提交的内容内部有重复
        List<String> sortsNames = new ArrayList<>();
        sortsNames.add(param.getSortName());
        if (param.getChildren() != null) {
            sortsNames.addAll(param.getChildren().stream().map(GoodsRecommendSortParam.GoodsRecommendSortChild::getSortName).collect(Collectors.toList()));
        }
        // 判断传入的数据是否存在名称重复
        if (isSortNamesRepeat(sortsNames)) {
            return fail(JsonResultCode.GOODS_SORT_NAME_EXIST);
        }

        //判断子分类内容和数据库内有重复
        if (shop().goods.goodsSort.isSortNameExist(param.getSortId(),sortsNames)) {
            return fail(JsonResultCode.GOODS_SORT_NAME_EXIST);
        }

        shop().goods.goodsSort.updateRecommendSort(param);
        return success();
    }

    @GetMapping("/api/admin/goods/sort/recommend/get/{sortId}")
    public JsonResult getRecommendSort(@PathVariable("sortId") Integer sortId) {
        if (sortId == null) {
            return fail(JsonResultCode.GOODS_SORT_ID_IS_NULL);
        }
        return success(shop().goods.goodsSort.getRecommendSort(sortId));
    }

    /**
     * 删除商家分类
     * @param sortId 分类id
     */
    @GetMapping("/api/admin/goods/sort/delete/{sortId}")
    public JsonResult delete(@PathVariable("sortId") Integer sortId) {
        if (sortId == null) {
            return fail(JsonResultCode.GOODS_SORT_ID_IS_NULL);
        }
        shop().goods.goodsSort.delete(sortId);
        return success();
    }

    /**
     * 判断字符串集合内是否存在重复数据
     * @param sortNames 字符串集合
     * @return true 存在， false 不存在
     */
    private boolean isSortNamesRepeat(List<String> sortNames){
        Set<String> set = new HashSet<>(sortNames);
        return set.size()!=sortNames.size();
    }

    /**
     * 设置推荐分类配置
     * @param goodsRecommendSortConfig 推荐配置参数
     */
    @PostMapping("/api/admin/goods/sort/setConfig")
    public JsonResult setRecommendSortConfig(@RequestBody GoodsRecommendSortConfig goodsRecommendSortConfig){
        shop().config.recommendSortConfigService.setRecommendSortConfig(goodsRecommendSortConfig);
        return success();
    }

    /**
     * 获取推荐分类配置
     * @return 推荐分类配置项 {@link com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsRecommendSortConfig}
     */
    @GetMapping("/api/admin/goods/sort/getConfig")
    public JsonResult getRecommendSortConfig() {
        return success(shop().config.recommendSortConfigService.getRecommendSortConfig());
    }

}
