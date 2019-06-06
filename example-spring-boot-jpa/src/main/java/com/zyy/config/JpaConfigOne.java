package com.zyy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
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
@EnableJpaRepositories(basePackages = "com.zyy.dao",
        entityManagerFactoryRef = "entityManagerFactoryBeanOne",
        transactionManagerRef = "platformTransactionManagerOne")
public class JpaConfigOne {
    @Resource(name = "dsOne")
    DataSource dataSource;
    @Autowired
    JpaProperties jpaProperties;

    /**
     * 首先配直数据源，然后设直 JPA 相关配直（ JpaProperties 由系
     *  统自动加载），再设直实体类所在的位直，最后配直持久化单元名，若项目中只有一个
     *  EntityManagerFactory 则 persistenceUnit 可以省咯拌，若有多个，则必须明确指定持久化单
     *  元名
     *
     * Primary注解表示存在多个实例时该实例将被优先
     * @return
     */
    @Bean
    @Primary    //必须加上此注解不然启动报错
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBeanOne(EntityManagerFactoryBuilder builder){
        return builder.dataSource(dataSource)
                .properties(jpaProperties.getProperties())
                .packages("com.zyy.entity")
                .persistenceUnit("pu1")
                .build();
    }

    /**
     * 创建事务管理器
     * @param builder
     * @return
     */
    @Bean
    @Primary    //必须加上此注解不然启动报错
    PlatformTransactionManager platformTransactionManagerOne(EntityManagerFactoryBuilder builder){
        LocalContainerEntityManagerFactoryBean factoryBean = entityManagerFactoryBeanOne(builder);
        return new JpaTransactionManager(factoryBean.getObject());
    }

    @Bean
    @Primary    //必须加上此注解不然启动报错
    public EntityManager entityManagerOne(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryBeanOne(builder).getObject().createEntityManager();
    }
}
