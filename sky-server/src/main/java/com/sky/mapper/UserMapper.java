package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    // 根据openid查询用户
    @Select("select * from user where openid=#{openid}")
    User getByOpenId(String openid);

    // 由于插入数据后需要得到主键值，所以用xml动态sql实现
    void insert(User user);

    @Select("select * from user where id=#{userId}")
    User getById(Long userId);


    /**
     * 根据动态条件统计用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}






























