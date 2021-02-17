package com.meidianyi.shop.service.pojo.shop.market.commentaward;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/8/20 14:22
 */
@Data
public class CommentAwardListParam {
    /**
     * 分页信息
     */
    private Integer currentPage;
    private Integer pageRows;

    /**
     * 导航类型 0全部 1进行中 2未开始 3已过期  4已停用
     */
    @NotNull
    @Range(min = 0,max = 4)
    private Byte navType;

}
