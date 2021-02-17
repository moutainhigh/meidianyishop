package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.GroupDrawRecord;
import com.meidianyi.shop.db.shop.tables.records.ShopCfgRecord;
import com.meidianyi.shop.service.pojo.shop.market.friendpromote.PromoteActCopywriting;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.GroupActivityCopyWriting;
import com.meidianyi.shop.service.pojo.shop.market.integration.GroupInteMaVo;
import com.meidianyi.shop.service.pojo.shop.member.score.CheckSignVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
/**
 * 
 * @author zhaojianqiang
 *
 * 2019年10月17日 上午11:27:00
 */
@RestController
public class HelpController extends HelpBaseController {
	
	
	private Logger log=LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 服务条款
	 * @param shopId
	 * @param userId
	 * @return
	 */
	@GetMapping("/api/wxapp/score/scoreDocument")
	public JsonResult scoreDocument(@RequestParam Integer shopId, @RequestParam Integer userId) {
		log.info("查询积分说明");
		checkId();
		ShopCfgRecord scoreNum = saas.getShopApp(shopId).userCard.scoreService.score.getScoreNum("score_document");
		if(scoreNum!=null) {
			log.info("设置查询积分说明");
			String v = scoreNum.getV();
			Map parseJson = Util.parseJson(v, Map.class);
			Object object = parseJson.get("document");
			return success(object);
		}
		log.info("未设置查询积分说明");
		return fail();
	}

	
	/**
	 * 签到帮助页
	 * @param shopId
	 * @param userId
	 * @param sign_rule
	 * @return
	 */
	@GetMapping("/api/wxapp/sign/help")
	public JsonResult getSignHelp(@RequestParam Integer shopId, @RequestParam Integer userId) {
		log.info("进入签到帮助页");
		checkId();
		CheckSignVo sCheckSignVo = saas.getShopApp(shopId).userCard.scoreService.checkSignInScore(userId);
		return success(sCheckSignVo);
	}
	
	/**
	 * 组团瓜分积分活动说明
	 * @param shopId
	 * @param pid
	 * @return
	 */
	@GetMapping("/api/wxapp/pinintegration/help")
	public JsonResult getGroupInfo(@RequestParam Integer shopId,@RequestParam Integer pid) {
		log.info("进入组团瓜分积分活动说明");
		checkId();
		GroupInteMaVo vo = saas.getShopApp(shopId).groupIntegration.getActivityCopywriting(pid);
		if(vo==null) {
			return fail();
		}else {
			return success(vo);			
		}
		
	}

    /**
     * 查询服务条款配置
     *
     * @return 服务条款配置内容
     */
    @GetMapping("/api/wxapp/order/termsofservice")
    public JsonResult getTermsOfService(@RequestParam Integer shopId) {
        try {
            return success(saas.getShopApp(shopId).trade.getTermsOfService());
        } catch (IOException e) {
            log.error("服务条款配置内容错误", e);
            return fail();
        }
    }
    
    /**
     * 拼团抽奖服务条款
     * @param shopId
     * @param group_draw_id
     * @return
     */
    @GetMapping("/api/wxapp/groupDraw/help")
    public JsonResult getTempGroupDraw(@RequestParam Integer shopId,@RequestParam Integer groupDrawId) {
		GroupDrawRecord record = saas.getShopApp(shopId).groupDraw.getById(groupDrawId);
		if (record == null) {
			// 活动不存在或没有可参与的活动商品
			return fail(JsonResultCode.GROUP_DRAW_FAIL);
		}
		String activityCopywriting = record.getActivityCopywriting();
		GroupActivityCopyWriting json=null;
		if(!StringUtils.isEmpty(activityCopywriting)){
			json = Util.parseJson(activityCopywriting, GroupActivityCopyWriting.class);		
		}
		return success(json);
    }
	/**
	 * 获取拼团说明
	 * @param id id
	 * @return 拼团说明
	 */
	@GetMapping("/api/wxapp/groupbuy/copywriting")
	public JsonResult getActivityCopywriting(@RequestParam Integer shopId,@RequestParam Integer id){
		String activityCopywriting = saas.getShopApp(shopId).groupBuy.getActivityCopywriting(id);
		return success(activityCopywriting);
	}
    /**
     * 获取好友助力说明
     *
     * @param actCode 唯一活动码
     * @return 活动说明
     */
    @GetMapping("/api/wxapp/promote/actCopywriting")
    public JsonResult promoteActCopywriting(@RequestParam Integer shopId,@RequestParam String actCode) {
        PromoteActCopywriting vo = saas.getShopApp(shopId).friendPromoteService.getActCopywriting(actCode);
        return success(vo);
    }

    /**
     * 砍价规则
     *
     * @param shopId
     * @return
     */
    @GetMapping("/api/wxapp/bargain/rule")
    public JsonResult getBargainRule(@RequestParam Integer shopId, @RequestParam Integer id) {
        return success(saas.getShopApp(shopId).bargain.getBargainRule(id));
    }
}
