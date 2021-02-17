package com.meidianyi.shop.service.pojo.saas.shop.image;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2019/7/17 10:57
 */
@Data
public class ShopBatchMoveImageParam {
    /**
     * 图片分组id
     */
    @NotNull
    Integer imageCatId;
    List<@NotNull Integer> imageIds;

}
