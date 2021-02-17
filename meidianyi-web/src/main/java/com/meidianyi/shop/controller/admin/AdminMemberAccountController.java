package com.meidianyi.shop.controller.admin;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountPageListParam;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountPageListVo;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountParam;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;

/**
* @author 黄壮壮
* @Date: 2019年8月13日
* @Description: 会员的余额管理控制器
*/
@RestController
public class AdminMemberAccountController extends AdminBaseController {
	
	/**
	 *  获取会员用户的余额明细
	 */
	@PostMapping("/api/admin/member/account/list")
	public JsonResult getAccountDetails(@RequestBody @Valid AccountPageListParam param) {
		logger().info("正在查询会员用户的余额明细");		
		PageResult<AccountPageListVo> pageList = shop().member.account.getPageListOfAccountDetails(param,getLang());
		return success(pageList);
	}
	
	
	/**
	 * 会员用户余额变动
	 * @param param
	 * @return
	 */
	@PostMapping("/api/admin/member/account/update")
	public JsonResult updateMemberAccount(@RequestBody AccountParam param) {
		try {
			shop().member.account.updateUserAccount(param,TradeOptParam.builder().build());
		} catch (MpException e) {
			return this.fail(e.getErrorCode());
		}
		return success();
	}
	
}
