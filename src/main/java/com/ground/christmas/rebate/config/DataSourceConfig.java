package com.ground.christmas.rebate.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(value = {JdbcProperties.class})
public class DataSourceConfig {


    @SuppressWarnings("unchecked")
    private static <T> T createDataSource(DataSourceProperties properties,
                                          Class<? extends DataSource> type) {
        return (T) properties.initializeDataSourceBuilder().type(type).build();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.incar")
    public DataSourceProperties incarDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public JdbcTemplate incarJdbcTemplate(JdbcProperties properties, @Qualifier("incarDataSource") DataSource dataSource) {
        return createJdbcTemplate(properties, dataSource);
    }

    @Bean
    public JdbcTemplate userJdbcTemplate(JdbcProperties properties, @Qualifier("userDataSource") DataSource dataSource) {
        return createJdbcTemplate(properties, dataSource);
    }

    private JdbcTemplate createJdbcTemplate(JdbcProperties properties, DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcProperties.Template template = properties.getTemplate();
        jdbcTemplate.setFetchSize(template.getFetchSize());
        jdbcTemplate.setMaxRows(template.getMaxRows());
        if (template.getQueryTimeout() != null) {
            jdbcTemplate.setQueryTimeout((int) template.getQueryTimeout().getSeconds());
        }
        return jdbcTemplate;
    }

    @Bean
    @Primary
    public PlatformTransactionManager incarTransactionManager(@Qualifier("incarDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public PlatformTransactionManager userTransactionManager(@Qualifier("userDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Primary
    public TransactionTemplate incarTransactionTemplate(@Qualifier("incarTransactionManager") PlatformTransactionManager platformTransactionManager) {
        return new TransactionTemplate(platformTransactionManager);
    }

    @Bean
    public TransactionTemplate userTransactionTemplate(@Qualifier("userTransactionManager") PlatformTransactionManager platformTransactionManager) {
        return new TransactionTemplate(platformTransactionManager);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.user")
    public DataSourceProperties userDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Hikari DataSource configuration.
     */
    @Configuration
    static class Hikari {

        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.incar.hikari")
        public HikariDataSource incarDataSource(@Qualifier("incarDataSourceProperties") DataSourceProperties properties) {
            HikariDataSource dataSource = createDataSource(properties, HikariDataSource.class);
            if (StringUtils.hasText(properties.getName())) {
                dataSource.setPoolName(properties.getName());
            }
            return dataSource;
        }

    }

    /**
     * Hikari DataSource configuration.
     */
    @Configuration
    static class BatchHikari {

        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.user.hikari")
        public HikariDataSource userDataSource(@Qualifier("userDataSourceProperties") DataSourceProperties properties) {
            HikariDataSource dataSource = createDataSource(properties, HikariDataSource.class);
            if (StringUtils.hasText(properties.getName())) {
                dataSource.setPoolName(properties.getName());
            }
            return dataSource;
        }

    }
}
