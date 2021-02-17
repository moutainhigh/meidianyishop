package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.ShopCfgRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.member.score.UserScoreVo;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.member.score.CheckSignVo;
import com.meidianyi.shop.service.pojo.shop.member.score.ScorePageListParam;
import com.meidianyi.shop.service.pojo.shop.member.score.ScorePageListVo;
import com.meidianyi.shop.service.pojo.wxapp.score.ExpireVo;
import com.meidianyi.shop.service.pojo.wxapp.score.SignInScoreParam;
import com.meidianyi.shop.service.pojo.wxapp.score.UserScoreInfoVo;
import com.meidianyi.shop.service.pojo.wxapp.score.UserScoreListVo;
import com.meidianyi.shop.service.shop.ShopApplication;

/**
 * 微信积分的
 * 
 * @author zhaojianqiang
 *
 *         2019年10月16日 下午2:38:23
 */
@RestController
public class WxAppScoreConrtoller extends  WxAppBaseController {
	
	/**
	 * 获取用户积分信息
	 * @return
	 */
	@PostMapping(value = "/api/wxapp/score/info")
	public JsonResult getUserScoreInfo() {
		logger().info("获取用户积分信息");
		Integer shopId = wxAppAuth.shopId();
		ShopApplication shopApp = saas.getShopApp(shopId);
		Integer userId = wxAppAuth.user().getUserId();
		UserScoreInfoVo vo=new UserScoreInfoVo();
		UserRecord usRecord = shopApp.user.getUserByUserId(userId);
		vo.setScoreNum(usRecord.getScore());
		CheckSignVo sCheckSignVo = shopApp.userCard.scoreService.checkSignInScore(userId);
		vo.setSignScore(sCheckSignVo);
		ShopCfgRecord scoreNumRecord = shopApp.score.getScoreNum("score_page_id");
		if(scoreNumRecord!=null) {
			if(scoreNumRecord.getV()!=null) {	
				vo.setPageId(scoreNumRecord.getV());
			}else {
				vo.setPageId("0");
			}
		}
		return success(vo);
	}
	
	
	/**
	 * 获取用户积分数据
	 * @returns
	 */
	@PostMapping(value = "/api/wxapp/score/list")
	public JsonResult getUserScoreList(@RequestBody ScorePageListParam param) {
		logger().info("获取用户积分数据");
		UserScoreListVo vo=new UserScoreListVo();
		param.setUserId(wxAppAuth.user().getUserId());
		param.setType("wxapp");
		PageResult<ScorePageListVo> list = shop().userCard.scoreService.getPageListOfScoreDetails(param,getLang());
		ExpireVo expire= shop().userCard.scoreService.getUserScoreCfg(param.getUserId());
		vo.setList(list);
		vo.setExpire(expire);
		return success(vo);
	}
	
	/**
	 * 签到
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/api/wxapp/score/signAdd")
	public JsonResult signInScore( @RequestBody SignInScoreParam param) {
		logger().info("进入签到");
		Integer userId = wxAppAuth.user().getUserId();
		CheckSignVo signData = shop().userCard.scoreService.checkSignInScore(userId);
		if(signData.getIsOpenSign()==0) {
			//商家已关闭签到
			logger().info("商家已关闭签到");
			return fail(JsonResultCode.ERR_CODE_CODE_SING);
		}
		if(signData.getSignData().getIsSignIn()==1) {
			//您今天已完成签到
			logger().info("您今天已完成签到");
			return fail(JsonResultCode.ERR_CODE_HAVE_SING);
		}
		if(!signData.getSignData().getReceiveScore().equals(String.valueOf(param.getScore()))) {
			//签到积分有误
			logger().info("签到积分有误");
			return fail(JsonResultCode.ERR_CODE_CODE_SING_ERRO);
		}
		ScoreParam param2=new ScoreParam();
		param2.setRemarkCode(RemarkTemplate.SIGN_SOME_DAY_SEND.code);
		param2.setRemarkData(signData.getSignData().getDay()+","+param.getScore());
		param2.setScore(param.getScore());
		param2.setScoreStatus((byte)0);
		param2.setDesc("sign_score");
		param2.setUserId(userId);
		try {
			shop().userCard.scoreService.updateMemberScore(param2, 0, RecordTradeEnum.TYPE_SCORE_SIGN.val(), RecordTradeEnum.TRADE_FLOW_OUT.val());
		} catch (MpException e) {
			e.printStackTrace();
		}
		//shop().userCard.scoreService.addUserScore(vo, "0", (byte) 6, (byte) 1);
		CheckSignVo checkSignInScore = shop().userCard.scoreService.checkSignInScore(userId);
		logger().info("签到完成");
		return success(checkSignInScore);
	}

}
