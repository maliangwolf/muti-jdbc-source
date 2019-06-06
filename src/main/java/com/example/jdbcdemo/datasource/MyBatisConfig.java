package com.example.jdbcdemo.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过读取application.properties文件生成两个数据源（myTestDbDataSource、myTestDb2DataSource）
 * 使用以上生成的两个数据源构造动态数据源dataSource
 * @Primary：指定在同一个接口有多个实现类可以注入的时候，默认选择哪一个，而不是让@Autowire注解报错（一般用于多数据源的情况下）
 * @Qualifier：指定名称的注入，当一个接口有多个实现类的时候使用（在本例中，有两个DataSource类型的实例，需要指定名称注入）
 * @Bean：生成的bean实例的名称是方法名（例如上边的@Qualifier注解中使用的名称是前边两个数据源的方法名，而这两个数据源也是使用@Bean注解进行注入的）
 * 通过动态数据源构造SqlSessionFactory和事务管理器（如果不需要事务，后者可以去掉）
 */
@Configuration
@MapperScan(basePackages = "com.example.jdbcdemo.mapper")
public class MyBatisConfig {
    @Autowired
    private Environment env;

    @Bean
    @Primary
    public DataSource mysqlDataSource() throws Exception {
        DataSourceBuilder builder=DataSourceBuilder.create();
        System.out.println(env.getProperty("mysqljdbc.driverClassName"));
        DataSource dataSource=builder.driverClassName(env.getProperty("mysqljdbc.driverClassName"))
                .url(env.getProperty("mysqljdbc.url"))
                .username(env.getProperty("mysqljdbc.username"))
                .password(env.getProperty("mysqljdbc.password")).build();
//        System.out.println(dataSource.getConnection()==null);
//        Connection connection=dataSource.getConnection();
//        Statement statement=connection.createStatement();
//        ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE id = 1");
//        while (resultSet.next()) {
//            System.out.println(resultSet.getString(2));
//        }
//        connection.close();
       // System.out.println(resultSet.getString(1));
        return dataSource;
    }
    @Bean
    public DataSource oracleDataSource() throws Exception {
        DataSourceBuilder builder=DataSourceBuilder.create();
        System.out.println("oracleDataSource");
//        return builder.driverClassName(env.getProperty("mysqljdbc.driverClassName"))
//                .url(env.getProperty("mysqljdbc.url"))
//                .username(env.getProperty("mysqljdbc.username"))
//                .password(env.getProperty("mysqljdbc.password")).build();
        DataSource dataSource=builder.driverClassName(env.getProperty("oraclejdbc.driverClassName"))
                .url(env.getProperty("oraclejdbc.url"))
                .username(env.getProperty("oraclejdbc.username"))
                .password(env.getProperty("oraclejdbc.password")).build();

//        Connection connection=dataSource.getConnection();
//        Statement statement=connection.createStatement();
//        ResultSet resultSet = statement.executeQuery("SELECT * FROM secu_user WHERE code='ddjr01' ");
//        while (resultSet.next()) {
//            System.out.println(resultSet.getString(2));
//        }
//        connection.close();
        return dataSource;
    }

    /**
     * @Primary 该注解表示在同一个接口有多个实现类可以注入的时候，默认选择哪一个，而不是让@autowire注解报错
     * @Qualifier 根据名称进行注入，通常是在具有相同的多个类型的实例的一个注入（例如有多个DataSource类型的实例）
     */
    @Bean
    public DynamicDataSource primDataSource(@Qualifier("mysqlDataSource") DataSource mysqlDataSource,
                                        @Qualifier("oracleDataSource") DataSource oracleDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DatabaseType.mysqlDataSource, mysqlDataSource);
        targetDataSources.put(DatabaseType.oracleDataSource, oracleDataSource);

        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);// 该方法是AbstractRoutingDataSource的方法
        dataSource.setDefaultTargetDataSource(mysqlDataSource);// 默认的datasource设置为myTestDbDataSource

        return dataSource;
    }

    /**
     * 根据数据源创建SqlSessionFactory
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DynamicDataSource ds) throws Exception {
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setDataSource(ds);// 指定数据源(这个必须有，否则报错)
        // 下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
       // fb.setTypeAliasesPackage(env.getProperty("mybatis.typeAliasesPackage"));// 指定基包
       // fb.setMapperLocations(
       //         new PathMatchingResourcePatternResolver().getResources(env.getProperty("mybatis.mapperLocations")));//

        return fb.getObject();
    }

    /**
     * 配置事务管理器
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DynamicDataSource dataSource) throws Exception {
        return new DataSourceTransactionManager(dataSource);
    }

}
