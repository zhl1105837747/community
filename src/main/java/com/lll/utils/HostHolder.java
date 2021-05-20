package com.lll.utils;

import com.lll.entity.User;
import org.springframework.stereotype.Component;

/**
 * 储存用户数据
 * 相当于session容器
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    //清理user
    public void clear(){
        users.remove();
    }
}
