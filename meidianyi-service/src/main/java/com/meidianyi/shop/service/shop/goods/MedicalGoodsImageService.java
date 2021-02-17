package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.common.pojo.shop.table.GoodsImgDo;
import com.meidianyi.shop.dao.shop.img.GoodsImgDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年07月11日
 */
@Service
public class MedicalGoodsImageService {
    @Autowired
    private GoodsImgDao goodsImgDao;

    /**
     * 插入商品关联的图片信息
     * @param imgPaths 图片相对路径
     * @param goodsId 商品id
     */
    public void batchInsertGoodsImageRelation(List<String> imgPaths, Integer goodsId) {
        List<GoodsImgDo> goodsImgDos = new ArrayList<>(imgPaths.size());
        for (String imgPath :imgPaths) {
            goodsImgDos.add(new GoodsImgDo(goodsId, imgPath));
        }
        goodsImgDao.batchInsert(goodsImgDos);
    }

    /**
     * 根据商品id删除对应图片关联
     * @param goodsId
     */
    public void deleteGoodsImageRelation(Integer goodsId) {
        goodsImgDao.deleteByGoodsId(goodsId);
    }

    /**
     * 查询商品关联的图片相对地址
     * @param goodsId
     * @return
     */
    public List<String> listGoodsImages(Integer goodsId) {
        return goodsImgDao.listByGoodsId(goodsId);
    }
}
