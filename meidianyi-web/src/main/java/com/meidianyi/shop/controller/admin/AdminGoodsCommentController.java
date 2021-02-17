package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.goods.comment.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品评论控制器
 *
 * @author liangchen
 * @date 2019年7月7日
 */

@RestController
@RequestMapping("/api/admin/goods/comment")
public class AdminGoodsCommentController extends AdminBaseController {

	/**
	 * 商品评论分页查询
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/list")
	public JsonResult getPageList(@RequestBody GoodsCommentPageListParam param) {

		GoodsCommentListVo pageResult = shop().goods.goodsComment.getPageList(param);

		return success(pageResult);
	}

    /**
     * 获得所有评价有礼奖励
     * @return 评价有礼奖励id和name
     */
    @GetMapping("/award")
    public JsonResult getCommentAward(){
        List<CommentAwardVo> result = shop().goods.goodsComment.getCommentAward();
        return success(result);
    }

	/**
	 * 评论回复
	 * 
	 * @param goodsCommentAnswer 评价id和回复内容
	 * @return 结果信息
	 *
	 */
	@PostMapping("/answer")
	public JsonResult insertAnswer(@RequestBody GoodsCommentAnswerParam goodsCommentAnswer) {

		shop().goods.goodsComment.insertAnswer(goodsCommentAnswer);

		return success();
	}

	/**
	 * 删除回复
	 *
	 * @param goodsCommentId 评论id
	 * @return 结果信息
	 */
	@PostMapping("/delAnswer")
	public JsonResult delAnswer(@RequestBody GoodsCommentIdParam goodsCommentId) {

		shop().goods.goodsComment.delAnswer(goodsCommentId);

		return success();
	}

	/**
	 * 删除评论
	 *
	 * @param goodsCommentId 评论id
	 * @return 结果信息
	 */
	@PostMapping("/delete")
	public JsonResult delete(@RequestBody GoodsCommentIdParam goodsCommentId) {

		shop().goods.goodsComment.delete(goodsCommentId);

		return success();
	}

	/**
	 * 修改审核状态
	 *
	 * @param goodsCommentId 评论id
	 * @return 结果信息
	 */

	@PostMapping("/passflag")
	public JsonResult passflag(@RequestBody GoodsCommentIdParam goodsCommentId) {

		shop().goods.goodsComment.passflag(goodsCommentId);

		return success();
	}
    /**
     * 修改审核状态
     *
     * @param goodsCommentId 评论id
     * @return 结果信息
     */
    @PostMapping("/refuseflag")
	public JsonResult refuseflag(@RequestBody GoodsCommentIdParam goodsCommentId) {

		shop().goods.goodsComment.refuseflag(goodsCommentId);

		return success();
	}
	
	/**
	 * 修改审核配置
	 * 
	 * @param goodsCommentConfig 配置信息
	 * @return 结果信息
	 *
	 */
	@PostMapping("/checkconfig")
	public JsonResult checkConfig(@RequestBody GoodsCommentConfigParam goodsCommentConfig) {
		
			shop().config.commentConfigService.setCheckConfig(goodsCommentConfig.getV());

		return success();
	}

    /**
     * 获取审核配置
     *
     * @return 审核配置信息
     *
     */
    @GetMapping("/getconfig")
    public JsonResult getConfig() {

        Byte commentConfig = shop().config.commentConfigService.getCommentConfig();

        return success(commentConfig);
    }

	/**
	 * 修改开关配置
	 * 
	 * @param goodsCommentConfig 配置value
	 * @return 结果信息
	 *
	 */
	@PostMapping("/switchconfig")
	public JsonResult switchConfig(@RequestBody GoodsCommentConfigParam goodsCommentConfig) {
		
			shop().config.commentConfigService.setSwitchConfig(goodsCommentConfig.getV());

		return success();
	}

    /**
     * 获取开关配置
     *
     * @return 开关配置信息
     *
     */
    @GetMapping("/getswitch")
    public JsonResult getSwitch() {

        Byte switchConfig = shop().config.commentConfigService.getSwitchConfig();

        return success(switchConfig);
    }

	/**
	 * 添加评论分页查询
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/addlist")
	public JsonResult getAddPageList(@RequestBody GoodsCommentPageListParam param) {

		PageResult<GoodsCommentAddListVo> pageResult = shop().goods.goodsComment.getAddList(param);

		return success(pageResult);
	}
	
	
	/**
	 * 手动添加评论
	 *
	 * @param goodsCommentAddComm
	 * @return
	 */
	@PostMapping("/addcomm")
	public JsonResult addComment(@RequestBody GoodsCommentAddCommParam goodsCommentAddComm) {
        //没有添加权限
        if ((-1)==shop().goods.goodsComment.addComment(goodsCommentAddComm)){
            return fail(JsonResultMessage.GOODS_COMMENT_NO_PERMISSION);
        }
		return success();
	}

    /**
     * 评论置顶
     * @param param 评价记录id
     * @return
     */
	@PostMapping("/setTop")
    public JsonResult setTop(@RequestBody GoodsCommentIdParam param){
	    shop().goods.goodsComment.setTop(param);
	    return success();
    }

    /**
     * 取消评论置顶
     * @param param 评价记录id
     * @return
     */
    @PostMapping("/cancelTop")
    public JsonResult cancelTop(@RequestBody GoodsCommentIdParam param){
        shop().goods.goodsComment.cancelTop(param);
        return success();
    }

    /**
     * 设置买家秀
     * @param param 评价记录id
     * @return
     */
    @PostMapping("/setShow")
    public JsonResult setShow(@RequestBody GoodsCommentIdParam param){
        shop().goods.goodsComment.setShow(param);
        return success();
    }

    /**
     * 取消买家秀
     * @param param 评价记录id
     * @return
     */
    @PostMapping("/cancelShow")
    public JsonResult cancelShow(@RequestBody GoodsCommentIdParam param){
        shop().goods.goodsComment.cancelShow(param);
        return success();
    }
}
