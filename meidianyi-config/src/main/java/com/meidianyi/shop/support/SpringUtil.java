package com.meidianyi.shop.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author 新国
 *
 */
@Configuration
public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContextParam) throws BeansException {
		applicationContext = applicationContextParam;
	}

	public static <T> T getBean(Class<T> tClass) {
		String name = tClass.getSimpleName();
		String className = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
		if(applicationContext !=null && applicationContext.containsBean(className)) {
			return applicationContext.getBean(tClass);
		}
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		if (!factory.containsBean(className)) {
			GenericBeanDefinition gbd = new GenericBeanDefinition();
			gbd.setBeanClass(tClass);
			factory.registerBeanDefinition(className, gbd);
			System.out.println("getBean not found:"+className);
		}
		return factory.getBean(tClass);
	}

	public static boolean inited() {
		return applicationContext != null;
	}

	public static Object getBean(String id) {
		return applicationContext.getBean(id);
	}

}
