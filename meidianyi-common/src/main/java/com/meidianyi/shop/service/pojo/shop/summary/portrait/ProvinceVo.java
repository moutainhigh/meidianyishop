package com.meidianyi.shop.service.pojo.shop.summary.portrait;

import lombok.Data;

import java.util.List;

/**
 * 按省份统计出参
 *
 * @author 郑保乐
 */
@Data
public class ProvinceVo {

    private List<PortraitItem> list;
    private Integer sum;

    public void setList(List<PortraitItem> list) {
        this.list = list;
        setSum(list.parallelStream().mapToInt(PortraitItem::getValue).sum());
    }
}
