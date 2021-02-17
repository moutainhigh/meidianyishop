package com.meidianyi.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.util.TimeZone;

/**
 *
 * @author lixinguo
 *
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class,
    ElasticsearchAutoConfiguration.class,RestClientAutoConfiguration.class})
@ServletComponentScan
public class WebApplication {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		//close JOOQ banner
        System.getProperties().setProperty("org.jooq.no-logo", "true");
		SpringApplication app = new SpringApplication(WebApplication.class);
		app.run(args);
	}
}
