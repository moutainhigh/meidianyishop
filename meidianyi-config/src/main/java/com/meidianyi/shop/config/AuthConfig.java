package com.meidianyi.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 
 * @author lixinguo
 *
 */
@Configuration
@Data
public class AuthConfig {

	@Value(value = "${auth.secret}")
	protected String secret;

	@Value(value = "${auth.timeout}")
	protected Integer timeout;
}
