package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.goods.label.*;
import org.springframework.web.bind.annotation.*;

/**
 * @author 黄荣刚
 * @date 2019年7月4日
 */
@RestController
public class AdminGoodsLabelController extends AdminBaseController {

    /**
     * 商品标签分页查询
     * @param param
     * @return
     */
    @PostMapping("/api/admin/goods/label/page/list")
    public JsonResult getPageList(@RequestBody GoodsLabelPageListParam param) {
        PageResult<GoodsLabelPageListVo> pageResult = shop().goods.goodsLabel.getPageList(param);
        return success(pageResult);
    }

    @PostMapping("/api/admin/goods/label/add")
    public JsonResult insert(@RequestBody GoodsLabelAddAndUpdateParam param) {
        if (param.getName() == null) {
            return fail(JsonResultMessage.GOODS_LABEL_NAME_NOT_NULL);
        }
        boolean labelNameExist = shop().goods.goodsLabel.isLabelNameExist(null, param.getName());
        if (labelNameExist) {
            return fail(JsonResultCode.GOODS_LABEL_NAME_EXIST);
        }
        shop().goods.goodsLabel.insert(param);
        return success();
    }


    /**
     * 删除商品标签（只是将删除标志位置1）
     * @param id
     * @return
     */
    @PostMapping("/api/admin/goods/label/delete/{id}")
    public JsonResult delete(@PathVariable Integer id) {
        shop().goods.goodsLabel.delete(id);
        return success();
    }

    /**
     * 根据商品标签ID查询指定商品标签
     * @param id
     * @return
     */
    @GetMapping("/api/admin/goods/label/select/{id}")
    public JsonResult select(@PathVariable Integer id) {
        GoodsLabelVo goodsLabelVo = shop().goods.goodsLabel.selectGoodsLabelById(id);
        if (goodsLabelVo != null) {
            return success(goodsLabelVo);
        }
        return fail(JsonResultCode.GOODS_LABEL_NOT_EXIST);
    }

    /**
     * 更新商品标签
     * @param param
     * @return
     */
    @PostMapping("/api/admin/goods/label/update")
    public JsonResult update(@RequestBody GoodsLabelAddAndUpdateParam param) {
        if (param.getId() == null||param.getName()==null) {
            return fail();
        }

        if (shop().goods.goodsLabel.isLabelNameExist(param.getId(),param.getName())) {
            return fail(JsonResultCode.GOODS_LABEL_NAME_EXIST);
        }
        shop().goods.goodsLabel.update(param);
        return success();
    }



    /**
     * 判断标签名称是否重复
     * @param goodsLabelName 标签名
     * @param isUpdate 1修改操作，0新增操作
     * @return {@link JsonResult#getError()} ==0 可以使用否则名称已存在
     */
    @GetMapping("/api/admin/goods/label/name/exist/{goodsLabelName}/{isUpdate}/{goodsLabelId}")
    public JsonResult isGoodsLabelNameOk(@PathVariable("goodsLabelName") String goodsLabelName,
                                            @PathVariable("isUpdate") Byte isUpdate,
                                            @PathVariable("goodsLabelId")Integer goodsLabelId) {
        if (isUpdate == 1) {
            if (shop().goods.goodsLabel.isLabelNameExist(goodsLabelId,goodsLabelName)) {
                return fail(JsonResultCode.GOODS_LABEL_NAME_EXIST);
            }
        } else {
            if (shop().goods.goodsLabel.isLabelNameExist(null,goodsLabelName)) {
                return fail(JsonResultCode.GOODS_LABEL_NAME_EXIST);
            }
        }
        return success();
    }

    @PostMapping("/api/admin/label/couple/updateByGoodsId")
    public JsonResult updateLabelCoupleByGoodsId(@RequestBody GoodsLabelsMapParam param) {
        shop().goods.goodsLabelCouple.updateByGoodsId(param.getGoodsId(), param.getLabelIds());
        return success();
    }

    @GetMapping("/api/admin/label/chronic/test")
    public JsonResult insertChronicLabelTest() {
        shop().goods.goodsLabel.insertChronicLabelData();
        return success();
    }
}
