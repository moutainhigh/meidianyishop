package com.meidianyi.shop.dao.shop.sort;

import com.meidianyi.shop.common.pojo.shop.table.SortDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.service.pojo.shop.medical.sort.MedicalGoodsSortConstant;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.meidianyi.shop.db.shop.Tables.SORT;

/**
 * 分类dao
 * @author 李晓冰
 * @date 2020年07月07日
 */
@Repository
public class SortDao extends ShopBaseDao {

    /**
     * 获取分类的祖先点id集合
     * @param sortId
     * @return 祖先节点结合
     */
    public List<Integer> getParentSortIds(Integer sortId) {
        List<Integer> parentIds = new ArrayList<>(2);
        Integer parentId = db().select(SORT.PARENT_ID).from(SORT).where(SORT.SORT_ID.eq(sortId)).fetchAny(SORT.PARENT_ID);
        while (parentId != null && !MedicalGoodsSortConstant.ROOT_PARENT.equals(parentId)) {
            parentIds.add(parentId);
            parentId = db().select(SORT.PARENT_ID).from(SORT).where(SORT.SORT_ID.eq(parentId)).fetchAny(SORT.PARENT_ID);
        }
        return parentIds;
    }

    /**
     * 根据分类id集合获取祖先id集合
     * @param sortIds 分类id集合
     * @return
     */
    public List<Integer> getParentSortIds(List<Integer> sortIds) {
        Set<Integer> retIds = new HashSet<>(6);
        do {
            sortIds = db().select(SORT.PARENT_ID).from(SORT).where(SORT.SORT_ID.in(sortIds).and(SORT.PARENT_ID.ne(MedicalGoodsSortConstant.ROOT_PARENT)))
                .fetch(SORT.PARENT_ID);
            retIds.addAll(sortIds);
        } while (sortIds.size() > 0);

        return new ArrayList<>(retIds);
    }

    /**
     * 根据分类id集合获取分类信息
     * @param sortIds 分类id集合
     * @return
     */
    public List<SortDo> listSortDosBySortIds(List<Integer> sortIds) {
        return db().selectFrom(SORT).where(SORT.SORT_ID.in(sortIds))
            .fetchInto(SortDo.class);
    }
}
