package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.common.pojo.shop.table.SortDo;
import com.meidianyi.shop.dao.shop.sort.SortDao;
import com.meidianyi.shop.service.pojo.shop.medical.sort.vo.GoodsSortVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2020年07月09日
 */
@Service
public class MedicalGoodsSortService {

    @Autowired
    private SortDao sortDao;

    /**
     * 根据分类id集合获取对应的分类信息
     * @param sortIds
     * @return
     */
    public List<GoodsSortVo> listGoodsSortVos(List<Integer> sortIds) {
        List<SortDo> sortDos = sortDao.listSortDosBySortIds(sortIds);
        List<GoodsSortVo> sortVos = sortDos.stream().map(sortDo -> {
            GoodsSortVo vo = new GoodsSortVo();
            vo.setSortId(sortDo.getSortId());
            vo.setSortName(sortDo.getSortName());
            return vo;
        }).collect(Collectors.toList());
        return sortVos;
    }

    /**
     *
     * @param sortIds
     * @return
     */
    public Map<Integer,GoodsSortVo> getGoodsSortVosIdMap(List<Integer> sortIds){
        List<GoodsSortVo> sortVos = listGoodsSortVos(sortIds);
        return sortVos.stream().collect(Collectors.toMap(GoodsSortVo::getSortId, Function.identity(), (x1, x2) -> x1));
    }
}
