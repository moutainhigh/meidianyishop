package com.meidianyi.shop.service.foundation.email;

import com.meidianyi.shop.config.EmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * email service
 * @author 卢光耀
 * @date 2019-12-13 15:57
 *
*/
@Service
public class EmailService {

    @Value(value="${email.send.name}")
    private String sendUserName;


    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    EmailConfig emailConfig;


    /**
     * send simple mail message(only send text)
     * @param toUser send to user
     * @param title mail title
     * @param context mail context(text)
     */
    public void sendTextMessage(String toUser,String title,String context){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sendUserName);
        message.setTo(toUser);
        message.setSubject(title);
        message.setText(context);
        javaMailSender.send(message);
    }
    /**
     * send simple mail message(only send text)
     * @param toUser send to user
     * @param title mail title
     * @param html mail context(text)
     */
    public void sendHtmlMessage(String toUser,String title,String html){
            MimeMessage mimeMessage = emailConfig.mimeMessage();
        try {
            mimeMessage.setFrom(sendUserName);
            mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress(toUser));
            mimeMessage.setSubject(title);
            mimeMessage.setText(html,"UTF-8","html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        javaMailSender.send(mimeMessage);
    }
}
