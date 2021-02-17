package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.ShopUploadedImageRecord;
import com.meidianyi.shop.service.pojo.saas.shop.image.*;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.image.BatchDeleteImageParam;
import com.meidianyi.shop.service.pojo.shop.image.UploadPath;
import com.meidianyi.shop.service.pojo.shop.image.category.ImageCategoryRenameParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Part;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @author 孔德成
 * @date 2019/7/16 16:17
 */
@RequestMapping("/api")
@RestController
public class AdminShopImageController extends AdminBaseController {

  /**
   * 保存
   *
   * @param param
   * @return
   */
  @PostMapping("/admin/account/image/category/add")
  public JsonResult addImageCategory(@RequestBody @Valid ShopImageCategoryParam param) {
    AdminTokenAuthInfo adminInfo = adminAuth.user();
    if (adminInfo == null) {
      return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
    }
    saas.shop.image.addCategory(param, adminInfo.getSysId());
    return success();
  }

  /**
   * 删除
   *
   * @param param
   * @return
   */
  @PostMapping("/admin/account/image/category/delete")
  public JsonResult deleteImageCategory(@RequestBody @Valid ShopImageCategoryIDParam param) {
    AdminTokenAuthInfo adminInfo = adminAuth.user();
    if (adminInfo == null) {
      return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
    }
    saas.shop.image.removeCategory(param.getImgCatId(), adminInfo.getSysId());
    return success();
  }

  /**
   * 更新图片分组节点
   *
   * @return
   */
  @PostMapping("/admin/account/image/category/move")
  public JsonResult moveImageCategory(@RequestBody @Valid ShopUploadedImageCategoryParam param) {
    AdminTokenAuthInfo adminInfo = adminAuth.user();
    if (adminInfo == null) {
      return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
    }
    saas.shop.image.moveCategory(
        param.getImgCatId(), param.getImgCatParentId(), adminInfo.getSysId());
    return success();
  }

  /**
   * 更新分组名称
   *
   * @param param
   * @return
   */
  @PostMapping("/admin/account/image/category/rename")
  public JsonResult renameImageCategory(@RequestBody @Valid ImageCategoryRenameParam param) {
    AdminTokenAuthInfo adminInfo = adminAuth.user();
    if (adminInfo == null) {
      return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
    }
    saas.shop.image.renameImageCategory(
        param.getImgCatId(), param.getImgCatName(), adminInfo.getSysId());
    return success();
  }

  /**
   * 查询图片分组列表
   *
   * @return
   */
  @GetMapping("/admin/account/image/category/list")
  public JsonResult getImageCategoryList() {
    AdminTokenAuthInfo adminInfo = adminAuth.user();
    if (adminInfo == null) {
      return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
    }
    return success(saas.shop.image.getImageCategoryForTree(adminInfo.getSysId()));
  }

  /**
   * 上传单张图片
   *
   * @param param
   * @param file
   * @return
   * @throws IOException
   * @throws Exception
   */
  @PostMapping(value = "/admin/account/image/uploadOneImgae")
  protected JsonResult uploadOneFile(ShopUploadImageParam param, Part file)
      throws IOException, Exception {
    AdminTokenAuthInfo adminInfo = adminAuth.user();
    if (adminInfo == null) {
      return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
    }
    // 校验
    ResultMessage jsonResultCode = saas.shop.image.validImageParam(param, file);
    if (!jsonResultCode.getFlag()) {
      this.fail(jsonResultCode);
    }
    UploadPath uploadPath =
        saas.shop.image.getImageWritableUploadPath(file.getContentType(), adminInfo.getSysId());
    // 上传又拍云
    boolean ret =
        saas.shop.image.uploadToUpYunBySteam(uploadPath.relativeFilePath, file.getInputStream());
    if (ret) {
      ShopUploadedImageRecord record =
          saas.shop.image.addImageToDb(param, file, uploadPath, adminInfo.sysId);
      return this.success(record.into(ShopUploadedImageVo.class));
    }
    return fail(JsonResultCode.CODE_IMGAE_UPLOAD_FAILED);
  }

  /**
   * 图片列表
   *
   * @param param
   * @return
   */
  @PostMapping(value = "/admin/account/image/list")
  public JsonResult getImageList(@RequestBody ShopImageListQueryParam param) {
    AdminTokenAuthInfo adminInfo = adminAuth.user();
    if (adminInfo == null) {
      return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
    }
    PageResult<ShopUploadImageCatNameVo> imageList =
        saas.shop.image.getPageList(param, adminInfo.getSysId());
    return this.success(imageList);
  }

  /**
   * 图片裁剪
   *
   * @param param
   * @return
   * @throws Exception
   */
  @PostMapping(value = "/admin/account/image/makeCrop")
  public JsonResult makeCrop(@RequestBody @Valid ShopCropImageParam param) throws Exception {
    AdminTokenAuthInfo adminInfo = adminAuth.user();
    if (adminInfo == null) {
      return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
    }
    UploadPath uploadPath = saas.shop.image.makeCrop(param, adminInfo.getSysId());
    if (uploadPath == null) {
      return fail();
    }
    ShopUploadedImageRecord record =
        saas.shop.image.addImageToDb(param, uploadPath, adminInfo.sysId);
    return success(record.into(ShopUploadedImageVo.class));
  }

  /**
   * 批量移动分组
   *
   * @param param
   * @return
   */
  @PostMapping("/admin/account/image/batch/move")
  public JsonResult batchMoveImage(@RequestBody @Valid ShopBatchMoveImageParam param) {
    AdminTokenAuthInfo adminInfo = adminAuth.user();
    if (adminInfo == null) {
      return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
    }
    saas.shop.image.setCatId(
        param.getImageIds().toArray(new Integer[0]), param.getImageCatId(), adminInfo.getSysId());
    return success();
  }

  /**
   * 批量删除图片
   *
   * @param param
   * @return
   */
  @PostMapping("/admin/account/image/batch/delete")
  public JsonResult batchDeleteImage(@RequestBody @NotNull BatchDeleteImageParam param) {
    AdminTokenAuthInfo adminInfo = adminAuth.user();
    if (adminInfo == null) {
      return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
    }
    saas.shop.image.removeRows(param.getImageIds(), adminInfo.getSysId());
    return success();
  }
}
