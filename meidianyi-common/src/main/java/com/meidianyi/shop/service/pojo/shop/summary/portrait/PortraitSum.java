package com.meidianyi.shop.service.pojo.shop.summary.portrait;

import lombok.Data;

/**
 * 用户画像数据
 *
 * @author 郑保乐
 */
@Data
public class PortraitSum {

    private Integer province;
    private Integer city;
    private Integer genders;
    private Integer platforms;
    private Integer devices;
    private Integer ages;
}
