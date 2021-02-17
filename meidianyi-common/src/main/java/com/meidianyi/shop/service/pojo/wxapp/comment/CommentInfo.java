package com.meidianyi.shop.service.pojo.wxapp.comment;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.CommentDetailVo;
import lombok.Data;

import java.util.List;

/**
 * 小程序单商品评价详情
 * @author liangchen
 * @date 2020.03.26
 */
@Data
public class CommentInfo {
    private PageResult<MpGoodsCommentVo> comment;
    private List<CommentDetailVo.CommentLevelInfo> number;
}
