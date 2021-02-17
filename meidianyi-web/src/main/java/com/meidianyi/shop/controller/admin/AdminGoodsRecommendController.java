package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.RecommendGoodsRecord;
import com.meidianyi.shop.service.pojo.shop.goods.recommend.GoodsRecommend;
import com.meidianyi.shop.service.pojo.shop.goods.recommend.GoodsRecommendInsertParam;
import com.meidianyi.shop.service.pojo.shop.goods.recommend.GoodsRecommendPageListParam;
import com.meidianyi.shop.service.pojo.shop.goods.recommend.GoodsRecommendUpdateParam;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author 黄荣刚
 * @date 2019年7月9日
 */
@RestController
@RequestMapping("/api/admin/goods")
public class AdminGoodsRecommendController extends AdminBaseController {

  /**
   * 分页查询商品推荐列表，支持根据名称模糊查询
   *
   * @param param
   * @return
   */
  @PostMapping("/recommend/list")
  public JsonResult getPageList(@RequestBody GoodsRecommendPageListParam param) {
    PageResult<GoodsRecommend> pageList = shop().goodsRecommend.getPageList(param);
    return success(pageList);
  }

  @PostMapping("/recommend/add")
  public JsonResult insert(@RequestBody @Valid GoodsRecommendInsertParam goodsRecommendParam) {
    // 判断名字是否存在，不能插入同名
    if (shop().goodsRecommend.isGoodsRecommendNameExist(goodsRecommendParam.getRecommendName())) {
      return fail(JsonResultCode.GOODS_RECOMMEND_NAME_EXIST);
    }
    // 判断通过后插入操作
    int result = shop().goodsRecommend.insert(goodsRecommendParam);
    if (result > 0) {
      return success(JsonResultCode.CODE_SUCCESS);
    }
    return fail(JsonResultCode.CODE_FAIL);
  }

  @PostMapping("/recommend/delete/{id}")
  public JsonResult delete(@PathVariable Integer id) {
    if (id == null || shop().goodsRecommend.selectRecord(id) == null) {
      return fail(JsonResultCode.GOODS_RECOMMEND_ID_NOT_EXIST);
    }
    int result = shop().goodsRecommend.delete(id);
    if (result > 0) {
      return success(JsonResultCode.CODE_SUCCESS);
    }
    return fail(JsonResultCode.CODE_FAIL);
  }

  @PostMapping("/recommend/update")
  public JsonResult update(@RequestBody @Valid GoodsRecommendUpdateParam goodsRecommendParam) {
    RecommendGoodsRecord record = shop().goodsRecommend.selectRecord(goodsRecommendParam.getId());
    if (record == null || DelFlag.DISABLE.getCode() == record.getDelFlag()) {
      return fail(JsonResultCode.GOODS_RECOMMEND_NOT_EXIST);
    }
    int result = shop().goodsRecommend.update(goodsRecommendParam);
    if (result > 0) {
      return success(JsonResultCode.CODE_SUCCESS);
    }
    return fail(JsonResultCode.CODE_FAIL);
  }

  @GetMapping("/recommend/select/{id}")
  public JsonResult select(@PathVariable Integer id) {
    if (id == null) {
      return fail(JsonResultCode.GOODS_RECOMMEND_ID_NOT_EXIST);
    }
    GoodsRecommend goodsRecommend = shop().goodsRecommend.select(id);
    if (goodsRecommend == null) {
      fail(JsonResultCode.GOODS_RECOMMEND_NOT_EXIST);
    }
    return success(goodsRecommend);
  }
}
