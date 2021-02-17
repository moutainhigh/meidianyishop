package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.wxapp.comment.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品评价
 *
 * @author liangchen 2019.10.15
 */
@RestController
@RequestMapping("/api/wxapp/comment")
public class WxAppCommentController extends WxAppBaseController {

  /**
   * 商品评价获取待评价/已评价商品列表
   *
   * @param param
   * @return commentList
   */
  @PostMapping("/list")
  public JsonResult commentList(@RequestBody CommentListParam param) {

    List<CommentListVo> commentList = shop().goods.goodsComment.commentList(param);

    return success(commentList);
  }

  /**
   * 用户添加商品评价
   *
   * @param param
   */
  @PostMapping("/add")
  public JsonResult addCommentByUser(@RequestBody AddCommentParam param) {
    try {
      shop().goods.goodsComment.addCommentByUser(param);
    } catch (MpException e) {
      return fail(e.getErrorCode());
    }
    return success();
  }

    /**
     * 单商品评价详情
     * @param param
     */
    @PostMapping("/goods")
    public JsonResult getGoodsComment(@RequestBody MpGoodsCommentParam param) {
        CommentInfo commentInfo = shop().goods.goodsComment.goodsComment(param);
        return success(commentInfo);
    }
}
