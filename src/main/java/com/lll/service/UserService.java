package com.lll.service;

import com.lll.dao.LoginTicketMapper;
import com.lll.dao.UserMapper;
import com.lll.entity.LoginTicket;
import com.lll.entity.User;
import com.lll.utils.CommunityUtil;
import com.lll.utils.Constant;
import com.lll.utils.MailUtil;
import com.lll.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements Constant {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private TemplateEngine templateEngine;
    /*    @Autowired
        private LoginTicketMapper loginTicketMapper;
    */
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;


    public User findUserById(int id) {
        //   return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("userNameMsg", "帐号不能为空！！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！！");
            return map;
        }
        // 验证帐号
        User uN = userMapper.selectByName(user.getUsername());
        if (uN != null) {
            map.put("userNameMsg", "该帐号已存在！！");
            return map;
        }
        // 邮箱
        User uE = userMapper.selectByEmail(user.getEmail());
        if (uE != null) {
            map.put("emailMsg", "该邮箱已存在！！");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.getUUID().substring(0, 5));
        user.setPassword(CommunityUtil.MD5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.getUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailUtil.sendMail(user.getEmail(), "激活您的帐号", content);
        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        // 空值判断处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "帐号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        // 合法性验证
        // 帐号验证
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该帐号不存在！");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该帐号未激活！");
            return map;
        }
        // 密码验证
        password = CommunityUtil.MD5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确！");
            return map;
        }
        // 生成登入凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.getUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        /*loginTicketMapper.addLoginTicket(loginTicket);*/
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        /*loginTicketMapper.updateStatus(ticket, 1);*/
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    // 查找ticket凭证
    public LoginTicket findLoginTicket(String ticket) {
        /*return loginTicketMapper.selectByTicket(ticket);*/
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl) {
        //    return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    public Map<String, Object> updatePassword(User user, String oldPassword, String newPassword, String confirmPassword) {
        Map<String, Object> map = new HashMap<>();
        // 检验原密码是否正确
        oldPassword = CommunityUtil.MD5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "原密码不正确！！");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "密码不能为空!");
            return map;
        }
        if (!newPassword.equals(confirmPassword)) {
            map.put("confirmPasswordMsg", "两次输入密码不一致！！");
            return map;
        }
        int id = user.getId();
        newPassword = CommunityUtil.MD5(newPassword + user.getSalt());
        if (oldPassword.equals(newPassword)) {
            map.put("newPasswordMsg", "旧密码与新密码一致!");
            return map;
        }
        userMapper.updatePassword(id, newPassword);
        return map;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    // 2.取不到值时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    //查询某个用户的权限
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_MODERATOR;
                    case 2:
                        return AUTHORITY_ADMIN;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }

}
