package com.meidianyi.shop.service.saas.image;

import com.UpYun;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.RegexUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.config.UpYunConfig;
import com.meidianyi.shop.db.main.tables.records.UploadedImageRecord;
import com.meidianyi.shop.service.foundation.image.ImageDefault;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.image.CropImageParam;
import com.meidianyi.shop.service.pojo.shop.image.ImageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.image.UploadImageCatNameVo;
import com.meidianyi.shop.service.pojo.shop.image.UploadImageParam;
import com.meidianyi.shop.service.pojo.shop.image.UploadPath;
import org.apache.commons.io.FileUtils;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;

import static com.meidianyi.shop.db.main.tables.UploadedImage.UPLOADED_IMAGE;
import static com.meidianyi.shop.db.main.tables.UploadedImageCategory.UPLOADED_IMAGE_CATEGORY;
import static com.meidianyi.shop.service.pojo.shop.image.ImageConstant.IMG_CAT_ID_MY_IMAGE;
import static com.meidianyi.shop.service.pojo.shop.image.ImageConstant.IMG_CAT_ID_USER_IMAGE;

/**
 * @author 新国
 */
@Service

public class SystemImageService extends MainBaseService implements ImageDefault {

	@Autowired public SystemImageCategoryService category;

	@Autowired
    protected DomainConfig domainConfig;

    @Autowired
    protected UpYunConfig upYunConfig;


    @Override
	public String imageUrl(String relativePath) {
		if (!StringUtils.isEmpty(relativePath)) {
			if (RegexUtil.checkUrl(relativePath)) {
				return relativePath;
			}
			return domainConfig.imageUrl(relativePath);
		}
		return null;
	}

   	@Override
   	public String fullPath(String relativePath) {
   		return domainConfig.mainUrl(relativePath);
   	}

   	@Override
   	public UpYun getUpYunClient() {
   		return new UpYun(upYunConfig.getServer(), upYunConfig.getName(), upYunConfig.getPassword());
   	}

    /**
     * 删除单张图片
     *
     * @param imageId
     * @return
     */
    public int removeRow(Integer imageId) {
        Byte delFlag = 1;
        return db().update(UPLOADED_IMAGE)
                .set(UPLOADED_IMAGE.DEL_FLAG, delFlag)
                .where(UPLOADED_IMAGE.IMG_ID.eq(imageId))
                .execute();
    }

    /**
     * 删除多张图片
     *
     * @param imageIds
     * @return
     */
    public int removeRows(List<Integer> imageIds) {
        Byte delFlag = 1;
        return db().update(UPLOADED_IMAGE)
                .set(UPLOADED_IMAGE.DEL_FLAG, delFlag)
                .where(UPLOADED_IMAGE.IMG_ID.in(imageIds))
                .execute();
    }

    /**
     * 设置图片的分类
     *
     * @param imageIds
     * @param catId
     * @return
     */
    public int setCatId(Integer[] imageIds, Integer catId) {
        return db().update(UPLOADED_IMAGE)
                .set(UPLOADED_IMAGE.IMG_CAT_ID, catId)
                .where(UPLOADED_IMAGE.IMG_ID.in((imageIds)))
                .execute();
    }

    /**
     * 图片列表分页
     *
     * @param param
     * @return
     */
    public PageResult<UploadImageCatNameVo> getPageList(ImageListQueryParam param) {
        SelectWhereStep<Record> select = db().select(UPLOADED_IMAGE.asterisk(), UPLOADED_IMAGE_CATEGORY.IMG_CAT_NAME)
                .from(UPLOADED_IMAGE)
                .leftJoin(UPLOADED_IMAGE_CATEGORY)
                .on(UPLOADED_IMAGE.IMG_CAT_ID.eq(DSL.cast(UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID, Integer.class)));
        select = this.buildOptions(select, param);
        select.orderBy(UPLOADED_IMAGE.IMG_ID.desc());
        return this.getPageResult(select, param.getPage(),param.getPageRows(), UploadImageCatNameVo.class);
    }


    public List<Integer> convertIntegerArray(List<Integer> array) {
        List<Integer> result = new ArrayList<Integer>();
        for (Integer i : array) {
            result.add(i.intValue());
        }
        return result;
    }

    /**
     * 拼接查询条件
     * @param select
     * @param param
     * @return
     */
    public SelectWhereStep<Record> buildOptions(SelectWhereStep<Record> select, ImageListQueryParam param) {
        if (param == null) {
            return select;
        }
        Byte noDel = 0;
        select.where(UPLOADED_IMAGE.DEL_FLAG.eq(noDel))
                .and(UPLOADED_IMAGE.IMG_WIDTH.gt(0))
                .and(UPLOADED_IMAGE.IMG_HEIGHT.gt(0));

        if (param.getImgCatId().equals(IMG_CAT_ID_MY_IMAGE)){
            //我的图
            select.where(UPLOADED_IMAGE.IMG_CAT_ID.notEqual(IMG_CAT_ID_USER_IMAGE));
        }else if (param.getImgCatId().equals(IMG_CAT_ID_USER_IMAGE)){
            //用户上传
            select.where(UPLOADED_IMAGE.IMG_CAT_ID.eq(IMG_CAT_ID_USER_IMAGE));
        }else {
            List<Integer> imgCatIds = convertIntegerArray(category.getChildCategoryIds(param.getImgCatId()));
            select.where(UPLOADED_IMAGE.IMG_CAT_ID.in(imgCatIds.toArray(new Integer[0])));
        }

        if (!StringUtils.isBlank(param.getKeywords())) {
            select.where(UPLOADED_IMAGE.IMG_NAME.like(this.likeValue(param.getKeywords())));
        }
        if (param.getSearchNeed() != null && param.getSearchNeed() == 1) {
            if (param.getNeedImgWidth() != null && param.getNeedImgWidth() > 0) {
                select.where(UPLOADED_IMAGE.IMG_WIDTH.eq(param.getNeedImgWidth()));
            }
            if (param.getNeedImgHeight() != null && param.getNeedImgHeight() > 0) {
                select.where(UPLOADED_IMAGE.IMG_HEIGHT.eq(param.getNeedImgHeight()));
            }
        }
        SortField<?>[] sortFields = {UPLOADED_IMAGE.CREATE_TIME.desc(), UPLOADED_IMAGE.CREATE_TIME.asc(),
                UPLOADED_IMAGE.IMG_SIZE.desc(), UPLOADED_IMAGE.IMG_SIZE.asc(), UPLOADED_IMAGE.IMG_NAME.desc(),
                UPLOADED_IMAGE.IMG_NAME.asc()};
        if (param.getUploadSortId() != null && param.getUploadSortId() >= 0 && param.getUploadSortId() < sortFields.length) {
            select.orderBy(sortFields[param.getUploadSortId()]);
        } else {
            select.orderBy(UPLOADED_IMAGE.IMG_ID.desc());
        }
        return select;
    }


    /**
     * 通过原始URL得到图片信息
     *
     * @param imagePathOrUrl
     * @return
     */
    public UploadedImageRecord getImageFromOriginName(String imagePathOrUrl) {
        return db().selectFrom(UPLOADED_IMAGE).where(UPLOADED_IMAGE.IMG_ORIG_FNAME.eq(imagePathOrUrl)).fetchAny();
    }

    /**
     * 通过图片相对路径获取图片信息
     *
     * @param imagePath
     * @return
     */
    public UploadedImageRecord getImageFromImagePath(String imagePath) {
        return db().selectFrom(UPLOADED_IMAGE).where(UPLOADED_IMAGE.IMG_PATH.eq(imagePath)).fetchAny();
    }
    
    
    public UploadedImageRecord addImageToDb(UploadImageParam param, Part file, UploadPath uploadPath) {
    	 return addImageToDb(param,file.getSubmittedFileName(),file.getContentType(), (int) file.getSize(),uploadPath);
    }

    public UploadedImageRecord addImageToDb(CropImageParam param, UploadPath uploadPath) {
        UploadedImageRecord image = db().newRecord(UPLOADED_IMAGE);
        image.setImgName(baseFilename(uploadPath.getFilname()));
        image.setImgPath(uploadPath.relativeFilePath);
        image.setImgType(uploadPath.extension);
        image.setImgOrigFname(param.remoteImgPath);
        image.setImgSize(param.getSize());
        image.setImgUrl(uploadPath.getImageUrl());
        image.setImgWidth(param.getCropWidth());
        image.setImgHeight(param.getCropHeight());
        image.setImgCatId(param.getImgCatId());
        image.insert();
        return image;
      }
    
    /**
     * 保存图片到数据库
     *
     * @param relativePath
     * @param imageName
     * @param originFileName
     * @param catId
     * @return
     */
    public UploadedImageRecord addImageToDb(UploadImageParam param, String submitName,String  contentType,Integer fileSize, UploadPath uploadPath){
        UploadedImageRecord image = db().newRecord(UPLOADED_IMAGE);
        image.setImgName(baseFilename(submitName));
        image.setImgOrigFname(submitName);
        image.setImgType(contentType);
        image.setImgSize(new Long(fileSize).intValue());
        image.setImgPath(uploadPath.relativeFilePath);
        image.setImgUrl(uploadPath.getImageUrl());
        image.setImgWidth(param.getNeedImgWidth());
        image.setImgHeight(param.getNeedImgHeight());
        image.setImgCatId(param.getImgCatId());
        image.insert();
        image.insert();
        return image;
    }
    
	public UploadedImageRecord addImageToDb(String relativePath, String imageName, String originFileName, Integer catId)
			throws IOException {
		String fullPath = fullPath(relativePath);
		File file = new File(fullPath);
		if (file.exists()) {
			BufferedImage imageInfo = getImageInfo(fullPath);
			if (imageInfo == null) {
				return null;
			}
			UploadedImageRecord image = db().newRecord(UPLOADED_IMAGE);
			image.setImgName(imageName == null ? file.getName() : imageName);
			image.setImgPath(relativePath);
			image.setImgType(this.getImageExension(fullPath));
			image.setImgOrigFname(originFileName == null ? file.getName() : originFileName);
			image.setImgSize((int) (file.length()));
			image.setImgUrl(this.imageUrl(relativePath));
			image.setImgWidth(imageInfo.getWidth());
			image.setImgHeight(imageInfo.getHeight());
			image.setImgCatId(catId == null ? 0 : catId);
			image.setShopId(this.getShopId());
			image.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
			image.insert();
			return image;
		}
		return null;
	}

    /**
     * 得到图片信息
     *
     * @param imageId
     * @return
     */
    public UploadedImageRecord getImageById(Integer imageId) {
        return db().fetchAny(UPLOADED_IMAGE, UPLOADED_IMAGE.IMG_ID.eq((imageId)));
    }

    /**
     * 获取总大小（不包括删除）
     *
     * @return
     */
    public Integer getAllSize() {
        Byte noDel = 0;
        Object imageSize = db().select(DSL.sum(UPLOADED_IMAGE.IMG_SIZE))
                .from(UPLOADED_IMAGE)
                .where(UPLOADED_IMAGE.SHOP_ID.eq(this.getShopId()))
                .and(UPLOADED_IMAGE.IMG_WIDTH.gt(0))
                .and(UPLOADED_IMAGE.IMG_HEIGHT.gt(0))
                .and(UPLOADED_IMAGE.DEL_FLAG.eq(noDel))
                .fetchAny(0);
        return Util.getInteger(imageSize);
    }

    /**
     * 添加外链图片到数据库
     *
     * @param imageUrl
     * @return
     */
    public UploadedImageRecord addLocalGoodsImage(String imageUrl) {
        if (getImageFromOriginName(imageUrl) == null) {
            String extension = this.getImageExension(imageUrl);
            String filename = randomFilename();
            UploadPath uploadPath = this.getWritableUploadPath("image", filename, extension,null);
            try {
                FileUtils.copyURLToFile(new URL(imageUrl), new File(uploadPath.fullPath), 30, 30);
                deleteFile(uploadPath.getFullPath());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            UploadedImageRecord record = this.getImageFromImagePath(uploadPath.relativeFilePath);
            if (record == null) {
                try {
                    record = this.addImageToDb(uploadPath.relativeFilePath, null, imageUrl, 0);
                }catch (IOException e){
                    e.printStackTrace();
                    return null;
                }
            }
            return record;
        }
        return null;
    }

    /**
     * 当前店铺Id
     * @return
     */
    @Override
    public Integer currentShopId() {
    	return this.getShopId();
    }

	 /**
	  * 添加外链图片到又拍云
     * @param iamgeUrl
     * @param relativePath
     * @return
     */
	public Boolean addImgeToUp(String iamgeUrl, String relativePath) {
		if(StringUtils.isEmpty(iamgeUrl)||StringUtils.isEmpty(relativePath)) {
			return false;
		}
		InputStream inStream = null;
		HttpURLConnection httpUrl = null;
		URL url = null;
		boolean steam = false;
		try {
			url = new URL(iamgeUrl);
			httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.connect();
			inStream = httpUrl.getInputStream();
			steam = this.uploadToUpYunBySteam(relativePath, inStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpUrl != null) {
				httpUrl.disconnect();
				httpUrl = null;
			}
		}
		return steam;
	}
	
	  /**
	   * 校验添加图片参数
	   *
	   * @param param 入参
	   * @param file
	   * @return jsonResultCode, object
	   */
	  public ResultMessage validImageParam(UploadImageParam param, Part file) throws IOException {
	    return validImageParam(param,file.getSize(),file.getContentType(),file.getInputStream());
	  }

	  /**
	   * 校验添加图片参数
	   *
	   * @param param 入参
	   * @param fileSize
	   * @return jsonResultCode, object
	   */
	  public ResultMessage validImageParam(UploadImageParam param, long fileSize, String fileType, InputStream fileStream) {
	    Integer maxSize = 5 * 1024 * 1024;
	    if (fileSize > maxSize) {
	      return ResultMessage.builder().jsonResultCode(JsonResultCode.CODE_IMGAE_UPLOAD_GT_5M).build();
	    }
	    if (!validImageType(fileType)) {
	      return ResultMessage.builder()
	          .jsonResultCode(JsonResultCode.CODE_IMGAE_FORMAT_INVALID)
	          .build();
	    }
	    BufferedImage bufferImage = null;
	    try {
	      bufferImage = ImageIO.read(fileStream);
	    } catch (Exception e) {
	      return ResultMessage.builder().jsonResultCode(JsonResultCode.CODE_IMGAE_FORMAT_INVALID).build();
	    }
	    int type = bufferImage.getType();
	    if (bufferImage == null || bufferImage.getWidth(null) <= 0 || bufferImage.getHeight(null) <= 0) {
	      return ResultMessage.builder().jsonResultCode(JsonResultCode.CODE_IMGAE_FORMAT_INVALID).build();
	    }
	    if (param.needImgWidth != null || param.needImgHeight != null) {
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
