package com.meidianyi.shop.service.shop.config;

import com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsRecommendSortConfig;
import com.meidianyi.shop.service.shop.image.ImageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 李晓冰
 * @date 2019年08月06日
 */
@Service
public class GoodsRecommendSortConfigService extends BaseShopConfigService {

    @Autowired ImageService imageService;

    final static String K_RECOMMEND_SORT = "recommend_sort";
    /**
     *  获取推荐分类配置信息
     * @return
     */
    public GoodsRecommendSortConfig getRecommendSortConfig() {
        GoodsRecommendSortConfig config = getJsonObject(K_RECOMMEND_SORT, GoodsRecommendSortConfig.class, new GoodsRecommendSortConfig());
        config.setRecommendSortImg(getImgFullUrlUtil(config.getRecommendSortImgPath()));
        return config;
    }

    /**
     * 设置推荐分类配置信息
     * @param recommendSortConfig 配置信息
     */
    public void setRecommendSortConfig(GoodsRecommendSortConfig recommendSortConfig) {
       setJsonObject(K_RECOMMEND_SORT,recommendSortConfig);
    }
    /**
     * 将相对路劲修改为全路径
     *
     * @param relativePath 相对路径
     * @return null或全路径
     */
    private String getImgFullUrlUtil(String relativePath) {
        if (StringUtils.isBlank(relativePath)) {
            return null;
        } else {
            return imageService.imageUrl(relativePath);
        }
    }
}
