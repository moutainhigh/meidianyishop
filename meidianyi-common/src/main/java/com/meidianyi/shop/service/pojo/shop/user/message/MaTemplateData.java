package com.meidianyi.shop.service.pojo.shop.user.message;

import lombok.Builder;
import lombok.Data;

/**
 * @author luguangyao
 */
@Data
@Builder
public class MaTemplateData {

	/**参考SubcribeTemplateCategory*/
    private String config;

    private MaSubscribeData data;
}
