package com.lll.dao;

import com.lll.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    // 分页查询
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);

    // 发布帖子
    int addDiscussPost(DiscussPost discussPost);

    // 查看帖子详情信息
    DiscussPost selectDiscussPostById(int id);

    //更新帖子总数
    int updateCommentCount(int id, int commentCount);

    // 更改帖子类型
    int updateType(int id, int type);

    // 更改帖子状态
    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
