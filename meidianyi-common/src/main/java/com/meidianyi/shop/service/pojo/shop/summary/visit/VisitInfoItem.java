package com.meidianyi.shop.service.pojo.shop.summary.visit;

import com.meidianyi.shop.service.foundation.util.I18N;
import lombok.Builder;
import lombok.Data;

/**
 * 访问分布 key 对应关系（用于出参）
 *
 * @author 郑保乐
 */
@Data
@Builder
public class VisitInfoItem {

    /**
     * 统计项名称
     */
    @I18N(propertiesFileName = "source")
    private String name;
    /**
     * 统计项序号
     */
    private Integer key;
    /**
     * 数值
     */
    private Integer value;
    /**
     * 是否显示
     */
    @Builder.Default
    private Integer isShow = 1;
}
