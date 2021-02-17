package com.meidianyi.shop.controller.admin;


import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.video.category.UploadedVideoCategoryParam;
import com.meidianyi.shop.service.pojo.shop.video.category.VideoCategoryIDParam;
import com.meidianyi.shop.service.pojo.shop.video.category.VideoCategoryParam;
import com.meidianyi.shop.service.pojo.shop.video.category.VideoCategoryRenameParam;


/**
 * 视频分组列表
 *
 * @author 孔德成
 * @date 2019/7/8 13:59
 */
@RequestMapping("/api")
@RestController
public class AdminVideoCategoryController extends AdminBaseController {


    /**
     * 保存
     *
     * @param param
     * @return
     */
    @PostMapping("/admin/video/category/add")
    public JsonResult addVideoCategory(@RequestBody  @Valid VideoCategoryParam param) {
        shop().video.category.addCategory(param);
        return success();
    }


    /**
     * 删除
     *
     * @param param
     * @return
     */
    @PostMapping("/admin/video/category/delete")
    public JsonResult deleteVideoCategory(@RequestBody @Valid VideoCategoryIDParam param) {
        shop().video.category.removeCategory(param.getVideoCatId());
        return success();
    }


    /**
     * 更新视频分组节点
     *
     * @return
     */
    @PostMapping("/admin/video/category/move")
    public JsonResult moveVideoCategory(@RequestBody @Valid UploadedVideoCategoryParam param) {
        shop().video.category.moveCategory(param.getImgCatId(),param.getImgCatParentId());
        return success();
    }

    /**
     * 更新分组名称
     *
     * @param param
     * @return
     */
    @PostMapping("/admin/video/category/rename")
    public JsonResult renameVideoCategory(@RequestBody @Valid VideoCategoryRenameParam param) {
        shop().video.category.setCategoryName(param.getVideoCatId(), param.getVideoCatName());
        return success();
    }


    /**
     * 查询视频分组列表
     *
     * @return
     */
    @GetMapping("/admin/video/category/list")
    public JsonResult getVideoCategoryList() {
        return success(shop().video.category.getVideoCategoryForTree(0));
    }
}
