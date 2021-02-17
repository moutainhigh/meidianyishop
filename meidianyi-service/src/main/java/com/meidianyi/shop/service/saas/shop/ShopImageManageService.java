package com.meidianyi.shop.service.saas.shop;

import com.UpYun;
import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.config.StorageConfig;
import com.meidianyi.shop.config.UpYunConfig;
import com.meidianyi.shop.db.main.tables.records.ShopUploadedImageCategoryRecord;
import com.meidianyi.shop.db.main.tables.records.ShopUploadedImageRecord;
import com.meidianyi.shop.service.foundation.image.ImageDefault;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.image.*;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.image.CropImageParam;
import com.meidianyi.shop.service.pojo.shop.image.UploadPath;
import com.meidianyi.shop.service.shop.image.ImageService;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectWhereStep;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.main.tables.ShopUploadedImage.SHOP_UPLOADED_IMAGE;
import static com.meidianyi.shop.db.main.tables.ShopUploadedImageCategory.SHOP_UPLOADED_IMAGE_CATEGORY;

/**
 * 账号的图片service
 *
 * @author 孔德成
 * @date 2019/7/16 15:40
 */
@Service
public class ShopImageManageService extends MainBaseService implements ImageDefault {

  private static final String ROOT_NAME = "我的图片";

  /** 使用一些公共方法 */
  @Autowired private ImageService imageService;

  @Autowired protected DomainConfig domainConfig;

  @Autowired protected UpYunConfig upYunConfig;

  @Autowired protected StorageConfig storageConfig;

  @Override
  public String imageUrl(String relativePath) {
    return domainConfig.imageUrl(relativePath);
  }

  @Override
  public String fullPath(String relativePath) {
    return storageConfig.storagePath(relativePath);
  }

  @Override
  public UpYun getUpYunClient() {
    return new UpYun(upYunConfig.getServer(), upYunConfig.getName(), upYunConfig.getPassword());
  }

  /**
   * 添加分类
   *
   * @param cat
   * @param sysId
   * @return
   */
  public Boolean addCategory(ShopImageCategoryParam cat, Integer sysId) {
    ShopUploadedImageCategoryRecord record = db().newRecord(SHOP_UPLOADED_IMAGE_CATEGORY, cat);
    record.setSysId(sysId);
    record.insert();
    if (cat.getImgCatParentId().equals(0)) {
      record.setCatIds(String.valueOf(record.getImgCatId()));
      record.setLevel((byte) 1);
    } else {
      ShopUploadedImageCategoryRecord parent = this.getCategoryById(cat.getImgCatParentId());
      if (parent != null) {
        record.setCatIds(parent.getCatIds() + "," + record.getImgCatId());
        record.setLevel((byte) (parent.getLevel() + 1));
      }
    }
    return record.update() > 0;
  }

  /**
   * 根据id查询
   *
   * @param imgCatParentId
   * @return
   */
  private ShopUploadedImageCategoryRecord getCategoryById(Integer imgCatParentId) {
    return db().selectFrom(SHOP_UPLOADED_IMAGE_CATEGORY)
        .where(SHOP_UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID.eq(imgCatParentId))
        .fetchAny();
  }

  /**
   * 删除分组
   *
   * @param imgCatId
   * @param sysId
   */
  public int removeCategory(Integer imgCatId, Integer sysId) {
    if (imgCatId == 0) {
      return 0;
    }
    List<Integer> ids = this.getChildCategoryIds(imgCatId);
    ids.add(imgCatId);
    return db().delete(SHOP_UPLOADED_IMAGE_CATEGORY)
        .where(SHOP_UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID.in(ids.toArray(new Integer[0])))
        .and(SHOP_UPLOADED_IMAGE_CATEGORY.SYS_ID.eq(sysId))
        .execute();
  }

  /**
   * 获取子分组id
   *
   * @param imgCatId
   * @return
   */
  private List<Integer> getChildCategoryIds(Integer imgCatId) {
    Result<ShopUploadedImageCategoryRecord> result = getChildCategory(imgCatId);
    return result == null
        ? new ArrayList<Integer>()
        : result.getValues(SHOP_UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID);
  }

  /**
   * 获取子分类列表
   *
   * @param parentId
   * @return
   */
  private Result<ShopUploadedImageCategoryRecord> getChildCategory(Integer parentId) {
    ShopUploadedImageCategoryRecord record = this.getCategoryById(parentId);
    if (record == null) {
      return null;
    }
    Result<ShopUploadedImageCategoryRecord> record1 =
        db().selectFrom(SHOP_UPLOADED_IMAGE_CATEGORY)
            .where(SHOP_UPLOADED_IMAGE_CATEGORY.CAT_IDS.like(this.prefixLikeValue(record.getCatIds() + ",")))
            .fetch();
    record1.add(record);
    return record1;
  }

  /**
   * 移动分组
   *
   * @param imgCatId
   * @param imgCatParentId
   * @param sysId
   */
  public int moveCategory(Integer imgCatId, Integer imgCatParentId, Integer sysId) {
    ShopUploadedImageCategoryRecord record = this.getCategoryById(imgCatId);
    if (record == null) {
      return 0;
    }
    String oldCatIdsPrefix = record.getCatIds();
    ShopUploadedImageCategoryRecord parentRecord =
        imgCatParentId == 0 ? null : this.getCategoryById(imgCatParentId);
    Integer levelDiff =
        parentRecord != null
            ? parentRecord.getLevel() + 1 - record.getLevel()
            : 0 - record.getLevel();
    String newCatIdPreifx =
        parentRecord != null
            ? parentRecord.getCatIds() + "," + record.getImgCatId()
            : record.getImgCatId().toString();
    return db().update(SHOP_UPLOADED_IMAGE_CATEGORY)
        .set(
            SHOP_UPLOADED_IMAGE_CATEGORY.CAT_IDS,
            DSL.concat(
                newCatIdPreifx,
                DSL.substring(SHOP_UPLOADED_IMAGE_CATEGORY.CAT_IDS, oldCatIdsPrefix.length() + 1)))
        .set(SHOP_UPLOADED_IMAGE_CATEGORY.LEVEL, SHOP_UPLOADED_IMAGE_CATEGORY.LEVEL.add(levelDiff))
        .where(
            SHOP_UPLOADED_IMAGE_CATEGORY.CAT_IDS.like(this.prefixLikeValue(oldCatIdsPrefix + ",")))
        .and(SHOP_UPLOADED_IMAGE_CATEGORY.SYS_ID.eq(sysId))
        .execute();
  }

  /**
   * 修改分组名称
   *
   * @param imgCatId
   * @param imgCatName
   * @param sysId
   */
  public void renameImageCategory(Integer imgCatId, String imgCatName, Integer sysId) {
    db().update(SHOP_UPLOADED_IMAGE_CATEGORY)
        .set(SHOP_UPLOADED_IMAGE_CATEGORY.IMG_CAT_NAME, imgCatName)
        .where(SHOP_UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID.eq(imgCatId))
        .and(SHOP_UPLOADED_IMAGE_CATEGORY.SYS_ID.eq(sysId))
        .execute();
  }

  /**
   * 获取分组列表树
   *
   * @return
   * @param sysId
   */
  public List<ShopCategoryTreeItemVo> getImageCategoryForTree(Integer sysId) {
    List<ShopCategoryTreeItemVo> result = new ArrayList<ShopCategoryTreeItemVo>();
    ShopCategoryTreeItemVo root = new ShopCategoryTreeItemVo();
    root.setName(ROOT_NAME);
    root.setId(0);
    Result<ShopUploadedImageCategoryRecord> records = this.getImageCategoryBySysId(sysId);
    this.getImageCategoryTree(root, records, 2);
    result.add(root);
    return result;
  }

  private void getImageCategoryTree(ShopCategoryTreeItemVo root, Result<ShopUploadedImageCategoryRecord> records, int level) {
    for (int i = 0; i < records.size(); i++) {
      ShopUploadedImageCategoryRecord item = records.get(i);
      if (root.getId().equals(item.getImgCatParentId())) {
        ShopCategoryTreeItemVo child = new ShopCategoryTreeItemVo();
        child.setId(item.getImgCatId());
        child.setName(item.getImgCatName());
        child.setLevel(level);
        root.getChild().add(child);
        records.remove(item);
        i--;
        getImageCategoryTree(child, records, level + 1);
      }
    }
  }

  private Result<ShopUploadedImageCategoryRecord> getImageCategoryBySysId(Integer sysId) {
    return db().selectFrom(SHOP_UPLOADED_IMAGE_CATEGORY)
        .where(SHOP_UPLOADED_IMAGE_CATEGORY.SYS_ID.eq(sysId))
        .fetch();
  }

  public ShopUploadedImageRecord addImageToDb(
      String relativeFilePath,
      String baseFilename,
      String submittedFileName,
      Integer imgCatId,
      Integer sysId) {
    String fullPath = fullPath(relativeFilePath);
    File file = new File(fullPath);
    if (file.exists()) {
      BufferedImage imageInfo = null;
      try {
        imageInfo = getImageInfo(fullPath);
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (imageInfo == null) {
        return null;
      }
      ShopUploadedImageRecord image = db().newRecord(SHOP_UPLOADED_IMAGE);
      image.setImgName(baseFilename == null ? file.getName() : baseFilename);
      image.setImgPath(relativeFilePath);
      image.setImgType(getImageExension(fullPath));
      image.setImgOrigFname(submittedFileName == null ? file.getName() : submittedFileName);
      image.setImgSize((int) (file.length()));
      image.setImgUrl(imageUrl(relativeFilePath));
      image.setImgWidth(imageInfo.getWidth());
      image.setImgHeight(imageInfo.getHeight());
      image.setImgCatId(imgCatId == null ? 0 : imgCatId);
      //            image.setShopId(this.getShopId());
      image.setSysId(sysId);
      image.insert();
      image.refresh();
      return image;
    }
    return null;
  }

  /**
   * 添加记录
   *
   * @param param
   * @param file
   * @param uploadPath
   * @return
   */
  public ShopUploadedImageRecord addImageToDb(ShopUploadImageParam param, Part file, UploadPath uploadPath, Integer sysId) {
    ShopUploadedImageRecord image = db().newRecord(SHOP_UPLOADED_IMAGE);
    image.setImgName(uploadPath.getFilname());
    image.setImgPath(uploadPath.relativeFilePath);
    image.setImgType(uploadPath.type);
    image.setImgOrigFname(file.getSubmittedFileName());
    image.setImgSize((int) file.getSize());
    image.setImgUrl(uploadPath.getImageUrl());
    image.setImgWidth(param.getNeedImgWidth());
    image.setImgHeight(param.getNeedImgHeight());
    image.setImgCatId(param.getImgCatId() == null ? 0 : param.getImgCatId());
    image.setSysId(sysId);
    image.insert();
    return image;
  }

  /**
   * @param param
   * @param uploadPath
   * @param sysId
   * @return
   */
  public ShopUploadedImageRecord addImageToDb(
      ShopCropImageParam param, UploadPath uploadPath, Integer sysId) {
    ShopUploadedImageRecord image = db().newRecord(SHOP_UPLOADED_IMAGE);
    image.setImgName(uploadPath.getFilname());
    image.setImgPath(uploadPath.relativeFilePath);
    image.setImgType(uploadPath.type);
    image.setImgOrigFname(param.getRemoteImgPath());
    image.setImgSize(param.getSize());
    image.setImgUrl(uploadPath.getImageUrl());
    image.setImgWidth(param.getCropWidth());
    image.setImgHeight(param.getCropHeight());
    image.setImgCatId(param.getImgCatId() == null ? 0 : param.getImgCatId());
    image.setSysId(sysId);
    image.insert();
    return image;
  }

  public PageResult<ShopUploadImageCatNameVo> getPageList(
      ShopImageListQueryParam param, Integer sysId) {
    SelectWhereStep<Record> select =
        db().select(SHOP_UPLOADED_IMAGE.asterisk(), SHOP_UPLOADED_IMAGE_CATEGORY.IMG_CAT_NAME)
            .from(SHOP_UPLOADED_IMAGE)
            .leftJoin(SHOP_UPLOADED_IMAGE_CATEGORY)
            .on(SHOP_UPLOADED_IMAGE.IMG_CAT_ID.eq(SHOP_UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID));
    select = this.buildOptions(select, param, sysId);
    select.orderBy(SHOP_UPLOADED_IMAGE.IMG_ID.desc());

    return this.getPageResult(select, param.page, param.pageRows, ShopUploadImageCatNameVo.class);
  }

  /**
   * 拼接查询条件
   *
   * @param select
   * @param param
   * @param sysId
   * @return
   */
  public SelectWhereStep<Record> buildOptions(
      SelectWhereStep<Record> select, ShopImageListQueryParam param, Integer sysId) {
    if (param == null) {
      return select;
    }
    select
        .where(SHOP_UPLOADED_IMAGE.DEL_FLAG.eq((byte) 0))
        .and(SHOP_UPLOADED_IMAGE.IMG_WIDTH.gt(0))
        .and(SHOP_UPLOADED_IMAGE.IMG_HEIGHT.gt(0))
        .and(SHOP_UPLOADED_IMAGE.SYS_ID.eq(sysId));

    if (param.imgCatId != null && param.imgCatId > 0) {
      List<Integer> imgCatIds =
          imageService.convertIntegerArray(getChildCategoryIds(param.imgCatId));
      select.where(SHOP_UPLOADED_IMAGE.IMG_CAT_ID.in(imgCatIds.toArray(new Integer[0])));
    }
    if (!StringUtils.isBlank(param.keywords)) {
      select.where(SHOP_UPLOADED_IMAGE.IMG_NAME.like(this.likeValue(param.keywords)));
    }
    if (param.searchNeed != null && param.searchNeed == 1) {
      if (param.needImgWidth != null && param.needImgWidth > 0) {
        select.where(SHOP_UPLOADED_IMAGE.IMG_WIDTH.eq(param.needImgWidth));
      }
      if (param.needImgHeight != null && param.needImgHeight > 0) {
        select.where(SHOP_UPLOADED_IMAGE.IMG_HEIGHT.eq(param.needImgHeight));
      }
    }
    SortField<?>[] sortFields = {
      SHOP_UPLOADED_IMAGE.UPLOAD_TIME.desc(),
      SHOP_UPLOADED_IMAGE.UPLOAD_TIME.asc(),
      SHOP_UPLOADED_IMAGE.IMG_SIZE.desc(),
      SHOP_UPLOADED_IMAGE.IMG_SIZE.asc(),
      SHOP_UPLOADED_IMAGE.IMG_NAME.desc(),
      SHOP_UPLOADED_IMAGE.IMG_NAME.asc()
    };
    if (param.uploadSortId != null
        && param.uploadSortId >= 0
        && param.uploadSortId < sortFields.length) {
      select.orderBy(sortFields[param.uploadSortId]);
    } else {
      select.orderBy(SHOP_UPLOADED_IMAGE.IMG_ID.desc());
    }
    return select;
  }

  /**
   * 剪切图片
   *
   * @param shopParam
   * @param sysId
   * @return
   * @throws IOException
   * @throws UpException
   */
  public UploadPath makeCrop(ShopCropImageParam shopParam, Integer sysId)
      throws IOException, UpException {
    CropImageParam param = new CropImageParam();
    FieldsUtil.assignNotNull(shopParam, param);
    UploadPath uploadPath = makeCrop(param, sysId);
    shopParam.setSize(param.getSize());
    return uploadPath;
  }

  public int setCatId(Integer[] imageIds, Integer imageCatId, Integer sysId) {
    return db().update(SHOP_UPLOADED_IMAGE)
        .set(SHOP_UPLOADED_IMAGE.IMG_CAT_ID, imageCatId)
        .where(SHOP_UPLOADED_IMAGE.IMG_ID.in((imageIds)))
        .and(SHOP_UPLOADED_IMAGE.SYS_ID.eq(sysId))
        .execute();
  }

  public int removeRows(List<Integer> imageIds, Integer sysId) {
    return db().update(SHOP_UPLOADED_IMAGE)
        .set(SHOP_UPLOADED_IMAGE.DEL_FLAG, (byte) 1)
        .where(SHOP_UPLOADED_IMAGE.IMG_ID.in(imageIds))
        .and(SHOP_UPLOADED_IMAGE.SYS_ID.eq(sysId))
        .execute();
  }

  /**
   * 校验添加图片参数
   *
   * @param param 入参
   * @param file 文件流
   * @return jsonResultCode,object
   */
  public ResultMessage validImageParam(ShopUploadImageParam param, Part file) throws IOException {
    Integer maxSize = 5 * 1024 * 1024;
    if (file.getSize() > maxSize) {
      return ResultMessage.builder().jsonResultCode(JsonResultCode.CODE_IMGAE_UPLOAD_GT_5M).build();
    }
    if (!validImageType(file.getContentType())) {
      return ResultMessage.builder()
          .jsonResultCode(JsonResultCode.CODE_IMGAE_FORMAT_INVALID)
          .build();
    }
    if (param.needImgWidth != null || param.needImgHeight != null) {
      BufferedImage bufferImage = ImageIO.read(file.getInputStream());
      if (param.needImgWidth != null && param.needImgWidth != bufferImage.getWidth()) {
        return ResultMessage.builder()
            .jsonResultCode(JsonResultCode.CODE_IMGAE_UPLOAD_EQ_WIDTH)
            .message(param.needImgWidth)
            .build();
      }
      if (param.needImgHeight != null && param.needImgHeight != bufferImage.getHeight()) {
        return ResultMessage.builder()
            .jsonResultCode(JsonResultCode.CODE_IMGAE_UPLOAD_EQ_HEIGHT)
            .message(param.needImgHeight)
            .build();
      }
    }
    return ResultMessage.builder().flag(true).build();
  }
}
