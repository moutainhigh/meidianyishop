package com.meidianyi.shop.dao.shop.image;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.UploadedImageRecord;
import com.meidianyi.shop.service.pojo.shop.image.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Repository;

import javax.servlet.http.Part;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.UploadedImage.UPLOADED_IMAGE;
import static com.meidianyi.shop.db.shop.tables.UploadedImageCategory.UPLOADED_IMAGE_CATEGORY;
import static com.meidianyi.shop.service.pojo.shop.image.ImageConstant.IMG_CAT_ID_MY_IMAGE;
import static com.meidianyi.shop.service.pojo.shop.image.ImageConstant.IMG_CAT_ID_USER_IMAGE;


/**
 * @author lixinguo
 */
@Repository
public class UploadedImageDao extends ShopBaseDao {

    /**
     * 删除单张图片
     *
     * @param imageId 图片ID
     * @return 更新数量
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
     * @param imageIds 图片ID列表
     * @return 更新数量
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
     * @param imageIds 图片ID列表
     * @param catId    分类Id
     * @return 更新数量
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
     * @param param 图片列表查询参数
     * @return 分页结果
     */
    public PageResult<UploadImageCatNameVo> getPageList(ImageListQueryParam param) {
        SelectWhereStep<Record> select =
            db().select(ArrayUtils.addAll(UPLOADED_IMAGE.fields(), UPLOADED_IMAGE_CATEGORY.IMG_CAT_NAME))
                .from(UPLOADED_IMAGE)
                .leftJoin(UPLOADED_IMAGE_CATEGORY)
                .on(
                    UPLOADED_IMAGE.IMG_CAT_ID.eq(
                        DSL.cast(UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID, Integer.class)));
        select = this.buildOptions(select, param);
        select.orderBy(UPLOADED_IMAGE.IMG_ID.desc());

        return this.getPageResult(select, param.getPage(), param.getPageRows(), UploadImageCatNameVo.class);
    }

    /**
     * 拼接查询条件
     *
     * @param select 查询
     * @param param  条件
     * @return 查询
     */
    public SelectWhereStep<Record> buildOptions(SelectWhereStep<Record> select, ImageListQueryParam param) {
        if (param == null) {
            return select;
        }
        select.where(UPLOADED_IMAGE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
        if (param.getImgCatId().equals(IMG_CAT_ID_MY_IMAGE)) {
            //我的图
            select.where(UPLOADED_IMAGE.IMG_CAT_ID.notEqual(IMG_CAT_ID_USER_IMAGE));
        } else if (param.getImgCatId().equals(IMG_CAT_ID_USER_IMAGE)) {
            //用户上传
            select.where(UPLOADED_IMAGE.IMG_CAT_ID.eq(IMG_CAT_ID_USER_IMAGE));
        } else {
            select.where(UPLOADED_IMAGE.IMG_CAT_ID.in(param.getChildCatIdsAndImgCatId()));
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
        SortField<?>[] sortFields = {
            UPLOADED_IMAGE.CREATE_TIME.desc(),
            UPLOADED_IMAGE.CREATE_TIME.asc(),
            UPLOADED_IMAGE.IMG_SIZE.desc(),
            UPLOADED_IMAGE.IMG_SIZE.asc(),
            UPLOADED_IMAGE.IMG_NAME.desc(),
            UPLOADED_IMAGE.IMG_NAME.asc()
        };
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
     * @param imagePathOrUrl 图片路径
     * @return 图片信息
     */
    public UploadedImageDo getImageFromOriginName(String imagePathOrUrl) {
        return db().selectFrom(UPLOADED_IMAGE)
            .where(UPLOADED_IMAGE.IMG_ORIG_FNAME.eq(imagePathOrUrl))
            .fetchAnyInto(UploadedImageDo.class);
    }

    /**
     * 通过图片相对路径获取图片信息
     *
     * @param imagePath 图片路径
     * @return 图片信息
     */
    public UploadedImageDo getImageFromImagePath(String imagePath) {
        return db().selectFrom(UPLOADED_IMAGE).where(UPLOADED_IMAGE.IMG_PATH.eq(imagePath))
            .fetchAnyInto(UploadedImageDo.class);
    }

    /**
     * 保存图片到数据库
     *
     * @return
     */
    public UploadedImageDo addImageToDb(UploadImageParam param, Part file, UploadPath uploadPath) {
        return addImageToDb(param, file.getSubmittedFileName(), file.getContentType(), (int) file.getSize(), uploadPath);
    }

    /**
     * 得到图片basename
     *
     * @param filename
     * @return
     */
    private String baseFilename(String filename) {
        int p = filename.lastIndexOf('.');
        return p == -1 ? filename : filename.substring(0, p);
    }

    /**
     * 保存图片到数据库
     *
     * @return
     */
    public UploadedImageDo addImageToDb(UploadImageParam param, String submitName, String contentType, Integer fileSize, UploadPath uploadPath) {
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
        image.setUserId(param.getUserId());
        image.insert();
        return image.into(UploadedImageDo.class);
    }

    public UploadedImageDo addImageToDb(CropImageParam param, UploadPath uploadPath) {
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
        return image.into(UploadedImageDo.class);
    }

    /**
     * 保存图片到数据库
     */
    public UploadedImageDo addImageToDb(Integer imageId, UploadImageParam param, String submitName, String contentType, Integer fileSize, UploadPath uploadPath) {
        return addImageToDb(param, submitName, contentType, fileSize, uploadPath);
    }


    /**
     * 通过imageId更新原有图片
     */
    public UploadedImageDo updateImageToDb(Integer imageId, UploadImageParam param, String submitName, String contentType, Integer fileSize, UploadPath uploadPath) {
        UploadedImageRecord image = db().newRecord(UPLOADED_IMAGE);
        image.setImgId(imageId);
        image.setImgName(baseFilename(submitName));
        image.setImgOrigFname(submitName);
        image.setImgType(contentType);
        image.setImgSize(new Long(fileSize).intValue());
        image.setImgPath(uploadPath.relativeFilePath);
        image.setImgUrl(uploadPath.getImageUrl());
        image.setImgWidth(param.getNeedImgWidth());
        image.setImgHeight(param.getNeedImgHeight());
        image.setImgCatId(param.getImgCatId());
        image.setUserId(param.getUserId());

        image.update();
        return image.into(UploadedImageDo.class);
    }

    /**
     * 批量插入记录
     *
     * @param records
     */
    public void batchInsert(List<UploadedImageDo> records) {
        List<UploadedImageRecord> tableRecords = new ArrayList<>(records.size());
        for (int i = 0; i < records.size(); i++) {
            UploadedImageRecord r = db().newRecord(UPLOADED_IMAGE);
            r.from(records.get(i));
            tableRecords.add(r);
        }
        db().batchInsert(tableRecords).execute();
    }

    /**
     * 批量添加下载的外链图片信息
     *
     * @param downloadImageBos 下载图片
     * @return 下载相对路径列表
     */
    public List<String> addImageToDbBatch(List<DownloadImageBo> downloadImageBos) {
        List<String> imgRelativePath = new ArrayList<>(downloadImageBos.size());

        List<UploadedImageRecord> records = new ArrayList<>();
        for (DownloadImageBo bo : downloadImageBos) {
            if (Boolean.TRUE.equals(bo.isAlreadyHas())) {
                imgRelativePath.add(bo.getRelativeFilePath());
                continue;
            }

            UploadedImageRecord record = db().newRecord(UPLOADED_IMAGE);
            record.setImgType(bo.getImageType());
            record.setImgSize(bo.getSize());
            record.setImgOrigFname(bo.getImageName());
            // 去掉.jpg
            if (bo.getImageName().lastIndexOf(".") != -1) {
                record.setImgName(bo.getImageName().substring(0, bo.getImageName().lastIndexOf(".")));
            } else {
                record.setImgName(bo.getImageName());
            }

            record.setImgPath(bo.getRelativeFilePath());
            record.setImgUrl(bo.getImgUrl());
            record.setImgCatId(0);
            record.setImgWidth(bo.getWidth());
            record.setImgHeight(bo.getHeight());
            records.add(record);
            imgRelativePath.add(bo.getRelativeFilePath());
        }

        db().batchInsert(records).execute();
        return imgRelativePath;
    }

    /**
     * 根据图片orgName查询图片信息
     *
     * @param imgOrgNames 原名集合
     * @return 图片信息集合
     */
    public List<UploadedImageDo> getImgsByImgOrgNames(List<String> imgOrgNames) {
        return db().select(UPLOADED_IMAGE.IMG_ORIG_FNAME, UPLOADED_IMAGE.IMG_PATH).from(UPLOADED_IMAGE)
            .where(UPLOADED_IMAGE.IMG_ORIG_FNAME.in(imgOrgNames).and(UPLOADED_IMAGE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)))
            .fetchInto(UploadedImageDo.class);
    }

    /**
     * 得到图片信息
     *
     * @param imageId
     * @return
     */
    public UploadedImageDo getImageById(Integer imageId) {
        return db().selectFrom(UPLOADED_IMAGE).where(UPLOADED_IMAGE.IMG_ID.eq((imageId))).fetchAnyInto(UploadedImageDo.class);
    }

    /**
     * 获取总大小（不包括删除）
     *
     * @return
     */
    public Integer getAllSize() {
        Byte noDel = 0;
        BigDecimal imageSize =
            db().select(DSL.sum(UPLOADED_IMAGE.IMG_SIZE))
                .from(UPLOADED_IMAGE)
                .where(UPLOADED_IMAGE.SHOP_ID.eq(this.getShopId()))
                .and(UPLOADED_IMAGE.IMG_WIDTH.gt(0))
                .and(UPLOADED_IMAGE.IMG_HEIGHT.gt(0))
                .and(UPLOADED_IMAGE.DEL_FLAG.eq(noDel))
                .fetchOne().value1();
        return imageSize == null ? 0 : imageSize.intValue();
    }


    /**
     * 获取有效(未删除)图片的大小
     *
     * @return BigDecimal
     */
    public BigDecimal getValidImageTotalSize() {
        BigDecimal size = db().select(DSL.sum(UPLOADED_IMAGE.IMG_SIZE))
            .from(UPLOADED_IMAGE)
            .where(UPLOADED_IMAGE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .fetchOne()
            .value1();
        return size == null ? BigDecimal.ZERO : size;
    }

    /**
     * 通过用户id获取有效图片集合
     * @param userId 用户id
     * @return 有效图片信息集合
     */
    public List<UploadedImageDo> getValidImageByUserId(Integer userId) {
        return db().select(UPLOADED_IMAGE.IMG_ID, UPLOADED_IMAGE.IMG_NAME, UPLOADED_IMAGE.IMG_URL)
            .from(UPLOADED_IMAGE)
            .where(UPLOADED_IMAGE.USER_ID.eq(userId))
            .and(UPLOADED_IMAGE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .fetchInto(UploadedImageDo.class);
    }
}
