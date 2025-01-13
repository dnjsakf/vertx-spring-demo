package com.dms.apps.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource") 
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "postgresDataSource")
    @ConfigurationProperties("spring.datasource.postgres")
    public DataSource postgresDataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "h2DataSource")
    @ConfigurationProperties("spring.datasource.h2")
    public DataSource h2DataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }

}
