package com.meidianyi.shop.controller.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Part;
import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.upyun.UpException;
import com.meidianyi.shop.service.pojo.saas.shop.image.ShopUploadedImageVo;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.image.BatchDeleteImageParam;
import com.meidianyi.shop.service.pojo.shop.image.BatchMoveImageParam;
import com.meidianyi.shop.service.pojo.shop.image.CropImageParam;
import com.meidianyi.shop.service.pojo.shop.image.ImageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.image.UploadImageCatNameVo;
import com.meidianyi.shop.service.pojo.shop.image.UploadImageParam;
import com.meidianyi.shop.service.pojo.shop.image.UploadPath;
import com.meidianyi.shop.service.pojo.shop.image.UploadedImageVo;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.FileUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.UploadedImageRecord;
/**
 * @author 新国
 */
@RestController
@RequestMapping("/api")
public class SystemImageController extends SystemBaseController {

    /**
     * 上传多张图片
     *
     * @param param
     * @return
     * @throws IOException
     * @throws Exception
     */
    @PostMapping(value = "/system/image/upload")
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
    @PostMapping(value = "/system/image/uploadOneImgae")
    protected JsonResult uploadOneFile(@Validated UploadImageParam param, Part file) throws IOException {
        // 校验
        ResultMessage jsonResultCode =saas.sysImage.validImageParam(param, file);
        if (!jsonResultCode.getFlag()) {
            return this.fail(jsonResultCode);
        }
        UploadPath uploadPath =saas.sysImage.getImageWritableUploadPath(file.getContentType());
        // 上传又拍云
        boolean ret = false;
        try {
            ret =saas.sysImage.uploadToUpYunBySteam(uploadPath.relativeFilePath, file.getInputStream());
        } catch (IOException | UpException e) {
            e.printStackTrace();
        }
        if (ret) {
            // 保存记录
        	param.getImgCatId();
            UploadedImageVo uploadedImageVo =
                   saas.sysImage.addImageToDb(param, file, uploadPath).into(UploadedImageVo.class);
            return this.success(uploadedImageVo);
        }
        return fail(JsonResultCode.CODE_IMGAE_UPLOAD_FAILED);
    }


    @PostMapping(value = "/system/image/base64/uploadOneImgae")
    private JsonResult uploadBase64File(UploadImageParam param) throws IOException {
        MultipartFile multipartFile = FileUtil.base64MutipartFile(param.base64Image);
        if (multipartFile==null){
            return this.fail(JsonResultCode.CODE_IMGAE_FORMAT_INVALID);
        }
        // 校验
        ResultMessage jsonResultCode =saas.sysImage.validImageParam(param, multipartFile.getSize(),multipartFile.getContentType(),multipartFile.getInputStream());
        if (!jsonResultCode.getFlag()) {
            return this.fail(jsonResultCode);
        }
        UploadPath uploadPath =saas.sysImage.getImageWritableUploadPath(multipartFile.getContentType());
        // 上传又拍云
        boolean ret = false;
        try {
            ret =saas.sysImage.uploadToUpYunBySteam(uploadPath.relativeFilePath, multipartFile.getInputStream());
        } catch (UpException | IOException e) {
            e.printStackTrace();
        }
        if (ret) {
            // 保存记录
            UploadedImageVo uploadedImageVo =
                   saas.sysImage.addImageToDb(param, multipartFile.getOriginalFilename(),multipartFile.getContentType(), (int) multipartFile.getSize(), uploadPath).into(UploadedImageVo.class);
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
    @PostMapping(value = "/system/image/list")
    public JsonResult getImageList(@RequestBody @Valid ImageListQueryParam param) {
        PageResult<UploadImageCatNameVo> imageList =saas.sysImage.getPageList(param);
        return this.success(imageList);
    }

    /**
     * 图片裁剪
     *
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/system/image/makeCrop")
    public JsonResult makeCrop(@RequestBody @Valid CropImageParam param) throws Exception {
        UploadPath uploadPath =saas.sysImage.makeCrop(param);
        if (uploadPath == null) {
            return fail();
        }
        UploadedImageRecord record =saas.sysImage.addImageToDb(param, uploadPath);
        return success(record.into(ShopUploadedImageVo.class));
    }

    /**
     * 批量移动分组
     *
     * @param param
     * @return
     */
    @PostMapping("/system/image/batch/move")
    public JsonResult batchMoveImage(@RequestBody @Valid BatchMoveImageParam param) {
       saas.sysImage.setCatId(param.getImageIds().toArray(new Integer[0]), param.getImageCatId());
        return success();
    }

    /**
     * 批量删除图片
     *
     * @param param
     * @return
     */
    @PostMapping("/system/image/batch/delete")
    public JsonResult batchDeleteImage(@RequestBody BatchDeleteImageParam param) {
        saas.sysImage.removeRows(param.getImageIds());
        return success();
    }
}
