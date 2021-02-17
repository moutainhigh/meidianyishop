package com.meidianyi.shop.service.shop.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 评价配置
 * @author liangchen
 */
@Service

public class CommentConfigService extends BaseShopConfigService{

    /**
     * 0不用审核，1先发后审，2先审后发
     */
	private final static String K_COMMENT = "comment";
    /**
     * 设置前端是否隐藏未填写心得的评价，0关，1开
     */
	private final static String K_COMMENT_STATE = "comment_state";
	
	 /**
     * 修改评价审核配置
     *
     * @param value
      */
	public void setCheckConfig(String value) {
        this.set(K_COMMENT, value);
    }

    /**
     * 获取评价审核状态
     * @return 默认为0
     */
	public Byte getCommentConfig() {
        String s = this.get(K_COMMENT);
        return StringUtils.isBlank(s)? 0 : Byte.valueOf(s);
    }
	
	/**
     * 修改开关配置
     *
     * @param value
     */
	public void setSwitchConfig(String value) {
        this.set(K_COMMENT_STATE, value);
    }
    /**
     * 获取评价审核状态
     * @return 默认为0
     */
    public Byte getSwitchConfig() {
        String s = this.get(K_COMMENT_STATE);
        return StringUtils.isBlank(s)? 0 : Byte.valueOf(s);
    }
}
