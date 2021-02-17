package com.meidianyi.shop.controller.admin;

import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.NO_USE_SCORE_STATUS;
import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.USED_SCORE_STATUS;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.member.score.ScorePageListParam;
import com.meidianyi.shop.service.pojo.shop.member.score.ScorePageListVo;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreSetParam;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreSignParam;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreSignVo;
/**
* @author 黄壮壮
* @Date: 2019年8月13日
* @Description: 积分管理
*/
@RestController
public class AdminMemberScoreController extends AdminBaseController {
	/**
	 * 获取积分明细
	 */
	@PostMapping("/api/admin/member/score/list")
	public JsonResult getScoreDetails(@RequestBody @Valid ScorePageListParam param) {
		logger().info("正在会员用户积分明细");
		PageResult<ScorePageListVo> pageList = shop().member.score.getPageListOfScoreDetails(param,getLang());
		return success(pageList);
	}
	
	
	/**
	 * 更新积分
	 * @param param ScoreParam积分入参-参数
	 * @return JsonResult
	 */
	@PostMapping("/api/admin/member/score/update")
	public JsonResult updateMemberScore(@RequestBody @Valid ScoreSetParam param) {
		/** -判断修改后的值不能小于零 */
		Integer score = param.getScore();
		/** -最小值 */
		Integer scoreDis = param.getScoreDis();
		if((score+scoreDis)<0) {
			return fail(JsonResultMessage.MSG_MEMBER_SCORE_NOT_ENOUGH);
		}
		/** -获取操作员id */
		Integer subAccountId = this.adminAuth.user().getSubAccountId();
		
		if(param.getUserId()!=null) {
			Integer[] arrayUserId = param.getUserId();
			int userNumber = arrayUserId.length;
			
			/** -交易明细类型 */
			Byte tradeType = 4;
			/** -资金流向 */
			Byte tradeFlow = 1;
			/** -处理积分变动信息 */
			
			/** -积分大于零为充值,反之为消耗  */
			if(score>0) {
				param.setScoreStatus(NO_USE_SCORE_STATUS);
			}else {
				param.setScoreStatus(USED_SCORE_STATUS);
			}

			for(int i=0;i<userNumber;i++) {
				Integer userId = arrayUserId[i];
					/** -处理积分变动产生的异常 */
					try {
						
						ScoreParam scoreParam = new ScoreParam();
						BeanUtils.copyProperties(param, scoreParam);
						scoreParam.setUserId(userId);
						shop().member.score.updateMemberScore(scoreParam,subAccountId,tradeType,tradeFlow);
					} catch (MpException e) {
						logger().info("积分更新失败");
						return fail(e.getErrorCode().getMessage());
					}
			}
		}else {
			return fail(JsonResultMessage.MSG_MEMBER_NOT_EXIST);
		}
		return success(param.toString());
	}
	
	/**
	 * 查看签到会员
	 * @return
	 */
	@PostMapping("/api/admin/user/manage/score/sign")
	public JsonResult testScoreMethod(@RequestBody ScoreSignParam param) {
		logger().info("获取用户签到积分");
		PageResult<ScoreSignVo> res = shop().member.score.userSign(param);
		return success(res);
	}
	
}
