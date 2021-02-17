package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.service.pojo.shop.config.pledge.group.UpdateGroup;
import com.meidianyi.shop.service.pojo.shop.market.commentaward.CommentAwardVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.market.commentaward.CommentAwardIdParam;
import com.meidianyi.shop.service.pojo.shop.market.commentaward.CommentAwardListParam;
import com.meidianyi.shop.service.pojo.shop.market.commentaward.CommentAwardListVo;
import com.meidianyi.shop.service.pojo.shop.market.commentaward.CommentAwardParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 评价有礼
 *
 * @author 孔德成
 * @date 2019/8/20 9:52
 */
@RestController
@RequestMapping("/api/admin/market/comment/award")
public class AdminCommentAwardController extends AdminBaseController {

    /**
     *  添加
     * @param param CommentAwardParam
     * @return Json
     */
    @PostMapping("/add")
    public JsonResult addCommentAwardActivity(@RequestBody @Valid CommentAwardParam param) {
        int flag = shop().commentAward.addCommentAwardActivity(param);
        if (flag==0){
            return fail();
        }
        return success();
    }

    /**
     * 跟新
     * @param param CommentAwardActivity
     * @return json
     */
    @PostMapping("/update")
    public JsonResult updateCommentAwardActivity(@RequestBody @Validated(UpdateGroup.class) CommentAwardParam param){
        int flag = shop().commentAward.updateCommentAwardActivity(param);
        if (flag==0){
            return fail();
        }
        return success();
    }

    /**
     * 删除
     * @param param CommentAwardActivity
     * @return json
     */
    @PostMapping("/delete")
    public JsonResult deleteCommentAwardActivity(@RequestBody @Valid CommentAwardIdParam param){
        int flag = shop().commentAward.deleteCommentAwardActivity(param);
        if (flag==0){
            return fail();
        }
        return success();
    }

    /**
     * 跟新状态
     * @param param id
     * @return  json
     */
    @PostMapping("/change/status")
    public JsonResult changeCommentAwardActivity( @RequestBody @Valid CommentAwardIdParam param){
        int flag = shop().commentAward.changeCommentAwardActivity(param.getId());
        if (flag==0){
            return fail();
        }
        return success();
    }

    /**
     * 查询活动详情
     * @param param id
     * @return json
     */
    @PostMapping("/get")
    public JsonResult getCommentAwardActivity( @RequestBody @Valid CommentAwardIdParam param){
        CommentAwardVo commentAwardActivity = shop().commentAward.getCommentAwardActivity(param.getId());
        return success(commentAwardActivity);
    }

    /**
     * 查询活动列表
     * @param param
     * @return
     */
    @PostMapping("/list")
    public JsonResult getCommentAwardActivityList( @RequestBody @NotNull CommentAwardListParam param){
        PageResult<CommentAwardListVo> pageResult = shop().commentAward.getCommentAwardActivityList(param);
        return success(pageResult);
    }


}
