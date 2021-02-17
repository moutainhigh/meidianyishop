package com.meidianyi.shop.service.pojo.shop.image;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2019/7/9 14:29
 */
@Data
public class BatchMoveImageParam {

    /**
     * 图片分组id
     */
    @NotNull
    Integer imageCatId;
    List<@NotNull Integer> imageIds;


}
