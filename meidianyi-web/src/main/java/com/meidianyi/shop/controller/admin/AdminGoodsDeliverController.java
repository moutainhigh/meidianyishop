package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.area.AreaProvinceVo;
import com.meidianyi.shop.service.pojo.shop.config.DeliverTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.goods.deliver.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 运费模版控制器
 * 
 * @author liangchen
 * @date 2019年7月11日
 */
@RestController
@RequestMapping("/api/admin/goods/deliver")
public class AdminGoodsDeliverController extends AdminBaseController {

	/**
	 * 返回所有地区代码及名称
	 *
	 * @return JsonResult
	 */
	@GetMapping("/select")
	public JsonResult getAllArea() {
		List<AreaProvinceVo> areaSelectVo = shop().goods.goodsDeliver.getAllArea();
		return success(areaSelectVo);
	}

	/**
	 * 运费模版分页查询
	 *
	 * @param param 类型标识
	 * @return 分页信息
	 */

	@PostMapping("/templatelist")
	public JsonResult getTemplateList(@RequestBody GoodsDeliverPageListParam param) {
	    //店铺默认模板配置
		String config = shop().config.deliverTemplateConfigService.getDefaultDeliverTemplate();
		//模板信息分页查询
		PageResult<GoodsDeliverTemplateVo> pageResult = shop().goods.goodsDeliver.getDeliverTemplateList(param);
		//默认配置和其他模板信息一起返回
		GoodsDeliverTemplateListVo vo = new GoodsDeliverTemplateListVo(){{
            setConfig(config);
            setPageResult(pageResult);
        }};
		return success(vo);

	}

	/**
	 * 添加运费模版
	 * 
	 * @param goodsDeliverTemplateParam 模板名称 类型 详细信息
	 */
	@PostMapping("/addtemplate")
	public JsonResult addDeliverTemplate(@RequestBody GoodsDeliverTemplateParam goodsDeliverTemplateParam) {

		shop().goods.goodsDeliver.addDeliverTemplate(goodsDeliverTemplateParam);

		return success();
	}

	/**
	 * 修改运费模版
	 * 
	 * @param goodsDeliverTemplateParam 模板id 模板名称 类型 详细信息
	 *
	 */
	@PostMapping("/updatetemplate")
	public JsonResult updataDeliverTemplate(@RequestBody GoodsDeliverTemplateParam goodsDeliverTemplateParam) {

		shop().goods.goodsDeliver.updateDeliverTemplate(goodsDeliverTemplateParam);

		return success();
	}


	/**
	 * 真删除指定运费模版
	 *
	 * @param goodsDeliverIdParam 模板id
	 */
	@PostMapping("/delete")
	public JsonResult delete(@RequestBody GoodsDeliverIdParam goodsDeliverIdParam) {

		shop().goods.goodsDeliver.deleteDeliverTemplate(goodsDeliverIdParam);

		return success();
	}

	/**
	 * 修改模版前先查询单个模版的信息，将其参数作为修改时的默认值
	 *
	 * @param param 模板id
	 * @return JsonResult
	 */

	@PostMapping("/templateone")
	public JsonResult selectOne(@RequestBody GoodsDeliverIdParam param) {

		GoodsDeliverTemplateVo singleTemplate = shop().goods.goodsDeliver.selectOne(param);

		return success(singleTemplate);

	}

	/**
	 * 修改默认运费模板配置
	 *
	 * @param  param 标识与配置信息
	 */
	@PostMapping("/config")
	public JsonResult setDefaultDeliverTemplate(@RequestBody DeliverTemplateConfig param) {

		shop().config.deliverTemplateConfigService.setDefaultDeliverTemplate(param);

		return success();
	}

	/**
	 * 运费模板下拉框
	 *
	 * @return 地区列表
	 */
	@GetMapping("/box")
	public JsonResult getBox() {
		List<GoodsDeliverBoxVo> boxVos = shop().goods.goodsDeliver.getBox();
		return success(boxVos);
	}
	/**
	 * 获取默认运费模板配置
	 *
	 * @return 默认运费模板信息
	 */
	@GetMapping("/getconfig")
	public JsonResult getDefaultDeliverTemplate() {
		String config = shop().config.deliverTemplateConfigService.getDefaultDeliverTemplate();
		return success(config);
	}
	
	/**
	 *	复制运费模板
	 *
	 * @param param 模板id
	 */
	@PostMapping("/copy")
	public JsonResult copyTemplate(@RequestBody GoodsDeliverIdParam param) {
		shop().goods.goodsDeliver.copyTemplate(param);
		return success();
	}
}
