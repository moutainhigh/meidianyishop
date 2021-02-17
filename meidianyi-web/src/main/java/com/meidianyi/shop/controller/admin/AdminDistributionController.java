package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.distribution.AddDistributorToGroupParam;
import com.meidianyi.shop.service.pojo.shop.distribution.AddDistributorToLevelParam;
import com.meidianyi.shop.service.pojo.shop.distribution.BrokerageListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.BrokerageListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionApplyOptParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionDocumentParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionStrategyParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionStrategyVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorCheckListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorCheckListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorGroupListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorGroupListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorInvitedListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorInvitedListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorLevelCfgVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorLevelListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorLevelParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorLevelVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorSetGroupParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorWithdrawDetailVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorWithdrawListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorWithdrawListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.GroupCanSelectParam;
import com.meidianyi.shop.service.pojo.shop.distribution.PromotionLanguageAddParam;
import com.meidianyi.shop.service.pojo.shop.distribution.PromotionLanguageListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.PromotionLanguageListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.RebateGoodsDetailParam;
import com.meidianyi.shop.service.pojo.shop.distribution.RebateGoodsDetailVo;
import com.meidianyi.shop.service.pojo.shop.distribution.RebateGoodsParam;
import com.meidianyi.shop.service.pojo.shop.distribution.RebateGoodsVo;
import com.meidianyi.shop.service.pojo.shop.distribution.SetInviteCodeParam;
import com.meidianyi.shop.service.pojo.shop.distribution.ShowDistributionGroupParam;
import com.meidianyi.shop.service.pojo.shop.distribution.UserRemarkListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.withdraw.WithdrawRemarkParam;
import com.meidianyi.shop.service.pojo.shop.member.MemberEducationEnum;
import com.meidianyi.shop.service.pojo.shop.member.MemberIndustryEnum;
import com.meidianyi.shop.service.pojo.shop.member.MemberMarriageEnum;
import com.meidianyi.shop.service.shop.ShopApplication;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.meidianyi.shop.common.foundation.excel.AbstractExcelDisposer.LANGUAGE_TYPE_EXCEL;

/**
 * 分销模块
 * @author 常乐
 * 2019年7月17日
 */
@RestController
@RequestMapping("/api")
public class AdminDistributionController extends AdminBaseController{
/**
	@Override
    protected ShopApplication shop() {
        return saas.getShopApp(471752);
    }
*/

	//分销配置
	/**
	 * 获取分销配置
	 * @return
	 */
	@GetMapping("/admin/distribution/get")
	public JsonResult distributionCfg() {
		DistributionParam result = shop().config.distributionCfg.getDistributionCfg();
		return this.success(result);
	}

	/**
	 *设置分销配置
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/set")
	public JsonResult setDistributionCfg(@RequestBody DistributionParam param) {
		int result = shop().config.distributionCfg.setDistributionCfg(param);
		//自动检测补全邀请码
		if(param.getInvitationCode() != null && param.getInvitationCode() == 1){
            shop().distributorList.autoSetInviteCode();
        }
		return this.success(result);
	}

    /**
     * 子商户类型
     * @return
     */
    @PostMapping("/admin/distribution/mpPayType")
    public JsonResult serviceProviderType(){
        Byte mpPay = shop().config.distributionCfg.getMpPay();
        return this.success(mpPay);
    }

    /**
     * 获取推广文案二维码
     * @return
     * @throws Exception
     */
    @GetMapping("/admin/distribution/share/code")
    public JsonResult getDistributionShareCode() throws Exception {
        return success(shop().config.distributionCfg.getShareQrCode());
    }

	/**
	 * 获取分销推广文案
	 * @return
	 */
	@GetMapping("/admin/distribution/document/get")
	public JsonResult getDistributionDocument(){
		DistributionDocumentParam distributionDocument = shop().config.distributionCfg.getDistributionDocument();
		return this.success(distributionDocument);
	}

	/**
	 * 设置分销推广文案
	 * @return
	 */
	@PostMapping("/admin/distribution/document/set")
	public JsonResult setDistributionDocument(@RequestBody DistributionDocumentParam param){
		int result = shop().config.distributionCfg.setDistributionDocument(param);
		return this.success(result);
	}

	//返利策略配置
	/**
	 * 添加返利策略
	 * @param info
	 * @return
	 */
	@PostMapping("/admin/distribution/rebate/set")
	public JsonResult setRebateStrategy(@RequestBody DistributionStrategyParam info) {
		boolean result = shop().rebateStrategy.setRebateStrategy(info);
		if(result) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}

	/**
	 * 返利策略分页列表
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/rebate/list")
	public JsonResult rebateStrategyList(@RequestBody DistributionStrategyParam param) {
		PageResult<DistributionStrategyVo> list = shop().rebateStrategy.getStrategyList(param);
		return this.success(list);
	}

	/**
	 * 编辑返利策略
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/rebate/edit")
	public JsonResult oneRebateStrategyInfo(Integer id) {
		List<DistributionStrategyParam> result = shop().rebateStrategy.getOneInfo(id);
		return this.success(result);
	}

	/**
	 * 返利策略编辑保存
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/rebate/save")
	public JsonResult saveRebateStrategy(@RequestBody DistributionStrategyParam param) {
		boolean result = shop().rebateStrategy.saveRebateStrategy(param);
		return this.success(result);
	}

	/**
	 * 返利策略停用
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/rebate/pause")
	public JsonResult pauseRebateStrategy(Integer id) {
		boolean result = shop().rebateStrategy.pauseRebate(id);
		if(result) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}

	/**
	 * 返利策略启用
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/rebate/open")
	public JsonResult openRebateStrategy(Integer id) {
		boolean result = shop().rebateStrategy.openRebate(id);
		if(result) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}

	/**
	 * 返利策略删除
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/rebate/delete")
	public JsonResult deleteRebateStrategy(Integer id) {
		boolean result = shop().rebateStrategy.deleteRebate(id);
		if(result) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}

	//分销员分组
	/**
	 * 分销员分组列表
	 * @param param
	 */
	@PostMapping("/admin/distribution/group/list")
	public JsonResult distributorGroupList(@RequestBody DistributorGroupListParam param) {
		PageResult<DistributorGroupListVo> groupList = shop().distributorGroup.getDistributorGroupList(param);
		return this.success(groupList);
	}

    /**
     * 设置分销分组是否在小程序端展示
     * @param param
     * @return
     */
    @PostMapping("/admin/distribution/group/show")
	public JsonResult showDistributionGroup(@RequestBody ShowDistributionGroupParam param){
        int res = shop().distributorGroup.showDistributionGroup(param);
        return this.success(res);
    }
    @PostMapping("/admin/distribution/group/get")
    public JsonResult getGroupCfg(){
        int groupCfg = shop().distributorGroup.getGroupCfg();
        return this.success(groupCfg);
    }

	/**
	 * 添加分销员分组
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/group/add")
	public JsonResult distributorGroupAdd(@RequestBody DistributorGroupListParam param) {
		//判断是否存在该分组
		boolean isExists = shop().distributorGroup.isExistGroup(param);
		if(isExists) {
			return this.fail(JsonResultCode.DISTRIBUTOR_GROUP_NAME_EXIST);
		}
		boolean result = shop().distributorGroup.adddistributorGroup(param);
		if(result) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}


	/**
	 * 设置默认分组
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/group/default")
	public JsonResult setDefaultGroup(Integer id) {
		boolean result = shop().distributorGroup.setDefault(id);
		if(result) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}

	/**
	 * 取消默认分组
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/group/cancle")
	public JsonResult cancleDefaultGroup(Integer id) {
		boolean result = shop().distributorGroup.cancleDefault(id);
		return this.success(result);
	}

	/**
	 * 编辑分组，获取单条信息
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/group/edit")
	public JsonResult distributorGroupEdit(Integer id) {
		DistributorGroupListVo info = shop().distributorGroup.getOneInfo(id);
		return this.success(info);
	}
	/**
	 * 编辑保存分销分组
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/group/edit")
	public JsonResult distributorGroupSave(@RequestBody DistributorGroupListParam param) {
		int res = shop().distributorGroup.groupSave(param);
		return this.success(res);
	}

	/**
	 * 删除分组
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/group/del")
	public JsonResult distributorGroupDel(Integer id) {
		boolean result = shop().distributorGroup.delGroup(id);
		if(result) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}

	/**
	 * 分销员分组添加分销员
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/distributor/add")
	public JsonResult addDistributorToGroup(@RequestBody AddDistributorToGroupParam param) {
		boolean res = shop().distributorGroup.addDistributorGroup(param);
		return this.success(res);
	}

    /**
     * 分销分组是否支持用户可选 1；支持 0：不支持
     * @param param
     * @return
     */
    @PostMapping("/admin/distribution/distributor/group/select")
	public JsonResult userCanSelect(@RequestBody GroupCanSelectParam param){
        int res = shop().distributorGroup.userCanSelect(param);
        return this.success(res);
    }

	//分销员等级配置
	/**
	 * 分销员等级配置列表
	 * @return
	 */
	@GetMapping("/admin/distribution/level/config")
	public JsonResult distributorLevelConfig() {
		DistributorLevelCfgVo levelCfg = shop().distributorLevel.levelConfig();
		return this.success(levelCfg);
	}

	/**
	 * 分销员等级配置设置
	 * @param levelData
	 * @return
	 */
	@PostMapping("/admin/distribution/level/save")
	public JsonResult saveDistributorLevel(@RequestBody DistributorLevelParam[] levelData) {
        shop().distributorLevel.saveDistributorLevel(levelData);
        return this.success();
	}

	/**
	 * 获取分销员等级配置信息
	 * @return
	 */
	@PostMapping("/admin/distribution/level/list")
	public JsonResult getDistributorLevelList(){
		DistributorLevelListVo res = shop().distributorLevel.distributorLevelList();
		return this.success(res);
	}

	/**
	 * 分销员等级配置停用
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/level/pause")
	public JsonResult pauseDistributorLevel(Integer id){
		int res = shop().distributorLevel.pauseDistributorLevel(id);
		return this.success(res);
	}

	/**
	 * 分销员等级配置启用
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/level/open")
	public JsonResult openDistributorLevel(Integer id){
		int res = shop().distributorLevel.openDistributorLevel(id);
		return this.success(res);
	}

    /**
     * 分销员等级配置，手动升级添加分销员
     * @param param
     * @return
     */
	@PostMapping("/admin/distribution/level/distributor/add")
    public JsonResult addDistributorToLevel(@RequestBody AddDistributorToLevelParam param){
        int res = shop().distributorLevel.addDistributorToLevel(param);
        return this.success(res);
    }

	/**
	 * 分销员列表
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/distrobutor/list")
	public JsonResult distributorList(@RequestBody DistributorListParam param) {
		PageResult<DistributorListVo> distributorList = shop().distributorList.getPageList(param);
		return this.success(distributorList);
	}

    /**
     * 分销员列表导出
     * @param param
     * @param response
     * @throws IOException
     */
    @PostMapping("/admin/distribution/distrobutor/list/export")
    public void exportDistributorList(@RequestBody DistributorListParam param, HttpServletResponse response) throws IOException {
        Workbook workbook = shop().distributorList.exportDistributorList(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.DISTRIBUTOR_LIST_NAME,LANGUAGE_TYPE_EXCEL) + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook,fileName,response);
    }

    /**
     * 分销员编辑设置分组（支持批量设置）
     * @param param
     * @return
     */
	@PostMapping("/admin/distribution/distrobutor/group/set")
	public JsonResult setGroup(@RequestBody DistributorSetGroupParam param){
        int res = shop().distributorList.setGroup(param);
        return this.success(res);
    }

    /**
     * 添加会员备注
     * @param param
     * @return
     */
    @PostMapping("/admin/distribution/distrobutor/userRemark/add")
	public JsonResult addUserRemark(@RequestBody UserRemarkListVo param){
        int res = shop().distributorList.addUserRemark(param);
        return this.success(res);
    }

    /**
     * 会员备注列表
     * @param param
     * @return
     */
	@PostMapping("/admin/distribution/distrobutor/userRemark/list")
	public JsonResult userRemarkList(@RequestBody UserRemarkListVo param){
        List<UserRemarkListVo> userRemarkListVos = shop().distributorList.userRemarkList(param);
        return this.success(userRemarkListVos);
    }

    /**
     * 删除会员备注
     * @param id
     * @return
     */
    @GetMapping("/admin/distribution/distrobutor/userRemark/del")
    public JsonResult delUserRemark(Integer id){
        int res = shop().distributorList.delUserRemark(id);
        return this.success(res);
    }

	/**
	 * 分销员已邀请用户列表
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/invited/list")
	public JsonResult invitedList(@RequestBody DistributorInvitedListParam param) {
		DistributorInvitedListVo invitedlist = shop().distributorList.getInvitedList(param);
		return this.success(invitedlist);
	}

    /**
     * 分销员间接邀请用户列表
     * @param param
     * @return
     */
    @PostMapping("/admin/distribution/indirectInvited/list")
	public JsonResult indirectInviteList(@RequestBody DistributorInvitedListParam param){
        DistributorInvitedListVo indirectInviteList = shop().distributorList.getIndirectInviteList(param);
        return this.success(indirectInviteList);
    }

	/**
	 * 清除分销员身份
	 * @param userId
	 * @return
	 */
	@GetMapping("/admin/distribution/distributor/del")
	public JsonResult delDistributor(Integer userId) {
		int result = shop().distributorList.delDistributor(userId);
		return this.success(result);
	}

    /**
     * 分销员设置邀请码
     * @param param
     * @return
     */
	@PostMapping("/admin/distribution/distributor/inviteCode/set")
	public JsonResult setInviteCode(@RequestBody SetInviteCodeParam param){
        int res = shop().distributorList.setInviteCode(param);
        return this.success(res);
    }

	/**
	 * 佣金统计
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distributio/brokerage/list")
	public JsonResult brokerageList(@RequestBody BrokerageListParam param) {
		PageResult<BrokerageListVo> list = shop().brokerage.getbrokerageList(param);
		return this.success(list);
	}

    /**
     * 佣金统计导出
     * @param param
     * @param response
     * @throws IOException
     */
    @PostMapping("/admin/distribution/brokerage/list/export")
    public void exportBrokeList(@RequestBody BrokerageListParam param, HttpServletResponse response) throws IOException {
        Workbook workbook = shop().brokerage.exportBrokeList(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.BROKERAGE_LIST_NAME,LANGUAGE_TYPE_EXCEL) + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook,fileName,response);
    }

	/**
	 * 分销员等级列表
	 * @return
	 */
	@GetMapping("/admin/distribution/distributor/level")
	public JsonResult distributorLevelList() {
		List<DistributorLevelVo> levelList = shop().brokerage.getLevelList();
		return this.success(levelList);
	}

	/**
	 * 分销员分组列表
	 * @return
	 */
	@GetMapping("/admin/distribution/distributor/group")
	public JsonResult distributorGroupList() {
		List<DistributorGroupListVo> groupList = shop().brokerage.getGroupList();
		return this.success(groupList);
	}

	/**
	 * 商品返利统计
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/rebate/goods/list")
	public JsonResult rebateGoodsList(@RequestBody RebateGoodsParam param) {
		PageResult<RebateGoodsVo> rebateGoodsList = shop().rebateGoods.getRebateGoods(param);
		return this.success(rebateGoodsList);
	}

    /**
     * 商品返利统计导出Excel
     * @param param
     * @param response
     * @throws IOException
     */
    @PostMapping("/admin/distribution/rebate/goods/list/export")
    public void exportRebateGoodsList(@RequestBody RebateGoodsParam param, HttpServletResponse response) throws IOException {
        Workbook workbook = shop().rebateGoods.exportRebateGoodsList(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.REBATE_GOODS_NAME,LANGUAGE_TYPE_EXCEL) + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook,fileName,response);
    }

	/**
	 * 商品返利明细
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/rebate/goods/detail")
	public JsonResult rebateGoodsDetail(@RequestBody RebateGoodsDetailParam param) {
		PageResult<RebateGoodsDetailVo> detail = shop().rebateGoods.getRebateGoodsDetail(param);
		return this.success(detail);
	}

    /**
     * 商品返利明细导出Excel
     * @param param
     * @param response
     * @throws IOException
     */
    @PostMapping("/admin/distribution/rebate/goods/detail/export")
    public void exportRebateGoodsDetail(@RequestBody RebateGoodsDetailParam param, HttpServletResponse response) throws IOException {
        Workbook workbook = shop().rebateGoods.exportRebateGoodsDetail(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.REBATE_GOODS_DETAIL_NAME, LANGUAGE_TYPE_EXCEL) + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook, fileName, response);
    }

	//分销推广语
	/**
	 * 分销推广语列表
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/promotion/list")
	public JsonResult promotionLanguageList(@RequestBody PromotionLanguageListParam param) {
		PageResult<PromotionLanguageListVo> promotionLanguageList = shop().promotionLanguage.getPromotionLanguageList(param);
		return this.success(promotionLanguageList);
	}

	/**
	 * 添加分销推广语
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/promotion/add")
	public JsonResult promotionLanguageAdd(@RequestBody PromotionLanguageAddParam param) {
		int result = shop().promotionLanguage.addPromotionLanguage(param);
		return this.success(result);
	}

	/**
	 * 获取单条分销推广语信息
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/promotion/edit")
	public JsonResult promotionLanguageEdit(Integer id) {
		PromotionLanguageListVo result = shop().promotionLanguage.getOnePromotion(id);
		return this.success(result);
	}

	/**
	 * 分销推广语编辑保存
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/promotion/save")
	public JsonResult promotionLanguageSave(@RequestBody PromotionLanguageAddParam param) {
		int result = shop().promotionLanguage.savePromotionLanguage(param);
		return this.success(result);
	}

	/**
	 * 删除分销推广语，假删除
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/promotion/delete")
	public JsonResult promotionLanguageDelete(Integer id) {
		int result = shop().promotionLanguage.delPromotionLanguage(id);
		return this.success(result);
	}

	/**
	 * 停用分销推广语
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/promotion/pause")
	public JsonResult promotionLanguagePause(Integer id) {
		int result = shop().promotionLanguage.pausePromotionLanguage(id);
		return this.success(result);
	}

	/**
	 *启用分销推广语
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/promotion/open")
	public JsonResult promotionLanguageOpen(Integer id) {
		int result = shop().promotionLanguage.openPromotionLanguage(id);
		return this.success(result);
	}

	//分销提现审核
	/**
	 * 分销提现审核列表
	 * @param param
	 * @return
	 */
	@PostMapping("/admin/distribution/withdraw/list")
	public JsonResult withdrawList(DistributorWithdrawListParam param) {
		PageResult<DistributorWithdrawListVo> withdrawList = shop().withdraw.getWithdrawList(param);
		return this.success(withdrawList);
	}

	/**
	 * 提现审核详情
	 * @param id
	 * @return
	 */
	@GetMapping("/admin/distribution/withdraw/detail")
	public JsonResult withdrawDetail(Integer id) {
		DistributorWithdrawDetailVo detail = shop().withdraw.getWithdrawDetail(id);
		return this.success(detail);
	}

    /**
     * 分销员审核列表
     * @param param
     * @return
     */
    @PostMapping("/admin/distribution/distributor/check/list")
	public JsonResult distributorCheckList(@RequestBody DistributorCheckListParam param){
        PageResult<DistributorCheckListVo> distributorCheckList = shop().distributorCheck.getDistributorCheckList(param);
        for(DistributorCheckListVo list:distributorCheckList.dataList){
            if(list.getCheckField() != null){
                //转换行业码对应的名称
                if(list.getCheckField().getIndustryInfo() != null){
                    String industryInfo = MemberIndustryEnum.getNameByCode(list.getCheckField().getIndustryInfo(),getLang());
                    list.getCheckField().setIndustryName(industryInfo);
                }
                //教育程度
                if(list.getCheckField().getEducation() != null){
                    String education = MemberEducationEnum.getNameByCode(list.getCheckField().getEducation(),getLang());
                    list.getCheckField().setEducationName(education);
                }
                //性别
                if(list.getCheckField().getSex()!=null && "f".equalsIgnoreCase(list.getCheckField().getSex())){
                    list.getCheckField().setSex("女");
                }else if(list.getCheckField().getSex()!=null && "m".equalsIgnoreCase(list.getCheckField().getSex())){
                    list.getCheckField().setSex("男");
                }
                //婚姻状况
                if(list.getCheckField().getMaritalStatus() != null){
                    String maritalInfo = MemberMarriageEnum.getNameByCode(list.getCheckField().getMaritalStatus(),getLang());
                    list.getCheckField().setMaritalName(maritalInfo);
                }
                //分销分组名称
                if(list.getCheckField().getRebateGroup() != null){
                    DistributorGroupListVo oneInfo = shop().distributorGroup.getOneInfo(list.getCheckField().getRebateGroup());
                    String groupName;
                    if(oneInfo != null){
                        groupName = oneInfo.getGroupName();
                    }else{
                        groupName = null;
                    }
                    list.getCheckField().setRebateGroupName(groupName);
                }
            }

        }
        return this.success(distributorCheckList);
    }

    /**
     * 分销员审核通过
     * @param param
     * @return
     */
    @PostMapping("/admin/distribution/distributor/check/pass")
    public JsonResult applyPass(@RequestBody DistributionApplyOptParam param){
        boolean res = shop().distributorCheck.applyPass(param);
        return this.success(res);
    }

    /**
     * 分销员审核拒绝
     * @param param
     * @return
     */
    @PostMapping("/admin/distribution/distributor/check/refuse")
    public JsonResult applyRefuse(@RequestBody DistributionApplyOptParam param){
        boolean res = shop().distributorCheck.applyRefuse(param);
        return this.success(res);
    }

    /**
     * 提现详情添加备注
     * @param param
     * @return
     */
    @PostMapping("/admin/distribution/withdraw/withdrawRemark/add")
    public JsonResult addWithdrawRemark(@RequestBody WithdrawRemarkParam param){
        int res = shop().withdrawService.addWithdrawRemark(param);
        return this.success(res);
    }
}
