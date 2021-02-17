package com.meidianyi.shop.controller.wxapp;

import com.UpYun;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.image.UploadImageParam;
import com.meidianyi.shop.service.pojo.shop.image.UploadPath;
import com.meidianyi.shop.service.pojo.shop.image.UploadedImageVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片
 *
 * @author liangchen 2019.10.30
 */
@RestController
@RequestMapping("/api/wxapp/image")
public class WxAppImageController extends WxAppBaseController {

  /**
   * 上传单张图片
   *
   * @param param 图片信息
   * @param file 图片文件
   * @throws IOException 异常
   * @throws Exception 异常
   */
  @PostMapping("/upload")
  protected JsonResult upload(UploadImageParam param, Part file) throws IOException, Exception {
      logger().info("上传单张图片"+file);
    // 校验
    ResultMessage jsonResultCode = shop().image.validImageParam(param, file);
    if (!jsonResultCode.getFlag()) {
      return this.fail(jsonResultCode);
    }
      logger().info("校验结束");
    UploadPath uploadPath = shop().image.getImageWritableUploadPath(file.getContentType());
      logger().info("开始上传又拍云");
      //又拍云
    Map<String, String> upYunParams =new HashMap<>();
    if (param.getRotate()!=null&&!param.getRotate().trim().isEmpty()){
      upYunParams.put(UpYun.PARAMS.KEY_X_GMKERL_ROTATE.getValue(),param.getRotate());
    }
    // 上传又拍云
    boolean ret = shop().image.getUpYunClient().writeFile(uploadPath.relativeFilePath, file.getInputStream(), true, upYunParams);
    if (ret) {
      // 保存记录
      UploadedImageVo uploadedImageVo =
          shop().image.addImageToDb(param, file, uploadPath).into(UploadedImageVo.class);
      logger().info("上传完成");
      return this.success(uploadedImageVo);
    }
    return fail(JsonResultCode.CODE_IMGAE_UPLOAD_FAILED);
  }
}
