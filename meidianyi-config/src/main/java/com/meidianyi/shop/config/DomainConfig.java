package com.meidianyi.shop.config;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 域名配置
 *
 * @author lixinguo
 *
 */
@Configuration
@Data
public class DomainConfig {

	@Value(value = "${domain.main}")
	protected String mainDomain;

	@Value(value = "${domain.image}")
	protected String imageDomain;

    @Value(value = "${uyun.video.domain}")
    protected String videoDomain;

    /**
     * 视频路径
     *
     * @param relativePath
     * @return
     */
    public String videoUrl(String relativePath) {
        return this.videoUrl(pathJudge(relativePath), null);
    }

    /**
     * 视频路径
     *
     * @param relativePath
     * @return
     */
    public String videoUrl(String relativePath, String schema) {
        schema = StringUtils.isBlank(schema) ? "http" : schema;
        return String.format("%s://%s/%s", schema, videoDomain, relativePath);
    }

	/**
	 * 图片路径
	 *
	 * @param relativePath
	 * @return
	 */
	public String imageUrl(String relativePath) {
		return this.imageUrl(pathJudge(relativePath), null);
	}

	/**
	 * 图片路径
	 *
	 * @param relativePath
	 * @return
	 */
	public String imageUrl(String relativePath, String schema) {
		schema = StringUtils.isBlank(schema) ? "http" : schema;
		return String.format("%s://%s/%s", schema, imageDomain, relativePath);
	}

	/**
	 * 主域名路径
	 *
	 * @param relativePath
	 * @return
	 */
	public String mainUrl(String relativePath) {
		return this.mainUrl(relativePath, null);
	}

	/**
	 * 主域名路径
	 *
	 * @param relativePath
	 * @return
	 */
	public String mainUrl(String relativePath, String schema) {
		schema = schema == null ? "http" : schema;
		return String.format("%s://%s%s", schema, mainDomain, relativePath);
	}


	/**
	 * 小程序支付回调URL
	 * @param shopId
	 * @return
	 */
	public String getWxMaPayNotifyUrl(Integer shopId) {
		return mainUrl("/wechat/notify/ma/payment/"+shopId);
	}

	/**
	 * 判断路径开始是否为/，并做是否删除
	 * @param relativePath
	 * @return
	 */
	private String pathJudge(String relativePath) {
        char c = '/';
        if(!Strings.isEmpty(relativePath)&&relativePath.charAt(0)== c) {
			relativePath=relativePath.replaceFirst("/", "");
		}
		return relativePath;
	}

	/**
	 * 获取图片域名
	 * @return
	 */
	public String getImageHost() {
		return String.format("%s://%s/%s", "http", getImageDomain(), "");
	}
}
