package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.decoration.*;
import com.meidianyi.shop.service.pojo.shop.goods.brand.GoodsBrandClassifyVo;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelSelectListVo;
import com.meidianyi.shop.service.pojo.shop.sort.SortVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreListQueryParam;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 装修通用弹窗-选择链接
 * @author 常乐
 * 2019年7月9日
 */
@RestController
@RequestMapping("/api")
public class AdminChooseLinkController extends AdminBaseController{
//		@Override
//	protected ShopApplication shop() {
//		return saas.getShopApp(471752);
//	}
	/**
	 * 常用链接
	 */
	public Boolean commonLink() {
		return false;
	}

	/**
	 * 商品链接
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/goods/list")
	public JsonResult goodsLink(@RequestBody GoodsLinkVo param) {
		PageResult<GoodsLinkVo> goodsLinkList = shop().chooselink.getGoodsLink(param);
		return this.success(goodsLinkList);
	}

	/**
	 * 自定义页面
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/page/custom")
	public JsonResult customPage(@RequestBody ChooseLinkParam param) {
		PageResult<XcxCustomerPageVo> pageList = shop().chooselink.customPage(param);
		return success(pageList);
	}

	/**
	 * 创建网页跳转内容
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/web/save")
	public JsonResult saveWebLink(@RequestBody XcxLinkListVo param) {
		//正则校验URL合法性
		String pattern = "/^https:\\/\\/[\\w-_.]+(\\/[\\w-_]+)*\\/?$/";
		String linkPath = param.getLinkPath();
		if(!linkPath.matches(pattern)) {
			return this.fail(JsonResultMessage.DECORATE_URL_ILLEGAL);
		}

		int result = shop().chooselink.saveWebLink(param);
		if(result > 0) {
			return this.success(result);
		}else {
			return this.fail();
		}
	}

	/**
	 * 网页跳转列表
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/web/list")
	public JsonResult webLink() {
		List<XcxLinkListVo> list = shop().chooselink.getWebLink();
		return this.success(list);

	}

	/**
	 * 删除网页跳转记录
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/web/del")
	public JsonResult delWebLink(Integer id){
		int res = shop().chooselink.delWebLink(id);
		return this.success(res);
	}

	/**
	 * 表单页面链接
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/form/list")
	public JsonResult fromPage() {
		List<PageFormVo> list = shop().chooselink.getFromPage();
		return this.success(list);
	}

	/**
	 * 门店列表
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/store/list")
	public JsonResult store(StoreListQueryParam param) {
		PageResult<StoreVo> storeList = shop().chooselink.store(param);
		return this.success(storeList);
	}

	/**
	 * 获取小程序名称
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/xcx/list")
	public JsonResult getXcxNameList() {
		List<XcxNameListVo> list = shop().chooselink.getXcxNameList();
		return this.success(list);
	}

	/**
	 * 小程序跳转链接列表
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/link/list")
	public JsonResult xcxLinkList() {
		List<XcxLinkListVo> linkList = shop().chooselink.getXcxLinkList();
		return this.success(linkList);
	}

	/**
	 * 添加小程序跳转链接
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/decorate/link/save")
	public JsonResult xcxLinkSave(@RequestBody XcxLinkListVo param) {
		Boolean res = shop().chooselink.saveXcxLink(param);
		if(res) {
			return this.success();
		}else{
			return this.fail();
		}
	}

	/**
	 * 删除小程序跳转链接
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/link/del")
	public JsonResult xcxLinkDel(Integer id) {
		int res = shop().chooselink.delXcxLink(id);
		return this.success(res);
	}

	/**
	 * 拼团抽奖链接
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/pin/list")
	public JsonResult groupDraw() {
		List<ActivityVo> pinDrawList = shop().chooselink.getGroupDrawList();
		return this.success(pinDrawList);
	}

	/**
	 * 瓜分积分链接
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/integration/list")
	public JsonResult pinIntegration() {
		List<ActivityVo> integrationList = shop().chooselink.getIntegrationList();
		return this.success(integrationList);
	}

	/**
	 * 好友助力链接
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/promote/list")
	public JsonResult promoteList() {
		List<ActivityVo> promoteList = shop().chooselink.getPromoteList();
		return this.success(promoteList);
	}

	/**
	 * 加价购链接
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/price/list")
	public JsonResult priceList(){
		List<ActivityVo> priceList = shop().chooselink.getPriceList();
		return this.success(priceList);
	}

	/**
	 * 幸运到抽奖链接
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/lottery/list")
	public JsonResult lotteryList() {
		List<ActivityVo> lotteryList = shop().chooselink.getLotteryList();
		return this.success(lotteryList);
	}

	/**
	 * 优惠券链接
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/voucher/list")
	public JsonResult voucherList(Byte couponType) {
		 List<ActivityVo> voucherList = shop().chooselink.getVoucherList(couponType);
		 return this.success(voucherList);
	}

	/**
	 * 会员卡链接
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/card/list")
	public JsonResult cardList() {
		 List<ActivityVo> cardList = shop().chooselink.getCardList();
		 return this.success(cardList);
	}

	/**
	 * 一口价链接
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/package/list")
	public JsonResult packageList() {
		 List<ActivityVo> packageList = shop().chooselink.getPackageList();
		 return this.success(packageList);
	}

	/**
	 * 满折满减活动链接
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/mrking/list")
	public JsonResult mrkingList() {
		List<ActivityVo> mrkingList = shop().chooselink.getMrkingList();
		return this.success(mrkingList);
	}

	/**
	 * 测评活动链接
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/assess/list")
	public JsonResult assessList() {
		 List<ActivityVo> assessList = shop().chooselink.getAssessList();
		 return this.success(assessList);
	}

	/**
	 * 优惠券礼包
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/pack/list")
	public JsonResult packList() {
		List<ActivityVo> packList = shop().chooselink.getPackList();
		return this.success(packList);
	}

	/**
	 * 选择平台分类
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/cate/list")
	public JsonResult sysCate() {
//		List<SysCatevo> parentList = SysCatServiceHelper.getAllSysCateVoByCat();
		return this.success(new ArrayList());
	}

	/**
	 * 选择平台分类,根据父id获取下级分类
	 * @return
	 */
	@GetMapping(value = "/admin/decorate/cate/child")
	public JsonResult sysCate(Integer parentId) {
		List<ChildCateVo> parentList = saas.sysCate.getSysCateChild(parentId);
		return this.success(parentList);
	}

	/**
	 * 品牌分类
	 * @return
	 */
	@GetMapping("/admin/decorate/brand/list")
	public JsonResult brandClassifyList() {
		List<GoodsBrandClassifyVo> chooseBrandList = shop().goods.goodsBrand.getBrandClassifyList();
		return this.success(chooseBrandList);
    }

	/**
	 * 商家分类
	 * @return
	 */
	@GetMapping("/admin/decorate/sort/list")
	public JsonResult sortList() {
		List<SortVo> sortList = shop().chooselink.getSortList();
		return this.success(sortList);
	}

	/**
	 * 商品标签链接
	 * @return
	 */
	@GetMapping("/admin/decorate/label/list")
	public JsonResult goodsLabel() {
		List<GoodsLabelSelectListVo> goodsLabelList = shop().goods.goodsLabel.getGoodsLabelSelectList();
		return this.success(goodsLabelList);
	}
}
