package com.meidianyi.shop.service.pojo.shop.goods.sort;

import com.meidianyi.shop.db.shop.tables.records.SortRecord;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐分类新增，修改param
 * @author 李晓冰
 * @date 2019年11月21日
 */
@Data
public class GoodsRecommendSortParam {
    private Integer sortId;
    private String sortName;
    private Short first;
    private Byte type = GoodsConstant.RECOMMEND_SORT;
    private List<GoodsRecommendSortChild> children;

    @Data
    public static class GoodsRecommendSortChild {
        private Integer sortId;
        private Integer parentId;
        private String sortName;
        private String sortImg;
        private String imgLink;
        private Byte type = GoodsConstant.RECOMMEND_SORT;
    }


    public SortRecord convertParentToRecord() {
        SortRecord sortRecord = new SortRecord();
        sortRecord.setSortName(sortName);
        if (first != null) {
            sortRecord.setFirst(first);
        }
        sortRecord.setParentId(GoodsConstant.ROOT_PARENT_ID);
        sortRecord.setLevel(GoodsConstant.ROOT_LEVEL);
        sortRecord.setType(type);

        //更新操作
        if (sortId != null) {
            sortRecord.setSortId(sortId);
        }

        if (children != null && children.size() > 0) {
            sortRecord.setHasChild(GoodsConstant.HAS_CHILD);
        } else {
            sortRecord.setHasChild(GoodsConstant.HAS_NO_CHILD);
        }
        return sortRecord;
    }

    public List<SortRecord> convertChildrenToRecord() {
        List<SortRecord> records = new ArrayList<>(2);
        if (children == null || children.size() == 0) {
            return records;
        }
        children.forEach(child -> {
            SortRecord sortRecord = new SortRecord();
            sortRecord.setType(GoodsConstant.RECOMMEND_SORT);
            sortRecord.setLevel(GoodsConstant.SECOND_LEVEL);
            sortRecord.setHasChild(GoodsConstant.HAS_NO_CHILD);
            //更新操作
            if (child.getSortId() != null) {
                sortRecord.setSortId(child.getSortId());
            }
            sortRecord.setSortName(child.getSortName());
            sortRecord.setSortImg(child.getSortImg());

            if (child.getParentId() != null) {
                sortRecord.setParentId(child.getParentId());
            }

            if (child.getImgLink() != null) {
                sortRecord.setImgLink(child.getImgLink());
            }
            records.add(sortRecord);
        });
        return records;
    }
}
