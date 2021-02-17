package com.meidianyi.shop.controller.system;


import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.image.category.ImageCategoryIDParam;
import com.meidianyi.shop.service.pojo.shop.image.category.ImageCategoryParam;
import com.meidianyi.shop.service.pojo.shop.image.category.ImageCategoryRenameParam;
import com.meidianyi.shop.service.pojo.shop.image.category.UploadedImageCategoryParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 图片分组列表
 *
 * @author 孔德成
 * @date 2019/7/8 13:59
 */
@RequestMapping("/api")
@RestController
public class SystemImageCategoryController extends SystemBaseController {


    /**
     * 保存
     *
     * @param param
     * @return
     */
    @PostMapping("/system/image/category/add")
    public JsonResult addImageCategory(@RequestBody  @Valid ImageCategoryParam param) {
        saas.sysImage.category.addCategory(param);
        return success();
    }


    /**
     * 删除
     *
     * @param param
     * @return
     */
    @PostMapping("/system/image/category/delete")
    public JsonResult deleteImageCategory(@RequestBody @Valid ImageCategoryIDParam param) {
        int i = saas.sysImage.category.removeCategory(param.getImgCatId());
        if (i==0){
            return fail();
        }
        return success();
    }


    /**
     * 更新图片分组节点
     *
     * @return
     */
    @PostMapping("/system/image/category/move")
    public JsonResult moveImageCategory(@RequestBody @Valid UploadedImageCategoryParam param) {
        int i = saas.sysImage.category.moveCategory(param.getImgCatId(), param.getImgCatParentId());
        if (i==0){
            return fail();
        }
        return success();
    }

    /**
     * 更新分组名称
     *
     * @param param
     * @return
     */
    @PostMapping("/system/image/category/rename")
    public JsonResult renameImageCategory(@RequestBody @Valid ImageCategoryRenameParam param) {
        saas.sysImage.category.setCategoryName(param.getImgCatId(), param.getImgCatName());
        return success();
    }


    /**
     * 查询图片分组列表
     *
     * @return
     */
    @GetMapping("/system/image/category/list")
    public JsonResult getImageCategoryList() {
        return success(saas.sysImage.category.getImageCategoryForTree(getLang()));
    }


}
