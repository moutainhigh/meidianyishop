package com.meidianyi.shop.email;

import com.meidianyi.shop.App;
import com.meidianyi.shop.service.foundation.email.EmailMsgTemplate;
import com.meidianyi.shop.service.foundation.email.EmailService;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = App.class)
@ActiveProfiles("dev-mac")
public class EmailTest {

    @Autowired(required = false)
    EmailService emailService;

    private String receiveName;

    @Before
    public void init(){
        receiveName = "luguangyao@huice.com";
    }


    @Test
    public void testTextMailMessage(){
        emailService.sendTextMessage(receiveName,"测试Text","java小程序测试邮件发送");
    }
    @Test
    public void testHtmlMailMessage(){
        String table = EmailMsgTemplate.TableTemplate.table;

        String idTh = String.format(EmailMsgTemplate.TableTemplate.th,"id");
        String msgTh = String.format(EmailMsgTemplate.TableTemplate.th,"msg");
        String tr = String.format(EmailMsgTemplate.TableTemplate.tr,idTh+msgTh);
        StringBuilder tableContext = new StringBuilder(tr);
        for(int i = 0;i < 5;i++){
            String td = String.format(EmailMsgTemplate.TableTemplate.td,i);
            String td2 = String.format(EmailMsgTemplate.TableTemplate.td,"#^_^#");
            String msgTr = String.format(EmailMsgTemplate.TableTemplate.tr,td+td2);
            tableContext.append(msgTr);
        }
        emailService.sendHtmlMessage(receiveName,"测试Html",new Date()+"\n"+String.format(table, tableContext.toString()));
    }

}
