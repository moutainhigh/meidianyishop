package com.meidianyi.shop.service.pojo.shop.market.commentaward;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/8/20 14:19
 */
@Getter
@Setter
public class CommentAwardIdParam {

    @NotNull
    private Integer id;
}
