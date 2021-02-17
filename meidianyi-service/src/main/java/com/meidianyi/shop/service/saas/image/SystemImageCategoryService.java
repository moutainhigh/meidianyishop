package com.meidianyi.shop.service.saas.image;

import static com.meidianyi.shop.common.foundation.data.JsonResult.LANGUAGE_TYPE_MSG;
import static com.meidianyi.shop.db.main.tables.UploadedImageCategory.UPLOADED_IMAGE_CATEGORY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Result;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.UploadedImageCategoryRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.image.category.ImageCategoryParam;
import com.meidianyi.shop.service.pojo.shop.video.category.CategoryTreeItemVo;

/**
 * @author 新国，孔德成
 */
@Service

public class SystemImageCategoryService extends MainBaseService {
	 
    private static final String ROOT_NAME = "我的图片";

    /**
     * 添加分类
     *
     * @param cat
     * @return
     */
    public Boolean addCategory(ImageCategoryParam cat) {
        UploadedImageCategoryRecord record = db().newRecord(UPLOADED_IMAGE_CATEGORY, cat);
        record.setShopId(0);
        record.insert();
//        record.refresh();
        //父节点不是顶节点，查询父节点的ids
        if (!cat.getImgCatParentId().equals(0)) {
            UploadedImageCategoryRecord parent = this.getCategoryById(cat.getImgCatParentId());
            if (parent != null) {
                record.setCatIds(parent.getCatIds() + "," + record.getImgCatId());
                record.setLevel((byte) (parent.getLevel() + 1));
                return record.update() > 0;
            }
        }
        record.setImgCatParentId(0);
        record.setCatIds(String.valueOf(record.getImgCatId()));
        record.setLevel((byte) 1);
        return record.update() > 0;
    }

    /**
     * 删除分类及其子分类
     *
     * @param catId
     * @return
     */
    public int removeCategory(Integer catId) {
        if (catId == 0) {
            return 0;
        }
        List<Integer> ids = this.getChildCategoryIds(catId);
        return db()
                .delete(UPLOADED_IMAGE_CATEGORY)
                .where(UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID.in(ids.toArray(new Integer[0])))
                .execute();
    }

    /**
     * 得到子分类ID列表
     *
     * @param parentId
     * @return
     */
    public List<Integer> getChildCategoryIds(Integer parentId) {
       Result<UploadedImageCategoryRecord> result= getChildCategory(parentId);
        return result==null? new ArrayList<Integer>(): result.getValues(UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID);
    }

    /**
     * 获取子分类列表
     * @param parentId
     * @return
     */
    private Result<UploadedImageCategoryRecord> getChildCategory(Integer parentId) {
        UploadedImageCategoryRecord record = this.getCategoryById(parentId);
        if (record==null){
            return null;
        }

        Result<UploadedImageCategoryRecord> records = db().selectFrom(UPLOADED_IMAGE_CATEGORY)
                .where(UPLOADED_IMAGE_CATEGORY.CAT_IDS.like(this.prefixLikeValue(record.getCatIds() + ",")))
                .fetch();
        records.add(record);
        return records;
    }

    /**
     * 得到子分类列表
     *
     * @param parentId
     * @param includeParentId
     * @param includeDecent
     * @return
     */
    public Result<UploadedImageCategoryRecord> getChildCategory(Integer parentId, boolean includeParentId,
                                                                boolean includeDecent) {
        if (parentId == 0) {
            if (includeDecent) {
                return this.getAll();
            } else {
                return db().selectFrom(UPLOADED_IMAGE_CATEGORY).where(DSL.val(1).eq(2)).fetch();
            }
        }
        UploadedImageCategoryRecord record = this.getCategoryById(parentId);
        if (record == null) {
            if (includeDecent) {
                return db().selectFrom(UPLOADED_IMAGE_CATEGORY).fetch();
            } else {
                return db().selectFrom(UPLOADED_IMAGE_CATEGORY).fetch();
            }
        }
        if (includeDecent) {
            return db()
                    .selectFrom(UPLOADED_IMAGE_CATEGORY)
                    .where(UPLOADED_IMAGE_CATEGORY.CAT_IDS.like(this.prefixLikeValue(record.getCatIds())))
                    .fetch();
        } else {
            return db()
                    .selectFrom(UPLOADED_IMAGE_CATEGORY)
                    .where(UPLOADED_IMAGE_CATEGORY.CAT_IDS.eq(record.getCatIds()))
                    .fetch();
        }
    }

    /**
     * 得到所有分类
     *
     * @return
     */
    public Result<UploadedImageCategoryRecord> getAll() {
        return db().fetch(UPLOADED_IMAGE_CATEGORY);
    }

    /**
     * 移动分类
     *
     * @param catId
     * @param newParentId
     * @return
     */
    public int moveCategory(Integer catId, Integer newParentId) {
        UploadedImageCategoryRecord record = this.getCategoryById(catId);
        if (record == null) {
            return 0;
        }
        UploadedImageCategoryRecord parentRecord = newParentId == 0 ? null : this.getCategoryById(newParentId);
        Integer levelDiff = parentRecord != null ? parentRecord.getLevel() + 1 - record.getLevel()
                : 0 - record.getLevel();
        String oldCatIdsPrefix = record.getCatIds();
        String newCatIdPreifx = parentRecord != null ? parentRecord.getCatIds() + "," + record.getImgCatId()
                : record.getImgCatId().toString();
        return db().update(UPLOADED_IMAGE_CATEGORY)
                .set(UPLOADED_IMAGE_CATEGORY.CAT_IDS,
                        DSL.concat(newCatIdPreifx,
                                DSL.substring(UPLOADED_IMAGE_CATEGORY.CAT_IDS, oldCatIdsPrefix.length() + 1)))
                .set(UPLOADED_IMAGE_CATEGORY.LEVEL, UPLOADED_IMAGE_CATEGORY.LEVEL.add(levelDiff))
                .where(UPLOADED_IMAGE_CATEGORY.CAT_IDS.like(this.prefixLikeValue(oldCatIdsPrefix)))
                .execute();
    }

    public Integer[] getUpCatIds(Integer catId) {
        List<Integer> catIds = new ArrayList<Integer>();
        if (catId > 0) {
            UploadedImageCategoryRecord cat = this.getCategoryById(catId);
            if (cat == null) {
                return catIds.toArray(new Integer[0]);
            }
            Integer parentCatId = cat.getImgCatParentId();
            while (parentCatId > 0) {
                cat = this.getCategoryById(parentCatId);
                catIds.add(cat.getImgCatId());
                parentCatId = cat.getImgCatParentId();
            }
            Integer[] ids = catIds.toArray(new Integer[0]);
            ArrayUtils.reverse(ids);
            return ids;
        }
        return catIds.toArray(new Integer[0]);
    }

    /**
     * 得到分类
     *
     * @param catId
     * @return
     */
    public UploadedImageCategoryRecord getCategoryById(Integer catId) {
        return db().selectFrom(UPLOADED_IMAGE_CATEGORY)
                .where(UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID.eq(catId)).fetchAny();
    }


    /**
     * 判断是否有子分类
     *
     * @param parentCatId
     * @return
     */
    public boolean hasChildCategory(Integer parentCatId) {
        return db().fetchCount(UPLOADED_IMAGE_CATEGORY, UPLOADED_IMAGE_CATEGORY.IMG_CAT_PARENT_ID.eq(parentCatId)) > 0;
    }

    final public static class CategoryMap {
        public Map<Integer, UploadedImageCategoryRecord> map = new HashMap<Integer, UploadedImageCategoryRecord>();
        public Map<Integer, List<UploadedImageCategoryRecord>> parent = new HashMap<Integer, List<UploadedImageCategoryRecord>>();
    }


    /**
     * 得到最大等级
     *
     * @return
     */
    public Integer getCategoryMaxLevel() {
        Object result = db().select(DSL.max(UPLOADED_IMAGE_CATEGORY.LEVEL)).from(UPLOADED_IMAGE_CATEGORY).fetchAny(0);
        return Util.getInteger(result);
    }


    /**
     * 得到分类Map
     *
     * @param maxLevel
     * @return
     */
    protected CategoryMap getCategoryMap(Byte maxLevel) {
        CategoryMap result = new CategoryMap();
        SelectWhereStep<UploadedImageCategoryRecord> select = db().selectFrom(UPLOADED_IMAGE_CATEGORY);
        if (maxLevel != -1) {
            select.where(UPLOADED_IMAGE_CATEGORY.LEVEL.le(maxLevel));
        }
        select.orderBy(UPLOADED_IMAGE_CATEGORY.IMG_CAT_PARENT_ID.asc(),
                UPLOADED_IMAGE_CATEGORY.SORT.desc(),
                UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID.asc());
        Result<UploadedImageCategoryRecord> records = select.fetch();
        for (UploadedImageCategoryRecord record : records) {
            result.map.put(record.getImgCatId().intValue(), record);
            List<UploadedImageCategoryRecord> list = result.parent.get(record.getImgCatParentId());
            if (list == null) {
                list = new ArrayList<UploadedImageCategoryRecord>();
                result.parent.put(record.getImgCatParentId(), list);
            }
            list.add(record);
        }
        return result;
    }


    /**
     * 得到Tree图片目录列表
     *
     * @param openId
     * @return
     */
    public List<CategoryTreeItemVo> getImageCategoryForTree(Integer openId) {
        List<CategoryTreeItemVo> result = new ArrayList<CategoryTreeItemVo>();
        CategoryTreeItemVo root = new CategoryTreeItemVo();
        root.setName(ROOT_NAME);
        root.setId(openId);
        Result<UploadedImageCategoryRecord> records = this.getAll();
        this.getImageCategoryTree(root, records, 2);
        result.add(root);
        return result;
    }

    /**
     * 得到Tree图片目录列表
     *
     * @return
     * @param lang
     */
    public List<CategoryTreeItemVo> getImageCategoryForTree(String lang) {
        List<CategoryTreeItemVo> result = new ArrayList<CategoryTreeItemVo>();
        CategoryTreeItemVo root = new CategoryTreeItemVo();
        root.setName(Util.translateMessage(lang, JsonResultMessage.MSG_IMAGE_CATEGORY_IMGCATNAME_ROOT_NAME, LANGUAGE_TYPE_MSG));
        Result<UploadedImageCategoryRecord> records = this.getAll();
        this.getImageCategoryTree(root, records, root.getLevel());
        result.add(root);
        return result;
    }
    /**
     * 遍历图片标签树
     *
     * @param root
     * @param categoryTreeItemList
     * @param level
     * @return
     */
    public CategoryTreeItemVo getImageCategoryTree(CategoryTreeItemVo root, List<UploadedImageCategoryRecord> categoryTreeItemList, Integer level) {
        for (int i = 0; i < categoryTreeItemList.size(); i++) {
            UploadedImageCategoryRecord item = categoryTreeItemList.get(i);
            if (root.getId().equals(item.getImgCatParentId())) {
                CategoryTreeItemVo child = new CategoryTreeItemVo();
                child.setId(item.getImgCatId());
                child.setName(item.getImgCatName());
                child.setLevel(level);
                root.getChild().add(child);
                categoryTreeItemList.remove(item);
                i--;
                getImageCategoryTree(child, categoryTreeItemList, level + 1);
            }
        }
        return root;
    }


    /**
     * 设置分类名称
     *
     * @param catId
     * @param catName
     * @return
     */
    public int setCategoryName(Integer catId, String catName) {
        return db().update(UPLOADED_IMAGE_CATEGORY)
                .set(UPLOADED_IMAGE_CATEGORY.IMG_CAT_NAME, catName)
                .where(UPLOADED_IMAGE_CATEGORY.IMG_CAT_ID.eq(catId))
                .execute();
    }


}
