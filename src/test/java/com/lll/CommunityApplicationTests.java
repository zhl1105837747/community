package com.lll;

import com.lll.dao.LoginTicketMapper;
import com.lll.entity.LoginTicket;

import com.lll.utils.MailUtil;
import com.lll.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;

@SpringBootTest
class CommunityApplicationTests {

    // 邮件工具
    @Autowired
    private MailUtil mailUtil;
    // Template模板
    @Autowired
    private TemplateEngine templateEngine;
    // 登入状态
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    // 过滤敏感词
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    void contextLoads() {

    }

    /**
     * 测试邮件发送
     */
    @Test
    void MailTest() {
        mailUtil.sendMail("1493213639@qq.com", "测试", "Welcome!!");
    }

    @Test
    void HtmlMail() {
        Context context = new Context();
        context.setVariable("username", "王小贱");

        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);

        mailUtil.sendMail("1493213639@qq.com", "HTML测试", process);
    }

    @Test
    void LoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        int i = loginTicketMapper.addLoginTicket(loginTicket);
    }

    @Test
    void Ticket() {
        LoginTicket ticket = loginTicketMapper.selectByTicket("abc");
        System.out.println(ticket);
        int i = loginTicketMapper.updateStatus("abc", 1);
        ticket = loginTicketMapper.selectByTicket("abc");
        System.out.println(ticket);
    }

    @Test
    void sensitive() {
        String text = "这里可以赌博,可以嫖娼,可以吸毒,可以开票,哈哈哈!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "这里可以☆赌☆博☆,可以☆嫖☆娼☆,可以☆吸☆毒☆,可以☆开☆票☆,哈哈哈!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
    @Test
    void selectCountByEntity(){

    }
}
