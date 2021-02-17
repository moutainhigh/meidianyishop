package com.meidianyi.shop.controller.admin;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.market.gift.UserAction;
import com.meidianyi.shop.service.pojo.shop.member.card.ActiveAuditParam;
import com.meidianyi.shop.service.pojo.shop.member.card.ActiveAuditVo;
import com.meidianyi.shop.service.pojo.shop.member.card.BaseCardVo;
import com.meidianyi.shop.service.pojo.shop.member.card.BatchGroupVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBasicVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBatchDownLoadParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBatchParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBatchVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConsumeParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConsumeVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardHolderParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardIdParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardInsertVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardParam;
import com.meidianyi.shop.service.pojo.shop.member.card.ChargeParam;
import com.meidianyi.shop.service.pojo.shop.member.card.ChargeVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CodeReceiveParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CodeReceiveVo;
import com.meidianyi.shop.service.pojo.shop.member.card.PowerCardParam;
import com.meidianyi.shop.service.pojo.shop.member.card.SearchCardParam;

/**
 *
 * @author 黄壮壮
 * @Date: 2019年7月26日
 * @Description: 会员卡管理
 */
@RestController
@RequestMapping(value = "/api/admin/member")
public class AdminMemberCardController extends AdminBaseController {	/**
	 * 会员卡 - 创建
	 */
	@PostMapping("/card/add")
	public JsonResult createMemberCard(@RequestBody CardParam card) {
		logger().info("创建会员卡");
		shop().member.card.cardCreateSvc.createMemberCard(card);
		return this.success();
	}

	/**
	 * 会员卡 - 更新
     */
	@PostMapping("/card/update")
	public JsonResult updateCard(@RequestBody CardParam param) {
		logger().info("更新会员卡");
		shop().member.card.cardCreateSvc.updateMemberCard(param);
		return success();
	}

	/**
	 *	返回相应的会员卡列表
	 */
	@PostMapping("/card/list")
	public JsonResult getCardList(@RequestBody SearchCardParam param) {
		logger().info("获取会员卡列表");
		PageResult<? extends BaseCardVo> result = shop().member.card.getCardList(param);

		return success(result);
	}

	/**
	 *	 设置会员卡使用状态或停用状态
	 */
	@PostMapping("/card/power")
	public JsonResult powerCard(@RequestBody PowerCardParam param) {
		logger().info("开始处理禁用或启动会员卡");
		shop().member.card.powerCard(param);
		return success();
	}

	/**
	 * 	删除会员卡
	 */
	@PostMapping("/card/delete")
	public JsonResult deleteCard(@RequestBody @Valid CardIdParam param) {
		logger().info("开始删除会员卡");
		shop().member.card.deleteCard(param);
		return success();
	}

	/**
	 * 获取需要更新的会员卡的详细信息
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/card/get")
	public JsonResult getCardById(@RequestBody @Valid CardIdParam param) {
		logger().info("获取会员卡的详细信息");
		BaseCardVo card = shop().member.card.cardDetailSvc.getCardDetailById(param);
		return success(card);
	}

	/**
	 * 获取所有会员卡弹窗
	 */
	@PostMapping("/card/all/get")
	public JsonResult getAllUserCard() {
		logger().info("获取所有会员卡弹窗");
		List<CardBasicVo> allUserCard = shop().member.card.getAllUserCard();
		return success(allUserCard);
	}


	/**
	 * 获取所有专属会员卡弹窗
	 */
	@PostMapping("/card/exclusive/get")
	public JsonResult getCardExclusive() {
		logger().info("获取所有专享会员卡弹窗");
		List<CardBasicVo> allUserCard = shop().member.card.getCardExclusive();
		return success(allUserCard);
	}

	/**
	 * 获取可用会员卡列表
	 */
	@PostMapping("/card/usable/list")
	public JsonResult getMemberCardList() {
		logger().info("获取可用会员卡列表");
		List<UserAction> userActions = shop().member.card.getUsableMemberCardList();
		return success(userActions);
	}

	/**
	 * 获取持卡会员
	 */
	@PostMapping("/cardholder")
	public JsonResult getAllCardHolders(@RequestBody CardHolderParam param) {
		logger().info("获取所有持卡会员");
		Map<String,Object> result = shop().member.card.getAllCardHolder(param);
		return success(result);
	}

	@PostMapping("/cardholder/import/export")
	public void getAllCardHoldersExport(@RequestBody CardHolderParam param,HttpServletResponse response) {
		logger().info("导出所有持卡会员");
		Workbook workbook = shop().member.card.getAllCardHolderExport(param,getLang());
		String fileName = Util.translateMessage(getLang(), JsonResultMessage.USER_CARD_TEMPLATE_NAME, BaseConstant.LANGUAGE_TYPE_EXCEL,"messages");
		String dateFormat = DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE);
		export2Excel(workbook, fileName + dateFormat, response);
		logger().info("结束导出所有持卡会员");
	}

	/**
	 * 获取会员卡领取详情
	 */
	@PostMapping("/code/receivelist")
	public JsonResult getReceiveList(@RequestBody CodeReceiveParam param) {
		logger().info("获取会员卡领取详情");
		PageResult<CodeReceiveVo> result = shop().member.card.getReceiveList(param);
		return success(result);
	}

	/**
	 * 获取会员卡批次
	 * @param cardId
	 * @return
	 */
	@PostMapping("/card/batch/get/{cardId}")
	public JsonResult getCardBatchList(@PathVariable Integer cardId) {
		logger().info("获取会员卡所有批次");
		List<CardBatchVo> cardBatchList = shop().member.card.getCardBatchList(cardId);
		return success(cardBatchList);
	}

	/**
	 * 废除指定的会员卡批次
	 * @param cardId
	 * @return
	 */
	@PostMapping("/card/batch/delete/{id}")
	public JsonResult deleteCardBatch(@PathVariable Integer id) {
		logger().info("删除会员卡批次");
		shop().member.card.deleteCardBatch(id);
		return success();
	}

	/**
	 * 	获取充值明细
	 * @param param
	 * @return
	 */
	@PostMapping("/card/charge/list")
	public JsonResult getChargeList(@RequestBody ChargeParam param) {
		PageResult<ChargeVo> chargeList = shop().member.card.getChargeList(param,getLang());
		return success(chargeList);
	}



	/**
	 * 获取消费明细
	 * @param param
	 * @return
	 */
	@PostMapping("/card/consume/list")
	public JsonResult getConsumeList(@RequestBody ChargeParam param) {
		PageResult<ChargeVo> chargeList = shop().member.card.getConsumeList(param,getLang());
		return success(chargeList);
	}

	/**
	 * 获取激活列表信息
	 * @param param
	 * @return
	 */
	@PostMapping("/activateAudit/list")
	public JsonResult getActivateAuditList(@RequestBody ActiveAuditParam param) {
		logger().info("获取激活列表信息");
		PageResult<ActiveAuditVo> activateAuditList = shop().member.card.cardVerifyService.getActivateAuditList(param);
		return i18nSuccess(activateAuditList);
	}

	/**
	 * 审核用户激活通过
	 * @return
	 */
	@PostMapping("/activateAudit/activate")
	public JsonResult passActivateAudit(@RequestBody ActiveAuditParam param) {
	    param.setSysId(adminAuth.user().getSysId());
		shop().member.card.cardVerifyService.passActivateAudit(param);
		return success();
	}

	/**
	 * 拒绝通过审核
	 * @param param
	 * @return
	 */
	@PostMapping("/activateAudit/reject")
	public JsonResult rejectActivateAudit(@RequestBody ActiveAuditParam param) {
        param.setSysId(adminAuth.user().getSysId());
		shop().member.card.cardVerifyService.rejectActivateAudit(param);
		return success();
	}

	/**
	 * 查询会员卡订单
	 */
	@PostMapping("/card/order/list")
	public JsonResult getCardConsumeOrderList(@RequestBody CardConsumeParam param) {
		PageResult<CardConsumeVo> results = shop().member.card.getCardConsumeOrderList(param);
		return success(results);
	}

	/**
	 * 添加领取批次
	 */
	@PostMapping("/card/generatecode")
	public JsonResult generateCardCode(@RequestBody CardBatchParam param) {
		logger().info("添加领取码");
		CardBatchVo vo = shop().member.card.generateCardCode(param);
		return success(vo);
	}
	/**
	 * 获取领取批次
	 */
	@PostMapping("/card/code/{batchId}")
	public JsonResult getBatchCfg(@PathVariable Integer batchId) {
		logger().info("获取领取批次");
		return success(shop().member.card.getBatchCfg(batchId));
	}


	/**
	 * 获取等级卡所有等级
	 */
	@PostMapping("/card/all/grade")
	public JsonResult getAvailableGradeCard() {
		List<String> res = shop().member.card.getAllNoDeleteCardGrade();
		return success(res);
	}

	/**
	 * 获取导入领取码模板
	 *
	 * @param response
	 */
	@GetMapping(value = "/card/code/getTemplate")
	public void getTemplate(HttpServletResponse response) {
		logger().info("开始获取导入领取码模板");
		Workbook workbook = shop().member.card.getCardNoTemplate(getLang());
		String fileName = Util.translateMessage(getLang(), JsonResultMessage.CARD_NO_TEMPLATE_NAME, BaseConstant.LANGUAGE_TYPE_EXCEL,"messages");
		export2Excel(workbook, fileName, response);
		logger().info("结束获取导入领取码模板");
	}

	/**
	 * 导入领取码模板
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/card/code/import/insert")
	public JsonResult importUser(CardBatchParam param) {
		logger().info("导入领取码模板");
		MultipartFile file = param.getFile();
		logger().info("上传文件:" + file.getName());
		ExcelTypeEnum checkFile = shop().member.userImportService.checkFile(file);
		if (checkFile == null) {
			return fail(JsonResultCode.CODE_EXCEL_ERRO);
		}
		CardInsertVo vo = shop().member.card.insertCardNo(getLang(), param);
		JsonResultCode code = vo.getCode();
		if(code.equals(JsonResultCode.CODE_SUCCESS)) {
			return success(vo.getBatchId());
		}
		return fail(code);
	}

	/**
	 * 获得生成/导入记录
	 * @param batchId
	 * @return
	 */
	@GetMapping(value = "/card/code/importlist/{batchId}")
	public JsonResult getList(@PathVariable Integer batchId) {
		BatchGroupVo batchGroupList = shop().member.card.getBatchGroupList(batchId);
		return success(batchGroupList);
	}

	/**
	 * 下载失败数据
	 *
	 * @param param
	 * @param response
	 */
	@PostMapping(value = "/card/code/import/fail")
	public void getErrorExcel(@RequestBody CardBatchDownLoadParam param, HttpServletResponse response) {
		logger().info("开始下载领取码失败数据");
		Workbook workbook = shop().member.card.getExcel(param.getBatchId(), getLang(),false,param.getIsPwd());
		String fileName = Util.translateMessage(getLang(), JsonResultMessage.CARD_NO_IMPORT_NAME, BaseConstant.LANGUAGE_TYPE_EXCEL,
				"messages");
		String dateFormat = DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE);
		export2Excel(workbook, fileName + dateFormat, response);
		logger().info("结束下载领取码失败数据");
	}

	/**
	 * 下载成功数据
	 *
	 * @param param
	 * @param response
	 */
	@PostMapping(value = "/card/code/import/success")
	public void getSuccessExcel(@RequestBody CardBatchDownLoadParam param, HttpServletResponse response) {
		logger().info("开始下载领取码成功数据");
		Workbook workbook = shop().member.card.getExcel(param.getBatchId(), getLang(),true,param.getIsPwd());
		String fileName = Util.translateMessage(getLang(), JsonResultMessage.CARD_NO_IMPORT_NAME, BaseConstant.LANGUAGE_TYPE_EXCEL,
				"messages");
		String dateFormat = DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE);
		export2Excel(workbook, fileName + dateFormat, response);
		logger().info("结束下载领取码成功数据");
	}

	/**
	 * 获取导入领取码+密码的模板
	 *
	 * @param response
	 */
	@GetMapping(value = "/card/codePwd/getTemplate")
	public void getcodePwdTemplate(HttpServletResponse response) {
		logger().info("开始获取导入领取码模板");
		Workbook workbook = shop().member.card.getCardNoPwdTemplate(getLang());
		String fileName = Util.translateMessage(getLang(), JsonResultMessage.CARD_NO_TEMPLATE_NAME, BaseConstant.LANGUAGE_TYPE_EXCEL,"messages");
		export2Excel(workbook, fileName, response);
		logger().info("结束获取导入领取码模板");
	}

	/**
	 * 	获取待审核的卡列表
	 */
	@PostMapping(value="/card/examine/list")
	public JsonResult getCardExamineList() {
		logger().info("获取待审核的卡列表");
		return success(shop().mallOverview.cardVerifyService.getCardExamineList());
	}

	/**
	 * 	获取会员卡分享码
	 */
	@PostMapping(value="/card/getqrcode/{cardId}")
	public JsonResult getCardQrcode(@PathVariable Integer cardId) {
		logger().info("获取卡ID: "+cardId+"的分享码");
		return success(shop().member.card.getShareCode(cardId));
	}

	@PostMapping(value="/cards/list/get")
	public JsonResult getCardByIds(@RequestBody List<Integer> param) {
		logger().info("根据id数组获取会员卡");
		return success(shop().member.card.getCardById(param));
	}

	/**
	 * 	获取有效的等级卡
	 */
	@PostMapping(value="/valid/grade/card/list")
	public JsonResult getAllValidGradeCardList() {
		logger().info("获取有效的等级卡");
		return success(shop().member.card.gradeCardService.getAllValidGradeCardList());
	}


	/**
	 * 下载失败数据
	 *
	 * @param param
	 * @param response
	 */
	@PostMapping(value = "/code/receivelist/export")
	public void getErrorExcel(@RequestBody CodeReceiveParam param, HttpServletResponse response) {
		logger().info("开始下载领取详情");
		Workbook workbook = shop().member.card.getReceiveExcel(param, getLang());
		String fileName = Util.translateMessage(getLang(), JsonResultMessage.USER_CARD_RECEIVE_NAME, BaseConstant.LANGUAGE_TYPE_EXCEL,
				"messages");
		String dateFormat = DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE);
		export2Excel(workbook, fileName + dateFormat, response);
		logger().info("结束下载领取详情");
	}

	/**
	 * 会员卡审核导出接口
	 */
	@PostMapping("/activateAudit/list/export")
	public void getActivateAuditList(@RequestBody ActiveAuditParam param,HttpServletResponse response) {
		logger().info("会员卡审核导出");
		String cardName = shop().member.card.getCardNameById(param.getCardId());
		Workbook workbook = shop().member.card.cardVerifyService.exportToExcel(param,getLang());
		String fileName = Util.translateMessage(getLang(), JsonResultMessage.CARD_EXAMINE_FILE_NAME, BaseConstant.LANGUAGE_TYPE_EXCEL,
				"messages");
		String dateFormat = DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE);
		export2Excel(workbook, cardName+fileName + dateFormat, response);
	}

}
