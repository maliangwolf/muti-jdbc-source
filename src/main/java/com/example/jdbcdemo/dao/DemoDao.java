package com.example.jdbcdemo.dao;
import com.example.jdbcdemo.datasource.DatabaseContextHolder;
import com.example.jdbcdemo.datasource.DatabaseType;
import com.example.jdbcdemo.datasource.controller.domain.Users;
import com.example.jdbcdemo.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
@Repository
public class DemoDao {
    @Autowired
    private DemoMapper demoMapper;

    public Users getUsers(int id) {
        DatabaseContextHolder.setDatabaseType(DatabaseType.oracleDataSource);
        return demoMapper.getUser2(id);
    }
}
