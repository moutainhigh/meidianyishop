package com.meidianyi.shop.controller.admin;

import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.FileUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.UploadedImageRecord;
import com.meidianyi.shop.service.pojo.saas.shop.image.ShopUploadedImageVo;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.image.BatchDeleteImageParam;
import com.meidianyi.shop.service.pojo.shop.image.BatchMoveImageParam;
import com.meidianyi.shop.service.pojo.shop.image.CropImageParam;
import com.meidianyi.shop.service.pojo.shop.image.ImageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.image.UploadBase64Group;
import com.meidianyi.shop.service.pojo.shop.image.UploadImageCatNameVo;
import com.meidianyi.shop.service.pojo.shop.image.UploadImageParam;
import com.meidianyi.shop.service.pojo.shop.image.UploadPath;
import com.meidianyi.shop.service.pojo.shop.image.UploadedImageVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 新国
 */
@RestController
@RequestMapping("/api")
public class AdminImageController extends AdminBaseController {

    /**
     * 上传多张图片
     *
     * @param param
     * @return
     * @throws IOException
     * @throws Exception
     */
    @PostMapping(value = "/admin/image/upload")
    public JsonResult imageUpload(@RequestBody @Valid UploadImageParam param) throws IOException, Exception {
        List<UploadedImageVo> uploadImages = new ArrayList<UploadedImageVo>();
        JsonResult result = null;
        List<Part> files = Util.getFilePart(request, param.uploadFileId);
        int success = 0;
        for (int i = 0; i < files.size(); i++) {
            Part file = files.get(i);
            result = this.uploadOneFile(param, file);
            if (result.getError() == JsonResultCode.CODE_SUCCESS.getCode()) {
                uploadImages.add((UploadedImageVo) result.getContent());
                success++;
            }
        }
        if (success > 0) {
            return this.success(uploadImages);
        } else {
            return result != null ? result : fail(JsonResultCode.CODE_IMGAE_UPLOAD_FAILED);
        }
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
    @PostMapping(value = "/admin/image/uploadOneImgae")
    protected JsonResult uploadOneFile(@Validated UploadImageParam param, Part file) throws IOException {
        // 校验
        ResultMessage jsonResultCode = shop().image.validImageParam(param, file);
        if (!jsonResultCode.getFlag()) {
            return this.fail(jsonResultCode);
        }
        UploadPath uploadPath = shop().image.getImageWritableUploadPath(file.getContentType());
        // 上传又拍云
        boolean ret = false;
        try {
            ret = shop().image.uploadToUpYunBySteam(uploadPath.relativeFilePath, file.getInputStream());
        } catch (IOException | UpException e) {
            e.printStackTrace();
        }
        if (ret) {
            // 保存记录
            UploadedImageVo uploadedImageVo =
                    shop().image.addImageToDb(param, file, uploadPath).into(UploadedImageVo.class);
            return this.success(uploadedImageVo);
        }
        return fail(JsonResultCode.CODE_IMGAE_UPLOAD_FAILED);
    }


    @PostMapping(value = "/admin/image/base64/uploadOneImgae")
    private JsonResult uploadBase64File(@RequestBody @Validated(UploadBase64Group.class) UploadImageParam param) throws IOException {
        MultipartFile multipartFile = FileUtil.base64MutipartFile(param.base64Image);
        if (multipartFile==null){
            return this.fail(JsonResultCode.CODE_IMGAE_FORMAT_INVALID);
        }
        // 校验
        ResultMessage jsonResultCode = shop().image.validImageParam(param, multipartFile.getSize(),multipartFile.getContentType(),multipartFile.getInputStream());
        if (!jsonResultCode.getFlag()) {
            return this.fail(jsonResultCode);
        }
        UploadPath uploadPath = shop().image.getImageWritableUploadPath(multipartFile.getContentType());
        // 上传又拍云
        boolean ret = false;
        try {
            ret = shop().image.uploadToUpYunBySteam(uploadPath.relativeFilePath, multipartFile.getInputStream());
        } catch (UpException | IOException e) {
            e.printStackTrace();
        }
        if (ret) {
            // 保存记录
            UploadedImageVo uploadedImageVo =
                    shop().image.addImageToDb(param, multipartFile.getOriginalFilename(),multipartFile.getContentType(), (int) multipartFile.getSize(), uploadPath).into(UploadedImageVo.class);
            return this.success(uploadedImageVo);
        }
        return fail(JsonResultCode.CODE_IMGAE_UPLOAD_FAILED);
    }

    /**
     * 图片列表
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/admin/image/list")
    public JsonResult getImageList(@RequestBody @Valid ImageListQueryParam param) {
        PageResult<UploadImageCatNameVo> imageList = shop().image.getPageList(param);
        return this.success(imageList);
    }

    /**
     * 图片裁剪
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/admin/image/makeCrop")
    public JsonResult makeCrop(@RequestBody @Valid CropImageParam param) throws Exception {
        UploadPath uploadPath = shop().image.makeCrop(param);
        if (uploadPath == null) {
            return fail();
        }
        UploadedImageRecord record = shop().image.addImageToDb(param, uploadPath);
        return success(record.into(ShopUploadedImageVo.class));
    }

    /**
     * 批量移动分组
     *
     * @param param
     * @return
     */
    @PostMapping("/admin/image/batch/move")
    public JsonResult batchMoveImage(@RequestBody @Valid BatchMoveImageParam param) {
        shop().image.setCatId(param.getImageIds().toArray(new Integer[0]), param.getImageCatId());
        return success();
    }

    /**
     * 批量删除图片
     *
     * @param param
     * @return
     */
    @PostMapping("/admin/image/batch/delete")
    public JsonResult batchDeleteImage(@RequestBody BatchDeleteImageParam param) {
        shop().image.removeRows(param.getImageIds());
        return success();
    }
}
