package com.yj2025.mail;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;

/**
 * Created by serv on 2015/7/28.
 */
public class MailServiceImpl implements MailService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private JavaMailSender javaMailSender;

    private String fromNickName;

    private MailProperties properties;

    public MailServiceImpl(JavaMailSender javaMailSender, String fromNickName, MailProperties properties) {
        this.javaMailSender = javaMailSender;
        this.fromNickName = fromNickName;
        this.properties = properties;
    }

    @Override
    public void send(String[] to, String subject, String html,File[] attachments) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            //创建MimeMessageHelper对象，处理MimeMessage的辅助类
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(new InternetAddress(properties.getUsername(), fromNickName));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html,true);
//            //添加资源文件
//            if(inlines!=null){
//                for (File inlineFile : inlines){
//                    helper.addInline(inlineFile.getName(), inlineFile);
//                }
//            }
            //添加附件
            if(attachments!=null){
                for (File attachmentFile : attachments){
                    helper.addAttachment(attachmentFile.getName(), attachmentFile);
                }
            }

        } catch (Exception e) {
            throw new MailSendException(e.getMessage(),e);
        }

        logger.info("mail: to[{}] subject[{}]",to,subject);
        javaMailSender.send(mimeMessage);
    }
}
