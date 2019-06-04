package com.zyy.service.impl;

import com.zyy.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 15:49 2019/5/30
 */
@Service
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private TemplateEngine templateEngine;

    /**
     * @param from  发送者
     * @param to    收件人
     * @param cc    抄送人
     * @param subject   邮件主题
     * @param content   邮件内容
     */
    @Override
    public void sendSimpleMail(String from, String to, String cc, String subject, String content) throws Exception {
        SimpleMailMessage simpMsg =new SimpleMailMessage() ;
        simpMsg.setFrom(from);
        simpMsg.setTo(to);
        if (!StringUtils.isEmpty(cc)){
            simpMsg.setCc(cc);
        }
        simpMsg.setSubject(subject);
        simpMsg.setText(content);
        javaMailSender.send(simpMsg);
        //sendAttachFileMail(from,to,cc,subject,content,null);
    }

    /**
     *
     * @param from  发送者
     * @param to    收件人
     * @param cc    抄送人
     * @param subject   邮件主题
     * @param content   邮件内容
     * @param file  附件
     */
    @Override
    public void sendAttachFileMail(String from, String to, String cc, String subject, String content, File file) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setFrom(from);
        helper.setTo(to);
        if (!StringUtils.isEmpty(cc)){
            helper.setCc(cc);
        }
        helper.setSubject(subject);
        helper.setText(content);
        if (null != file){
            helper.addAttachment(file.getName(),file);
        }
        javaMailSender.send(message);
    }

    /**
     * @param from  发送者
     * @param to    收件人
     * @param cc    抄送人
     * @param subject   邮件主题
     * @param content   邮件内容
     * @param srcPath   图片路径
     * @param resids    图片id
     * @throws Exception
     */
    @Override
    public void sendMailWithirng(String from, String to, String cc, String subject, String content, String[] srcPath, String[] resids) throws Exception {
        if (srcPath.length != resids.length){
            throw new Exception("发送失败");
        }
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setFrom(from);
        helper.setTo(to);
        if (!StringUtils.isEmpty(cc)){
            helper.setCc(cc);
        }
        helper.setSubject(subject);
        //true表示邮件正文是HTML格式的
        helper.setText(content,true);
        for (int i = 0; i < srcPath.length; i++) {
            FileSystemResource resource = new FileSystemResource(new File(srcPath[i]));
            helper.addInline(resids[i],resource);
        }
        javaMailSender.send(message);
    }

    /**
     * @param from  发送者
     * @param to    收件人
     * @param cc    抄送人
     * @param subject   邮件主题
     * @param params    模板参数
     * @param templatePath 模板路径
     */
    @Override
    public void sendHtmlMailThymeleaf(String from, String to, String cc, String subject, Map<String, Object> params, String templatePath) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setFrom(from);
        helper.setTo(to);
        if (!StringUtils.isEmpty(cc)){
            helper.setCc(cc);
        }
        helper.setSubject(subject);
        Context ctx = new Context();
        ctx.setVariables(params);
        String content=templateEngine.process(templatePath,ctx);
        //true表示邮件正文是HTML格式的
        helper.setText(content,true);
        javaMailSender.send(message);
    }

}
