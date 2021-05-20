package com.lll.dao;

import com.lll.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author 龙儿
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addLoginTicket(LoginTicket loginTicket);

    @Select({"select * from login_ticket where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update login_ticket set status=#{status} where ticket=#{ticket}"})
    int updateStatus(String ticket, int status);
}
