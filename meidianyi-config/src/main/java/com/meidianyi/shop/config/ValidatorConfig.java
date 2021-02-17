package com.meidianyi.shop.config;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * @author liufei
 * @date 10/12/2019
 */
@Configuration
public class ValidatorConfig {
    private static final String HIBERNATE_VALIDATOR_FAIL_FAST = "hibernate.validator.fail_fast";
    final protected static String UNDEER_LINE = "_";
    public final static String UNDEER_POINT=".";
    public final static String VALID_MESSAGE_PREFIX="{";
    public final static String VALID_MESSAGE_SUFFIX="}";
    public final static String JAVAX_VALIDATION_PREFIX ="message";
    public final static String JAVAX_ORG_HIBERNATE_KEY=".constraints.";

    /**
     * 读取国际化文件
     */
    private static final List<String> BUNDLE_NAMES = new ArrayList<String>(){{
        add("static/i18n/param");
        add("static/i18n/messages");
    }};


    @Value(value = "${hibernate.validator.fail_fast:false}")
    private String isFailFast;

    @Bean
    public Validator validator() {
        Properties properties = new Properties();
        properties.setProperty(HIBERNATE_VALIDATOR_FAIL_FAST, isFailFast);
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationProperties(properties);
        localValidatorFactoryBean.setMessageInterpolator(new MessageInterpolator(BUNDLE_NAMES));
        return localValidatorFactoryBean;
    }
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator());
        return methodValidationPostProcessor;
    }

    public class MessageInterpolator extends ResourceBundleMessageInterpolator implements javax.validation.MessageInterpolator {
        MessageInterpolator( List<String> bundleNames) {
            super(new AggregateResourceBundleLocator(bundleNames));
        }
        @Override
        public String interpolate(String message, Context context, Locale locale) {
            if (message!=null&&message.startsWith(VALID_MESSAGE_PREFIX)&&!message.contains(JAVAX_ORG_HIBERNATE_KEY)){
                String annotationTypeName = context.getConstraintDescriptor().getAnnotation().annotationType().getName();
                String simpleName = context.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
                MessageInterpolatorContext mContext = (MessageInterpolatorContext)context;
                String name =messageBrace( mContext.getRootBeanType().getSimpleName(),  message.substring(1, message.length() - 1),simpleName );
                String interpolate = super.interpolate(name, context, locale);
                if (interpolate.equals(name)){
                     name =messageBrace( mContext.getRootBeanType().getSimpleName(), message.substring(1, message.length() - 1));
                     interpolate = super.interpolate(name, context, locale);
                     if (interpolate.equals(name)){
                         name =messageBrace(message.substring(1, message.length() - 1));
                         interpolate = super.interpolate(name, context, locale);
                     }
                }
                message =interpolate+messageBrace(annotationTypeName,JAVAX_VALIDATION_PREFIX);
            }else if (message!=null&&!message.startsWith(VALID_MESSAGE_PREFIX)&&!message.contains(JAVAX_ORG_HIBERNATE_KEY)) {
                message = messageBrace(message);
            }
            return super.interpolate(message, context, locale);
        }
        private String messageBrace(String... message){
            return  VALID_MESSAGE_PREFIX + StringUtils.join(message, UNDEER_POINT) + VALID_MESSAGE_SUFFIX;
        }
    }


    /**
     * 国际化语言切换
     *
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new AcceptHeaderLocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                Locale defaultLocale = Locale.SIMPLIFIED_CHINESE;
                String langHeader = "V-Lang";
                if (defaultLocale != null && request.getHeader(langHeader) == null) {
                    return defaultLocale;
                }
                String langStr = request.getHeader(langHeader);
                String[] languages = langStr.split(UNDEER_LINE);
                return new Locale(languages[0], languages[1]);
            }
        };
    }
}
