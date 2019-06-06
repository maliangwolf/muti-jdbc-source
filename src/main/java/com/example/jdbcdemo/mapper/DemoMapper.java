package com.example.jdbcdemo.mapper;

import com.example.jdbcdemo.datasource.controller.domain.Users;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Mapper
@Component(value = "demoMapper")
public interface DemoMapper {
    @Select("SELECT * FROM users WHERE id = #{i d}")
     @Results(value = { @Result(id = true, column = "id", property = "id"),
                                   @Result(column = "name", property = "name") })
     public Users getUser(@Param("id") int id);

    @Select("SELECT * FROM secu_user WHERE id = #{id}")
    @Results(value = { @Result(id = true, column = "id", property = "id"),
            @Result(column = "name", property = "name") })
    public Users getUser2(@Param("id") int id);


}
