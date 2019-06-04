package com.zyy.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 17:51 2019/5/27
 */
@Configuration
@MapperScan(basePackages = "com.zyy.mapper2", sqlSessionFactoryRef = "sqlSessionFactoryBean2")
public class MyBatisConfigTwo {
    @Autowired
    @Qualifier("dsTwo")
    DataSource dataSource;
    @Autowired
    private Environment env;

    @Bean
    SqlSessionFactory sqlSessionFactoryBean2() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean=new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        //必须配置，不配置找不到mapper.xml
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver.getResources(env.getProperty("mybatis.mapper-locations")));
        sqlSessionFactoryBean.setConfigLocation(resourcePatternResolver.getResource(env.getProperty("mybatis.config-location")));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    SqlSessionTemplate sqlSessionTemplate2() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactoryBean2());
    }

    /**
     * 配置事务管理器
     */
    @Bean
    public DataSourceTransactionManager transactionManagerTwo() throws Exception {
        return new DataSourceTransactionManager(dataSource);
    }
}
