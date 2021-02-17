package com.meidianyi.shop.service.pojo.wxapp.share;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 拼团活动画报，是否需要重新生成新图片规则类
 * @author 李晓冰
 * @date 2020年01月03日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PictorialRule implements Rule {
    private Timestamp goodsUpdateTime;
    private Timestamp activityUpdateTime;
    /**店铺配置颜色*/
    Integer red;
    Integer green;
    Integer blue;
}
