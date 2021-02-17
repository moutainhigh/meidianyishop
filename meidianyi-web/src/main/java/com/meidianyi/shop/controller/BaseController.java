package com.meidianyi.shop.controller;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.service.foundation.util.I18N;
import com.meidianyi.shop.service.foundation.util.VoTranslator;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.summary.visit.VisitPageParam;
import com.meidianyi.shop.service.saas.SaasApplication;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author 新国 */
public class BaseController {
  @Autowired protected SaasApplication saas;

  @Autowired protected HttpServletRequest request;

  @Autowired protected Environment env;

  @Autowired private DomainConfig domainConfig;

  /**
   * Json输出
   *
   * @param resultCode
   * @param content
   * @return
   */
  public JsonResult result(JsonResultCode resultCode, Object content, Object... args) {
    String language = getLang();
    return (new JsonResult()).result(language, resultCode, content, args);
  }

  public JsonResult success() {
    return result(JsonResultCode.CODE_SUCCESS, null);
  }

  public JsonResult success(Object content) {
    return result(JsonResultCode.CODE_SUCCESS, content);
  }

  public JsonResult fail(JsonResultCode resultCode, Object... args) {
    return result(resultCode, null, args);
  }

  public JsonResult fail(ResultMessage message) {
    return result(message.getJsonResultCode(), null, message.getMessages().toArray());
  }

  public JsonResult fail() {
    return result(JsonResultCode.CODE_FAIL, null);
  }

  protected boolean isPost() {
    return "POST".equals(request.getMethod());
  }

  protected String post(String key) {
    return isPost() ? request.getParameter(key) : null;
  }

  protected String input(String key) {
    return request.getParameter(key);
  }

  protected Map<String, String[]> input() {
    return request.getParameterMap();
  }

  protected String getLang() {
    return request.getHeader("V-Lang");
  }

  /**
   * 响应错误信息
   *
   * @param apiMessageKey 错误信息key，对应JsonResultCode的message
   * @return
   */
  public JsonResult fail(String apiMessageKey) {
    for (JsonResultCode code : JsonResultCode.values()) {
      if (code.getMessage().equals(apiMessageKey)) {
        return result(code, null);
      }
    }
    throw new RuntimeException(apiMessageKey + " not defined in JsonResultCode.");
  }

  /**
   * 输入参数Map
   *
   * @param delim
   * @return
   */
  protected Map<String, String> inputMap(String delim) {
    Map<String, String> result = new HashMap<String, String>(0);
    Map<String, String[]> maps = this.input();
    for (String key : maps.keySet()) {
      String value = StringUtils.arrayToDelimitedString(maps.get(key), delim);
      result.put(key, value);
    }
    return result;
  }

  /**
   * 输入参数Map
   *
   * @return
   */
  protected Map<String, String> inputMap() {
    return inputMap(",");
  }

  /**
   * 主站路径URL
   *
   * @param path
   * @return
   */
  public String mainUrl(String path) {
    return domainConfig.mainUrl(path, request.getScheme());
  }

  /**
   * 图片路径URL
   *
   * @param path
   * @return
   */
  public String imageUrl(String path) {
    return domainConfig.imageUrl(path, request.getScheme());
  }

  /**
   * 返回带有国际化的 json 响应
   *
   * <p>使用方法：在入参或出参对象及其中嵌套对象中需要翻译的字段中添加 {@link I18N} 注解并指定对应的 properties 文件名，确保这些字段的值均为 properties
   * 中的某个 key，调用此方法后会通过 {@link VoTranslator#translateFields(Object,String)} (Object)} 自动对所有这些字段进行翻译
   *
   * <p>目前支持的属性类型：{@code Class<String>, Class<? extends Object> Class<List<? extends Object>>,
   * Class<List<String>>} 分别对应 json 中的 字符串（string）、对象（object）、对象数组及字符串数组（array）
   *
   * <p>使用示例：{@link com.meidianyi.shop.controller.admin.AdminSummaryController#getVisitPage(VisitPageParam)}
   *
   * @param content 出参对象
   * @author 郑保乐
   * @since r941 不再支持 ArrayList 等 List 子类型声明的成员变量
   */
  protected JsonResult i18nSuccess(Object content) {
    VoTranslator.translateFields(content, getLang());
    return result(JsonResultCode.CODE_SUCCESS, content);
  }

    protected <T> JsonResult i18nSuccess(List<T> contents) {
        contents.forEach(content -> VoTranslator.translateFields(content, getLang()));
        return result(JsonResultCode.CODE_SUCCESS, contents);
    }

  public JsonResult wxfail(WxOpenResult result) {
    JsonResult result2 = null;
    for (JsonResultCode code : JsonResultCode.values()) {
      if (code.getMessage().equals(result.getErrmsg())) {
        result2 = result(code, null);
      }
    }
    return result(JsonResultCode.CODE_FAIL, result2 == null ? result : result2);
  }

  /**
   * Export 2 excel. 导出excel，统一写入response的输出流，并关闭workbook
   *
   * @param workbook service层返回已经填充完数据的工作簿
   * @param fileName 导出的excel文件名
   * @param response 设置统一的响应类型，格式和报文头等信息
   */
	protected void export2Excel(Workbook workbook, String fileName, HttpServletResponse response) {
		try {
			response.setContentType("application/octet-stream;charset=UTF-8");
            String encodeFileName = URLEncoder.encode(String.format("%s.xlsx", fileName), "utf-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + encodeFileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            workbook.write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
