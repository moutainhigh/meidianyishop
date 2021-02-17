package com.meidianyi.shop.service.pojo.shop.market.form.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Map;

/**
 * 表单pojo
 * @author 孔德成
 * @date 2020/4/28
 */
@Getter
@Setter
@ToString
public class FormInfoBo {

    private Integer pageId;
    private String pageName;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte validityPeriod;
    private Byte isForeverValid;
    private Boolean isNewUser;
    /**
     * 提交数量
     */
    private Integer submitNum;
    /**
     * 状态：0未发布，1已发布 2已关闭 3 已删除
     */
    private Byte status;
    private Byte state;
    private String statusText;
    /**
     * json  页面内容
     */
    private String pageContent;
    /**
     * json  表单配置
     */
    private String formCfg;
    /**
     * 表单配置
     */
    private FormCfgBo formCfgBo;
    /**
     * 页面内容
     */
    Map<String,FormModulesBo> pageContentBo;


}
