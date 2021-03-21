package com.util;

import com.helei.api.common.ImplBase;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author:
 * @Date: 2018/12/5 15:23
 * @Description: 邮件服务的实现类
 */
@Component
public class MailServiceImpl extends ImplBase implements MailService {
    @Override
    public Map sendSimpleMail(JavaMailSender jms, String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = jms.createMimeMessage();
            MimeMessageHelper helper;
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("15111402649@163.com","测试系统");                //sender为自定义显示发件人名称
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);
            //helper.addAttachment("wx.png", new ClassPathResource("/static/images/wx.png"));
            jms.send(mimeMessage);                      //邮件发送完毕

//            SimpleMailMessage message = new SimpleMailMessage();
//            String fromByte = new String(("测试账户" + " <" + from + ">")
//                    .getBytes("UTF-8"));
//            message.setFrom(fromByte);
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(content);
//            mailSender.send(message);

            getLogger().info("邮件已经发送。");
            return new HashMap(){
                {
                    put("code", 0);
                    put("status", "SUCCESS");
                    put("msg", "邮件已经发送");
                }
            };
        } catch (Exception e) {
            getLogger().error("发送邮件时发生异常！", e);
            return new HashMap(){
                {
                    put("code", -1);
                    put("status", "ERRO");
                    put("msg", "发送邮件时发生异常");
                    put("Exception",e);
                }
            };
        }

    }


}