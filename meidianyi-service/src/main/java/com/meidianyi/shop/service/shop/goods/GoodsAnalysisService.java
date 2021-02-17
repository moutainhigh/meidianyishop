package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.shop.goods.GoodsAnalysisDao;
import com.meidianyi.shop.service.pojo.shop.goods.goodsanalysis.GoodsAnalysisListParam;
import com.meidianyi.shop.service.pojo.shop.goods.goodsanalysis.GoodsAnalysisListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chenjie
 * @date 2020年09月17日
 */
@Service
public class GoodsAnalysisService {
    @Autowired
    protected GoodsAnalysisDao goodsAnalysisDao;
    public PageResult<GoodsAnalysisListVo> getGoodsAnalysisPageList(GoodsAnalysisListParam param){
        PageResult<GoodsAnalysisListVo> data = goodsAnalysisDao.getGoodsAnalysisPageList(param);
        for (GoodsAnalysisListVo info: data.getDataList()){
            //todo goodsLabel
        }
        return data;
    }
}
