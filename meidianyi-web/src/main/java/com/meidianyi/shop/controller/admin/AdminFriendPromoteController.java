package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.market.friendpromote.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
  *   好友助力活动控制器
 * @author liangchen
 * @date 2019年8月7日
 */
@RestController
@RequestMapping("/api/admin/market/promote")
public class AdminFriendPromoteController extends AdminBaseController{
	/**
	  *  分页查询好友助力活动列表
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/list")
	public JsonResult getList(@RequestBody FriendPromoteListParam param) {
		PageResult<FriendPromoteListVo> listVo = shop().friendPromoteService.friendPromoteList(param);
		return success(listVo);
	}
	/**
	 * 启用或停用活动
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/switch")
	public JsonResult startOrBlock(@RequestBody FriendPromoteOptionParam param) {
		shop().friendPromoteService.startOrBlock(param);
		return success();
	}
	/**
	 * 删除单个活动
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/delete")
	public JsonResult deleteAct(@RequestBody FriendPromoteOptionParam param) {
		shop().friendPromoteService.deleteAct(param);
		return success();
	}

	/**
	 * 活动奖励领取明细
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/receive")
	public JsonResult receiveDetail(@RequestBody FriendPromoteReceiveParam param) {
		PageResult<FriendPromoteReceiveVo> pageResult =
				shop().friendPromoteService.receiveDetail(param);
		return success(pageResult);
	}

	/**
	 * 活动发起明细
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/launch")
	public JsonResult launchDetail(@RequestBody FriendPromoteLaunchParam param) {
		PageResult<FriendPromoteLaunchVo> pageResult =
				shop().friendPromoteService.launchDetail(param);
		return success(pageResult);
	}

	/**
	 * 活动参与明细
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/participate")
	public JsonResult participateDetail(@RequestBody FriendPromoteParticipateParam param) {
		PageResult<FriendPromoteParticipateVo> pageResult =
				shop().friendPromoteService.participateDetail(param);
		return success(pageResult);
	}

	/**
	 * 添加好友助力活动
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/add")
	public JsonResult addActivity(@RequestBody FriendPromoteAddParam param) {
		shop().friendPromoteService.addActivity(param);
		return success();
	}

	/**
	 * 查询单个好友助力活动
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/select")
	public JsonResult selectOne(@RequestBody FriendPromoteSelectParam param) {
		FriendPromoteSelectVo vo	= shop().friendPromoteService.selectOne(param);
		return success(vo);
	}

	/**
	 * 修改好友助力活动信息
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/update")
	public JsonResult updateActivity(@RequestBody FriendPromoteUpdateParam param) {
		shop().friendPromoteService.updateActivity(param);
		return success();
	}
    /**
     * 根据prdId查询商品信息
     *
     * @param param prdId
     * @return goodsInfo
     */
    @PostMapping("/goodsInfo")
    public JsonResult getGoodsInfoByPrdId(@RequestBody FriendPromoteUpdateParam param) {
        GoodsInfo goodsInfo = shop().friendPromoteService.getGoodsInfo(param.getId());
        return success(goodsInfo);
    }

    /**
     * 活动效果展示
     *
     * @param param 发起id
     * @return 活动效果
     */
    @PostMapping("/analysis")
    public JsonResult getEffectData(@RequestBody FriendPromoteSelectParam param) {

        ActEffectDataVo vo = shop().friendPromoteService.getEffectData(param);

        return success(vo);
    }
    /**
     * 分享
     * @param param 活动id
     * @return 二维码信息
     */
    @PostMapping("/share")
    public JsonResult share(@RequestBody @Valid FriendPromoteSelectParam param){
        return success(shop().friendPromoteService.getQrCode(param));
    }
    private static final String LANGUAGE_TYPE_EXCEL = "excel";
    /**
     * 发起明细导出表格
     * @param param
     * @return
     */
    @PostMapping("/launch/export")
    public void launchExport(@RequestBody @Valid FriendPromoteLaunchParam param, HttpServletResponse response){
        Workbook workbook = shop().friendPromoteService.launchExport(param,getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.FRIEND_PROMOTE_LAUNCH_DETAIL,LANGUAGE_TYPE_EXCEL,LANGUAGE_TYPE_EXCEL)+ DateUtils.getLocalDateTime().toString();
        export2Excel(workbook,fileName,response);
    }
    /**
     * 参与明细导出表格
     * @param param
     * @return
     */
    @PostMapping("/participate/export")
    public void joinExport(@RequestBody @Valid FriendPromoteParticipateParam param, HttpServletResponse response){
        Workbook workbook = shop().friendPromoteService.joinExport(param,getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.FRIEND_PROMOTE_JOIN_DETAIL,LANGUAGE_TYPE_EXCEL,LANGUAGE_TYPE_EXCEL)+ DateUtils.getLocalDateTime().toString();
        export2Excel(workbook,fileName,response);
    }
    /**
     * 领取明细导出表格
     * @param param
     * @return
     */
    @PostMapping("/receive/export")
    public void receiveExport(@RequestBody @Valid FriendPromoteReceiveParam param, HttpServletResponse response){
        Workbook workbook = shop().friendPromoteService.receiveExport(param,getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.FRIEND_PROMOTE_RECEIVE_DETAIL,LANGUAGE_TYPE_EXCEL,LANGUAGE_TYPE_EXCEL)+ DateUtils.getLocalDateTime().toString();
        export2Excel(workbook,fileName,response);
    }
}
