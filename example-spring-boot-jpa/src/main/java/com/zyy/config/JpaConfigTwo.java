package com.zyy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * @Author:zhouyuyang
 * @Description:    entityManagerFactoryRef 用来指定实体类管理工厂 Bean 的名称，
 *              transactionManagerRef 用来指定事务管理器的引用名称
 * @Date: Created in 11:17 2019/5/29
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.zyy.dao2",
        entityManagerFactoryRef = "entityManagerFactoryBeanTwo",
        transactionManagerRef = "platformTransactionManagerTwo")
public class JpaConfigTwo {
    @Resource(name = "dsTwo")
    DataSource dataSource;
    @Autowired
    JpaProperties jpaProperties;

    /**
     * 首先配直数据源，然后设直 JPA 相关配直（ JpaProperties 由系
     *  统自动加载），再设直实体类所在的位直，最后配直持久化单元名，若项目中只有一个
     *  EntityManagerFactory 则 persistenceUnit 可以省咯拌，若有多个，则必须明确指定持久化单
     *  元名
     * @return
     */
    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBeanTwo(EntityManagerFactoryBuilder builder){
        return builder.dataSource(dataSource)
                .properties(jpaProperties.getProperties())
                .packages("com.zyy.entity")
                .persistenceUnit("pu2")
                .build();
    }

    /**
     * 创建事务管理器
     * @param builder
     * @return
     */
    @Bean
    PlatformTransactionManager platformTransactionManagerTwo(EntityManagerFactoryBuilder builder){
        LocalContainerEntityManagerFactoryBean factoryBean = entityManagerFactoryBeanTwo(builder);
        return new JpaTransactionManager(factoryBean.getObject());
    }

    @Bean
    public EntityManager entityManagerOne(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryBeanTwo(builder).getObject().createEntityManager();
    }
}
