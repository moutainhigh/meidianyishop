package com.meidianyi.shop.controller;

import java.util.List;

import com.meidianyi.shop.auth.StoreAuthInterceptor;
import com.meidianyi.shop.auth.WxAppAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.meidianyi.shop.auth.AdminAuthInterceptor;
import com.meidianyi.shop.auth.SystemAuthInterceptor;
import com.meidianyi.shop.support.LineToHumpHandler;

/**
 *
 * @author 新国
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	protected Environment env;

	@Autowired
	protected SystemAuthInterceptor sysAuthInterceptor;

	@Autowired
	protected AdminAuthInterceptor adminAuthInterceptor;
	@Autowired
	protected WxAppAuthInterceptor wxAppAuthInterceptor;
	@Autowired
	protected StoreAuthInterceptor storeAuthInterceptor;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new LineToHumpHandler());

	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(sysAuthInterceptor).addPathPatterns("/api/system/**")
				.excludePathPatterns("/api/system/login", "/api/system/logout", "/api/system/message");
		registry.addInterceptor(adminAuthInterceptor).addPathPatterns("/api/admin/**").addPathPatterns("/api/wechat/**")
				.excludePathPatterns("/api/admin/login", "/api/admin/logout", "/api/admin/shopDecorate/**",
						"/api/admin/order/orderList", "/api/wechat/proxy/**","/admin/**");
        registry.addInterceptor(storeAuthInterceptor).addPathPatterns("/api/store/**")
            .excludePathPatterns("/api/store/login", "/api/store/logout");
		registry.addInterceptor(wxAppAuthInterceptor).addPathPatterns("/api/wxapp/**");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
				.allowCredentials(true).maxAge(3600);
	}
}
