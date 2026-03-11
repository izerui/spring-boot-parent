package com.yj2025.mail;

import java.io.File;

/**
 * Created by serv on 2015/7/28.
 */
public interface MailService {

    /**
     * 发送邮件
     * @param to 收件人 可以参考 http://www.ietf.org/rfc/rfc822.txt
     * @param subject 标题
     * @param html html内容
     * @param attachments 附件
     */
    void send(String[] to, String subject, String html,File[] attachments);
}
