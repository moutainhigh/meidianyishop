package com.meidianyi.shop.service.shop.config;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-10-14 15:31
 **/
@Service
public class OrderExportConfigService extends BaseShopConfigService {

    /**
     * 订单导出的表头列，每次导出订单时更新
     * 值为一个json对象，属性即表头
     */
    final public static String K_ORDER_EXPORT_LIST= "order_export_list";

    /**
     * 取订单导出表头
     * @return
     */
    public List<String> getOrderExportList() {
        return this.getJsonObject(K_ORDER_EXPORT_LIST, new TypeReference<List<String>>(){});
    }

    /**
     * 设置导出表头
     * @return
     */
    public int setOrderExportList(List<String> columns) {
    	Assert.isTrue(columns.size() >= 0,"columns size nedd >=0");
        return this.setJsonObject(K_ORDER_EXPORT_LIST, columns);
    }
}
