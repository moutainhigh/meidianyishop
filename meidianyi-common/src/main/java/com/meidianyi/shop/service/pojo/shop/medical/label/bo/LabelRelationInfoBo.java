package com.meidianyi.shop.service.pojo.shop.medical.label.bo;

import lombok.Data;

import java.util.List;

/**
 * 标签所关联的信息类
 * @author 李晓冰
 * @date 2020年07月09日
 */
@Data
public class LabelRelationInfoBo {
    private Integer labelId;
    /**
     * 是否关联了全部商品
     */
    private Boolean isAll;
    /**
     * 关联的所有分类信息
     */
    private List<Integer> sortIds;
    /**
     * 关联的所有商品信息
     */
    private List<Integer> goodsIds;
}
