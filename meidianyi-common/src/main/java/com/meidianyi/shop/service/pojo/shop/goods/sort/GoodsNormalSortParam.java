package com.meidianyi.shop.service.pojo.shop.goods.sort;

import com.meidianyi.shop.db.shop.tables.records.SortRecord;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品普通分类新增，修改param
 *
 * @author 李晓冰
 * @date 2019年11月21日
 */
@Getter
@Setter
public class GoodsNormalSortParam extends GoodsSort {
    /**
     * 修改时保存原父节点id（允许修改父节点）
     */
    private Integer oldParentId;
    private Byte type = GoodsConstant.NORMAL_SORT;

    /**
     * 转换为对应SortRecords
     * @return {@link SortRecord}
     */
    public SortRecord convertToSortRecord() {
        SortRecord sortRecord = new SortRecord();
        sortRecord.setSortName(sortName);
        sortRecord.setParentId(parentId);
        sortRecord.setLevel(level);
        sortRecord.setFirst(first);
        sortRecord.setSortImg(sortImg);
        sortRecord.setImgLink(imgLink);
        sortRecord.setType(type);

        // 更新操作
        if (sortId != null) {
            sortRecord.setSortId(sortId);
        }
        return sortRecord;
    }
}
