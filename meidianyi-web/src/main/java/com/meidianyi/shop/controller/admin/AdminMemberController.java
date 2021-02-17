package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.member.CommonMemberPageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.member.CommonMemberPageListQueryVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberDetailsVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberIndustryEnum;
import com.meidianyi.shop.service.pojo.shop.member.MemberInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberPageListParam;
import com.meidianyi.shop.service.pojo.shop.member.MemberParam;
import com.meidianyi.shop.service.pojo.shop.member.MememberLoginStatusParam;
import com.meidianyi.shop.service.pojo.shop.member.account.AddMemberCardParam;
import com.meidianyi.shop.service.pojo.shop.member.account.MemberCardVo;
import com.meidianyi.shop.service.pojo.shop.member.card.AvailableMemberCardVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardParam;
import com.meidianyi.shop.service.pojo.shop.member.card.UserCardDetailParam;
import com.meidianyi.shop.service.pojo.shop.member.card.UserCardDetailVo;
import com.meidianyi.shop.service.pojo.shop.member.data.IndustryVo;
import com.meidianyi.shop.service.pojo.shop.member.report.MemberGoodsBrowseReportParam;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagVo;
import com.meidianyi.shop.service.pojo.shop.member.tag.UserTagParam;
import com.meidianyi.shop.service.pojo.shop.user.detail.UserAssociatedDoctorParam;
import com.meidianyi.shop.service.shop.user.user.UserService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
/**
 * 会员管理
 * @author 黄壮壮
 * 2019-07-08 09:30
 */
@RestController
@RequestMapping(value="/api/admin/member")
public class AdminMemberController extends AdminBaseController{

    @Autowired
    private UserService userService;

	/**
	 * 返回所有行业信息
	 */
	@PostMapping("/industry/get")
	public JsonResult getIndustryList() {
		List<IndustryVo> allIndustryInfo = MemberIndustryEnum.getAllIndustryInfo();
		allIndustryInfo.stream().forEach(item->this.i18nSuccess(item));
		return success(allIndustryInfo);
	}

	/**
	 * 通用会员列表弹窗分页查询
	 * @return
	 */
	@PostMapping("/common/list")
	public JsonResult getCommonPageList(@RequestBody CommonMemberPageListQueryParam param) {
		PageResult<CommonMemberPageListQueryVo> pageResult = this.shop().member.getCommonPageList(param);
		return this.success(pageResult);
	}

	/**
	 * 会员列表分页查询
	 * @param param
	 * @return
	 */
	@PostMapping("/list")
	public JsonResult getPageList(@RequestBody MemberPageListParam param) {
		/** 获取语言，用于国际化 */
		PageResult<MemberInfoVo> pageResult = this.shop().member.getPageList(param,getLang());
		return this.success(pageResult);
	}

	/**
	 * 会员列表导出
	 * @param param
	 * @throws IOException
	 */
	@PostMapping("/list/export")
	public void exportUser(@RequestBody MemberPageListParam param,HttpServletResponse response) throws IOException {
		logger().info("正在进行会员导出");
		Workbook workBook = this.shop().member.exportUser(param,getLang());
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		// TODO file name
		String fileName = "filename";
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
		workBook.write(response.getOutputStream());
	}

	/**
	 * 	用户列表导出新接口
	 */
	@PostMapping("/list/export/new")
	public void exportUserNew(@RequestBody MemberPageListParam param,HttpServletResponse response) {
		logger().info("会员导出");
		Workbook workbook = shop().member.userExpSvc.userExport(param,getLang());
		String fileName = Util.translateMessage(getLang(), JsonResultMessage.USER_EXPORT, "excel","messages");
		String dateFormat = DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE);
		export2Excel(workbook, fileName + dateFormat, response);
		logger().info("会员导出完毕");
	}

	/**
	 * 会员卡-弹窗
	 */
	@PostMapping("/card/all/list")
	public JsonResult getAllCardList(@RequestBody CardParam param) {
		logger().info("获取系统中的所有会员卡");
		MemberCardVo vo  = shop().member.card.getAllCardList(param);
		return success(vo);
	}

	/**
	 * 会员-添加会员卡
	 */
	@PostMapping("/card/all/add")
	public JsonResult addCardForMember(@RequestBody @Valid AddMemberCardParam param) {
		logger().info("为会员发放会员卡");
		logger().info(param.toString());
		shop().member.card.addCardForMember(param);
		return success();
	}


	/**
	 * 批量禁止登录-恢复登录
	 */
	@PostMapping("/manage/update")
	public JsonResult loginStatusControl(@RequestBody @Valid MememberLoginStatusParam param) {
		logger().info("更新用户登录状态");
		shop().member.changeLoginStatus(param);
		return success();
	}


	/**
	 * 打标签，为用户设置标签
	 */
	@PostMapping("/tag/edit")
	public JsonResult setTagForMember(@RequestBody @Valid UserTagParam param) {
		logger().info("为会员用户打标签");
		shop().member.setTagForMember(param);
		return success();
	}

	/**
	 * 查询某个用户的标签列表
	 * @param param
	 * @return
	 */
	@PostMapping("/tag/get")
	public JsonResult getTagForMember(@RequestBody MemberParam param) {
		logger().info("正在该会员的标签");
		List<TagVo> tagList = shop().member.getTagForMember(param.getUserId());
		return success(tagList);
	}


	/**
	 * 会员信息详情
	 */
	@PostMapping("/manager/center/{userId}")
	public JsonResult getMemberInfo(@PathVariable Integer userId) {
		logger().info("获取会员用户id为 " + userId + " 详情信息");

		MemberDetailsVo vo = shop().member.getMemberInfoById(userId,getLang());

		return i18nSuccess(vo);
	}

	/**
	 * 商品浏览记录
	 */
	@PostMapping("/goods/browse/report")
	public JsonResult userGoodsRecordReport(@RequestBody @Validated MemberGoodsBrowseReportParam param){
		return success(shop().member.userGoodsRecordReport(param));
	}


	/**
	 * 	更新会员信息
	 */
	@PostMapping("/info/update")
	public JsonResult updateMemberInfo(@RequestBody MemberParam param) throws MpException {
		logger().info("更新用户会员信息");
		shop().member.updateMemberInfo(param);
		return success();
	}

	/**
	 * 获取用户目前持有的所有可用会员卡
	 * @param userId
	 * @return
	 */
	@PostMapping("/card/all/get/{userId}")
	public JsonResult getAllAvailableMemberCard(@PathVariable Integer userId) {
		logger().info("正在获取用户所有的可用会员卡");
		List<AvailableMemberCardVo> allAvailableMemberCard = shop().member.getAllAvailableMemberCard(userId);
		return success(allAvailableMemberCard);
	}

    /**
     * 获取用户持有会员卡明细
     *
     * @param param
     * @return
     */
	@PostMapping("/card/detail/list")
	public JsonResult getAllUserCardDetail(@RequestBody UserCardDetailParam param) {
		logger().info("正在获取会员领取会员卡明细");
		List<UserCardDetailVo> allUserCardDetail = shop().member.getAllUserCardDetail(param);
		return success(allUserCardDetail);
	}

	/**
	 * 	获取用户导出配置信息
	 */
	@PostMapping("/export/cfg")
	public JsonResult getUserExportCfg(@RequestBody MemberPageListParam param) {
		logger().info("获取用户导出配置信息");
		return success(shop().member.userExpSvc.getExportCfg(param));
	}

    /**
     * 获取用户关联医师列表
     * @param userAssociatedDoctorParam 关联医师入参
     * @return JsonResult
     */
    @PostMapping("/query/doctor")
	public JsonResult getUserAssociatedDoctor(@Validated @RequestBody UserAssociatedDoctorParam userAssociatedDoctorParam) {
        return success(userService.getUserAssociatedDoctor(userAssociatedDoctorParam));
    }

}
