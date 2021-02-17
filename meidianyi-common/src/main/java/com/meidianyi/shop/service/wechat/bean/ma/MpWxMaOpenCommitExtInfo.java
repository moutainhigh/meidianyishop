package com.meidianyi.shop.service.wechat.bean.ma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import me.chanjar.weixin.open.bean.ma.WxMaOpenNetworkTimeout;
import me.chanjar.weixin.open.bean.ma.WxMaOpenPage;
import me.chanjar.weixin.open.bean.ma.WxMaOpenSubpackage;
import me.chanjar.weixin.open.bean.ma.WxMaOpenTabBar;
import me.chanjar.weixin.open.bean.ma.WxMaOpenWindow;

/**
 * 
 * @author lixinguo
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MpWxMaOpenCommitExtInfo {
	/**
	   * 授权小程序Appid，可填入商户小程序AppID，以区分不同商户
	   */
	  private String extAppid;
	  
	  /**
	   * 是否ext生效
	   */
	  private Boolean extEnable = true;

	  /**
	   * 是否直接提交
	   */
	  private Boolean directCommit = false;
	  
	  /**
	   * 允许跳转的appid列表
	   */
	  private List<String> navigateToMiniProgramAppIdList;
	  

	  @SerializedName("ext")
	  private Map<String, Object> ext;

	  @SerializedName("extPages")
	  private Map<String, WxMaOpenPage> extPages;

	  /**
	   * 页面路径列表(和app.json结构一致)
	   */
	  @SerializedName("pages")
	  private List<String> pages;

	  /**
	   * 分包结构配置
	   */
	  @SerializedName("subpackages")
	  private List<WxMaOpenSubpackage> subpackages;

	  @SerializedName("window")
	  private WxMaOpenWindow window;

	  @SerializedName("networkTimeout")
	  private WxMaOpenNetworkTimeout networkTimeout;

	  @SerializedName("tabBar")
	  private WxMaOpenTabBar tabBar;

	  /**
	   * 添加扩展项
	   *
	   * @param key
	   * @param value
	   */
	  public void addExt(String key, String value) {
	    if (ext == null) {
	    	ext = new HashMap<>(0);
	    }
	    if (StringUtils.isNoneBlank(key, value)) {
	    	ext.put(key, value);
	    }
	  }

	  /**
	   * 添加扩展页面
	   *
	   * @param pagePath
	   * @param page
	   */
	  public void addExtPage(String pagePath, WxMaOpenPage page) {
	    if (extPages == null) {
	      extPages = new HashMap<>(0);
	    }
	    if (StringUtils.isNotBlank(pagePath) && page != null) {
	      extPages.put(pagePath, page);
	    }
	  }

	  /**
	   * 添加页面
	   *
	   * @param pagePath
	   */
	  public void addPage(String pagePath) {
	    if (pages == null) {
	    	pages = new ArrayList<>();
	    }
	    if (StringUtils.isNotBlank(pagePath)) {
	    	pages.add(pagePath);
	    }
	  }



}
