package com.dms.apps.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public abstract class MyBatisConfig {

    public static final String BASE_PACKAGE = "com.dms.apps";
    
    protected void configureSqlSessionFactory(SqlSessionFactoryBean sessionFactoryBean, DataSource dataSource, String mapperPath) throws IOException {
        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
        sessionFactoryBean.setDataSource(dataSource);
        // sessionFactoryBean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
        sessionFactoryBean.setMapperLocations(pathResolver.getResources(mapperPath));

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactoryBean.setConfiguration(configuration);
    }
}

@Configuration
@MapperScan(basePackages = MyBatisConfig.BASE_PACKAGE, sqlSessionFactoryRef = "postgresSqlSessionFactory")
class SqlSession extends MyBatisConfig {

    @Bean
    public SqlSessionFactory postgresSqlSessionFactory(@Qualifier("postgresDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        configureSqlSessionFactory(sessionFactoryBean, dataSource, "classpath:/mybatis/mapper/postgres/*.xml");
        return sessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionFactory h2SqlSessionFactory(@Qualifier("h2DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        configureSqlSessionFactory(sessionFactoryBean, dataSource, "classpath:/mybatis/mapper/h2/*.xml");
        return sessionFactoryBean.getObject();
    }
}