package com.meidianyi.shop.service.pojo.shop.summary.portrait;

import com.meidianyi.shop.service.pojo.shop.summary.KeyValueChart;
import lombok.Data;

import java.util.List;

/**
 * 用户画像数据
 *
 * @author 郑保乐
 */
@Data
public class Portrait {

    private List<PortraitItem> ages;
    private KeyValueChart agesFirst;
    private List<PortraitItem> city;
    private List<PortraitDeviceItem> devices;
    private List<PortraitItem> genders;
    private List<PortraitItem> platforms;
    private List<PortraitItem> province;
}
