package com.meidianyi.shop.service.shop.video;

import static com.meidianyi.shop.db.shop.tables.UploadedVideoCategory.UPLOADED_VIDEO_CATEGORY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Result;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.UploadedVideoCategoryRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.video.category.CategoryTreeItemVo;
import com.meidianyi.shop.service.pojo.shop.video.category.VideoCategoryParam;

/**
 * @author 新国，孔德成
 */
@Service

public class VideoCategoryService extends ShopBaseService {

    private static final String ROOT_NAME = "我的视频";

    /**
     * 添加分类
     *
     * @param cat
     * @return
     */
    public Boolean addCategory(VideoCategoryParam cat) {
        UploadedVideoCategoryRecord record = db().newRecord(UPLOADED_VIDEO_CATEGORY, cat);
        record.setShopId(getShopId());
        record.insert();
        //父节点不是顶节点，查询父节点的ids
        if (!cat.getVideoCatParentId().equals(0)) {
            UploadedVideoCategoryRecord parent = this.getCategoryById(cat.getVideoCatParentId());
            if (parent != null) {
                record.setCatIds(parent.getCatIds() + "," + record.getVideoCatId());
                record.setLevel((byte) (parent.getLevel() + 1));
                return record.update() > 0;
            }
        }
        record.setVideoCatParentId(0);
        record.setCatIds(String.valueOf(record.getVideoCatId()));
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
                .delete(UPLOADED_VIDEO_CATEGORY)
                .where(UPLOADED_VIDEO_CATEGORY.VIDEO_CAT_ID.in(ids.toArray(new Integer[0])))
                .execute();
    }

    /**
     * 得到子分类ID列表
     *
     * @param parentId
     * @return
     */
    public List<Integer> getChildCategoryIds(Integer parentId) {
       Result<UploadedVideoCategoryRecord> result= getChildCategory(parentId);
        return result==null? new ArrayList<Integer>(): result.getValues(UPLOADED_VIDEO_CATEGORY.VIDEO_CAT_ID);
    }

    /**
     * 获取子分类列表
     * @param parentId
     * @return
     */
    private Result<UploadedVideoCategoryRecord> getChildCategory(Integer parentId) {
        UploadedVideoCategoryRecord record = this.getCategoryById(parentId);
        if (record==null){
            return null;
        }

        Result<UploadedVideoCategoryRecord> records = db().selectFrom(UPLOADED_VIDEO_CATEGORY)
                .where(UPLOADED_VIDEO_CATEGORY.CAT_IDS.like(this.prefixLikeValue(record.getCatIds() + ",")))
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
    public Result<UploadedVideoCategoryRecord> getChildCategory(Integer parentId, boolean includeParentId,
                                                                boolean includeDecent) {
        if (parentId == 0) {
            if (includeDecent) {
                return this.getAll();
            } else {
                return db().selectFrom(UPLOADED_VIDEO_CATEGORY).where(DSL.val(1).eq(2)).fetch();
            }
        }
        UploadedVideoCategoryRecord record = this.getCategoryById(parentId);
        if (record == null) {
            if (includeDecent) {
                return db().selectFrom(UPLOADED_VIDEO_CATEGORY).fetch();
            } else {
                return db().selectFrom(UPLOADED_VIDEO_CATEGORY).fetch();
            }
        }
        if (includeDecent) {
            return db()
                    .selectFrom(UPLOADED_VIDEO_CATEGORY)
                    .where(UPLOADED_VIDEO_CATEGORY.CAT_IDS.like(this.prefixLikeValue(record.getCatIds())))
                    .fetch();
        } else {
            return db()
                    .selectFrom(UPLOADED_VIDEO_CATEGORY)
                    .where(UPLOADED_VIDEO_CATEGORY.CAT_IDS.eq(record.getCatIds()))
                    .fetch();
        }
    }

    /**
     * 得到所有分类
     *
     * @return
     */
    public Result<UploadedVideoCategoryRecord> getAll() {
        return db().fetch(UPLOADED_VIDEO_CATEGORY);
    }

    /**
     * 移动分类
     *
     * @param catId
     * @param newParentId
     * @return
     */
    public int moveCategory(Integer catId, Integer newParentId) {
        UploadedVideoCategoryRecord record = this.getCategoryById(catId);
        if (record == null) {
            return 0;
        }
        UploadedVideoCategoryRecord parentRecord = newParentId == 0 ? null : this.getCategoryById(newParentId);
        Integer levelDiff = parentRecord != null ? parentRecord.getLevel() + 1 - record.getLevel()
                : 0 - record.getLevel();
        String oldCatIdsPrefix = record.getCatIds();
        String newCatIdPreifx = parentRecord != null ? parentRecord.getCatIds() + "," + record.getVideoCatId()
                : record.getVideoCatId().toString();
        return db().update(UPLOADED_VIDEO_CATEGORY)
                .set(UPLOADED_VIDEO_CATEGORY.CAT_IDS,
                        DSL.concat(newCatIdPreifx,
                                DSL.substring(UPLOADED_VIDEO_CATEGORY.CAT_IDS, oldCatIdsPrefix.length() + 1)))
                .set(UPLOADED_VIDEO_CATEGORY.LEVEL, UPLOADED_VIDEO_CATEGORY.LEVEL.add(levelDiff))
                .where(UPLOADED_VIDEO_CATEGORY.CAT_IDS.like(this.prefixLikeValue(oldCatIdsPrefix)))
                .execute();
    }

    public Integer[] getUpCatIds(Integer catId) {
        List<Integer> catIds = new ArrayList<Integer>();
        if (catId > 0) {
            UploadedVideoCategoryRecord cat = this.getCategoryById(catId);
            if (cat == null) {
                return catIds.toArray(new Integer[0]);
            }
            Integer parentCatId = cat.getVideoCatParentId();
            while (parentCatId > 0) {
                cat = this.getCategoryById(parentCatId);
                catIds.add(cat.getVideoCatId());
                parentCatId = cat.getVideoCatParentId();
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
    public UploadedVideoCategoryRecord getCategoryById(Integer catId) {
        return db().selectFrom(UPLOADED_VIDEO_CATEGORY)
                .where(UPLOADED_VIDEO_CATEGORY.VIDEO_CAT_ID.eq(catId)).fetchAny();
    }


    /**
     * 判断是否有子分类
     *
     * @param parentCatId
     * @return
     */
    public boolean hasChildCategory(Integer parentCatId) {
        return db().fetchCount(UPLOADED_VIDEO_CATEGORY, UPLOADED_VIDEO_CATEGORY.VIDEO_CAT_PARENT_ID.eq(parentCatId)) > 0;
    }

    final public static class CategoryMap {
        public Map<Integer, UploadedVideoCategoryRecord> map = new HashMap<Integer, UploadedVideoCategoryRecord>();
        public Map<Integer, List<UploadedVideoCategoryRecord>> parent = new HashMap<Integer, List<UploadedVideoCategoryRecord>>();
    }


    /**
     * 得到最大等级
     *
     * @return
     */
    public Integer getCategoryMaxLevel() {
        Object result = db().select(DSL.max(UPLOADED_VIDEO_CATEGORY.LEVEL)).from(UPLOADED_VIDEO_CATEGORY).fetchAny(0);
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
        SelectWhereStep<UploadedVideoCategoryRecord> select = db().selectFrom(UPLOADED_VIDEO_CATEGORY);
        if (maxLevel != -1) {
            select.where(UPLOADED_VIDEO_CATEGORY.LEVEL.le(maxLevel));
        }
        select.orderBy(UPLOADED_VIDEO_CATEGORY.VIDEO_CAT_PARENT_ID.asc(),
                UPLOADED_VIDEO_CATEGORY.SORT.desc(),
                UPLOADED_VIDEO_CATEGORY.VIDEO_CAT_ID.asc());
        Result<UploadedVideoCategoryRecord> records = select.fetch();
        for (UploadedVideoCategoryRecord record : records) {
            result.map.put(record.getVideoCatId().intValue(), record);
            List<UploadedVideoCategoryRecord> list = result.parent.get(record.getVideoCatParentId());
            if (list == null) {
                list = new ArrayList<UploadedVideoCategoryRecord>();
                result.parent.put(record.getVideoCatParentId(), list);
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
    public List<CategoryTreeItemVo> getVideoCategoryForTree(Integer openId) {
        List<CategoryTreeItemVo> result = new ArrayList<CategoryTreeItemVo>();
        CategoryTreeItemVo root = new CategoryTreeItemVo();
        root.setName(ROOT_NAME);
        root.setId(openId);
        Result<UploadedVideoCategoryRecord> records = this.getAll();
        this.getVideoCategoryTree(root, records, 2);
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
    public CategoryTreeItemVo getVideoCategoryTree(CategoryTreeItemVo root, List<UploadedVideoCategoryRecord> categoryTreeItemList, Integer level) {
        for (int i = 0; i < categoryTreeItemList.size(); i++) {
            UploadedVideoCategoryRecord item = categoryTreeItemList.get(i);
            if (root.getId().equals(item.getVideoCatParentId())) {
                CategoryTreeItemVo child = new CategoryTreeItemVo();
                child.setId(item.getVideoCatId());
                child.setName(item.getVideoCatName());
                child.setLevel(level);
                root.getChild().add(child);
                categoryTreeItemList.remove(item);
                i--;
                getVideoCategoryTree(child, categoryTreeItemList, level + 1);
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
        return db().update(UPLOADED_VIDEO_CATEGORY)
                .set(UPLOADED_VIDEO_CATEGORY.VIDEO_CAT_NAME, catName)
                .where(UPLOADED_VIDEO_CATEGORY.VIDEO_CAT_ID.eq(catId))
                .execute();
    }


}
