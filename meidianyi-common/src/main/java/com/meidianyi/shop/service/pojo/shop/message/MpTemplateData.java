package com.meidianyi.shop.service.pojo.shop.message;

import lombok.Builder;
import lombok.Data;

/**
 * @author luguangyao
 */
@Data
@Builder
public class MpTemplateData {
    private MpTemplateConfig config;

    private String[][] data;
}
