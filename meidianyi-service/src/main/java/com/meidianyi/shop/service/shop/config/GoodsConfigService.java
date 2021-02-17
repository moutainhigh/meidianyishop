package com.meidianyi.shop.service.shop.config;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-03-31 13:54
 **/
@Service
public class GoodsConfigService extends BaseShopConfigService {
    /**
     * 商品导出的表头列，每次导出订单时更新
     * 值为一个String数组，已选的表头名称
     */
    final public static String K_GOODS_EXPORT_LIST= "goods_export_list";

    /**
     * 取商品导出表头
     * @return
     */
    public List<String> getGoodsExportList() {
        return this.getJsonObject(K_GOODS_EXPORT_LIST, new TypeReference<List<String>>(){});
    }

    /**
     * 设置导出表头
     * @return
     */
    public int setGoodsExportList(List<String> columns) {
        Assert.isTrue(columns.size() >= 0,"columns size nedd >=0");
        return this.setJsonObject(K_GOODS_EXPORT_LIST, columns);
    }
}
