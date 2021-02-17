package com.meidianyi.shop.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

/**
 * 
 * @author lixinguo
 *
 */
@Configuration
@Data
public class UpYunConfig {
    private static final String PATTERN = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

	@Value(value = "${uyun.image.sv}")
	protected String server;

	@Value(value = "${uyun.image.op.name}")
	protected String name;

	@Value(value = "${uyun.image.op.pwd}")
	protected String password;

	@Value(value = "${uyun.video.sv}")
	protected String videoServer;

	@Value(value = "${uyun.video.op.name}")
	protected String videoOpName;

	@Value(value = "${uyun.video.op.pwd}")
	protected String videoOpPassword;

	@Value(value = "${uyun.video.domain}")
	protected String videoDomain;

	public String videoUrl(String path) {
        if( Pattern.matches(PATTERN,path) ){
            return path;
        }
	    return "http://" + this.videoDomain + path;
	}
	
	public String imageUrl(String path) {
        if( Pattern.matches(PATTERN,path) ){
            return path;
        }
	    return "http://" + this.videoDomain + path;
	}

}
