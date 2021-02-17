package com.meidianyi.shop.service.pojo.shop.auth;

import java.util.ArrayList;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 
 * @author lixinguo
 *
 */
@ConfigurationProperties(prefix = "admin.menu.authority")
@Component
public class MenuAuthority extends ArrayList<MenuAuthorityItem>{

	private static final long serialVersionUID = -8795781933083439261L;
}
