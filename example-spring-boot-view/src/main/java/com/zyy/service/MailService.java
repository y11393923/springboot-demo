package com.zyy.service;

import org.thymeleaf.Thymeleaf;

import javax.mail.MessagingException;
import java.io.File;
import java.util.Map;

public interface MailService {
    void sendSimpleMail(String from, String to, String cc, String subject, String content) throws Exception;

    void sendAttachFileMail(String from, String to, String cc, String subject, String content, File file) throws Exception;

    void sendMailWithirng(String from, String to, String cc, String subject, String content,String[] srcPath,String[] resids) throws Exception;

    void sendHtmlMailThymeleaf(String from, String to, String cc, String subject, Map<String,Object> params, String thymeleafPath) throws Exception;
}
