package com.meidianyi.shop.controller.admin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.member.tag.DeleteTagParam;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagInfoParam;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagPageListParam;
import com.meidianyi.shop.service.pojo.shop.member.tag.UpdateTagParam;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagVo;

/**
 * 会员标签管理
 * 
 * @author 黄壮壮 2019-07-09 10:19
 */
@RestController
public class AdminTagController extends AdminBaseController {

	/**
	 * 分页获取全部标签列表
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/api/admin/tag/list")
	public JsonResult getTagList(@RequestBody @Valid TagPageListParam param) {
		
		return this.success(shop().tag.getPageList(param));
	}
	
	/**
	 * 标签弹窗
	 */
	@PostMapping("/api/admin/common/tag/list")
	public JsonResult getCommonTag() {
		logger().info("获取标签的弹窗接口");
		return this.success(shop().tag.getCommonTagList());
	}
	
	
	/**
	 * 添加标签
	 * @param param
	 * @param result
	 * @return
	 */
	@PostMapping(value = "/api/admin/tag/add")
	public JsonResult addTag(@RequestBody @Valid TagInfoParam param,BindingResult result) {

		//测试参数
		if(result.hasErrors()) {
			return this.fail(result.getFieldError().getDefaultMessage());
		}
		
		//check is the tagName is exits
		boolean tagNameExists = shop().tag.tagNameExists(param.getTagName());
		// if tag name exists then return
		if(tagNameExists) {
			JsonResult r = this.fail(JsonResultMessage.MSG_MEMBER_TAG_NAME_EXIST);
			r.setMessage(param.getTagName()+r.getMessage());
			return r;
		}
		
		// insert into database
		shop().tag.addTagName(param.getTagName());
	
		return this.success();
	}
	
	/**
	 * 删除标签
	 * @param param
	 * @return
	 */
	@PostMapping(value="/api/admin/tag/delete")
	public JsonResult deleteTag(@RequestBody  @Valid DeleteTagParam param) {
		
		//check tagName is exist
		boolean tagIdExists = shop().tag.tagIdExists(param.getTagId());
		if(!tagIdExists) {
			return this.success();
		}
		shop().tag.deleteTag(param.getTagId());
		return this.success();
	}
	
	/**
	 * 更新标签名称
	 * @param param
	 * @return
	 */
	@PostMapping(value="/api/admin/tag/update")
	public JsonResult updateTagName(@RequestBody @Valid UpdateTagParam param) {
		int result =  shop().tag.updateTag(param);
		System.out.println(result);
		if(result != 1) {
			return this.fail();
		}
		return this.success();
	}
	
	
	/**
	 * 标签弹窗
	 */
	@PostMapping(value="/api/admin/tag/all/get")
	public JsonResult getAllTag() {
		List<TagVo> allTag = shop().tag.getAllTag();
		return success(allTag);
	}
	
	/**
	 * 根据用户id获取标签
	 */
	@PostMapping(value="/api/admin/user/tag/all/get")
	public JsonResult getAllTagByUserIds(@RequestBody List<Integer> param) {
		logger().info("获取所有会员的所有标签");
		Map<Integer, List<TagVo>> userTag = shop().tag.getUserTag(param);
        int initialCapacity = 16;
        Map<Integer,List<String>> res = new HashMap<>(initialCapacity);
		
		for(Integer id: param) {
			if(userTag.get(id) == null) {
				res.put(id, Collections.emptyList());
			}else {
				List<String> tags = userTag.get(id).stream().map(r->r.getTagName()).collect(Collectors.toList());
				res.put(id, tags);
			}
		}
		return success(res);
	}
	
	/**
	 * 根据标签id获取标签
	 */
	@PostMapping(value="/api/admin/tags/get")
	public JsonResult getTagsById(@RequestBody List<Integer> param) {
		logger().info("根据标签id获取标签");
		return success(shop().tag.getTagsById(param));
	}
	
	
}
