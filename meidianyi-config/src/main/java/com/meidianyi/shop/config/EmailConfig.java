package com.meidianyi.shop.config;

import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Properties;

/**
 * @author luguangyao
 */
@Configuration
public class EmailConfig {



    @Value(value="${email.send.name}")
    private String sendUserName;

    @Value(value="${email.send.pwd}")
    private String sendUserPwd;

    @Value(value="${email.send.host}")
    private String sendHost;

    @Value(value="${email.send.port:25}")
    private Integer sendPort;

    @Value(value="${email.send.ssl.enable:true}")
    private Boolean sslEnable;

    /** 初始化连接邮件服务器的会话信息 */
    private static Properties props = null;

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(sendHost);
        sender.setUsername(sendUserName);
        sender.setPort(sendPort);
        sender.setPassword(sendUserPwd);
        sender.setDefaultEncoding("UTF-8");

        if( sslEnable ){
            try {
                sender.setJavaMailProperties(getProps());
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

        }
        return sender;
    }

    private Properties getProps() throws GeneralSecurityException {
        Properties properties = new Properties();

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.auth", sslEnable);
        properties.put("mail.smtp.ssl.enable", sslEnable);
        properties.put("mail.smtp.ssl.socketFactory", sf);
        properties.put("mail.smtp.starttls.enable", sslEnable);

        return properties;
    }

    public MimeMessage mimeMessage(){
        MimeMessage message = null;
        try {
            Session session = Session.getDefaultInstance(getProps());
            message = new MimeMessage(session);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return message;
    }


}
