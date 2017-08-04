package com.zboigor.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.zboigor.db.CamelCase2UnderscoreNamingStrategy;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Igor Zboichik
 * @since 2017-03-11
 */
@Configuration
@EnableJpaRepositories("com.zboigor.repository")
@EnableTransactionManagement
public class JpaConfig {

    private final AppProperties appProperties;

    @Autowired
    public JpaConfig(AppProperties appProperties) {this.appProperties = appProperties;}

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(appProperties.getDb().getUrl());
        dataSource.setUsername(appProperties.getDb().getUsername());
        dataSource.setPassword(appProperties.getDb().getPassword());

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory());
        return txManager;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        // will set the provider to 'org.hibernate.ejb.HibernatePersistence'
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        // will set hibernate.show_sql to 'true'
        vendorAdapter.setShowSql(true);
        // if set to true, will set hibernate.hbm2ddl.auto to 'update'
        vendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.zboigor.model");
        factory.setDataSource(dataSource());
        Properties properties = new Properties();
        properties.put("hibernate.physical_naming_strategy", CamelCase2UnderscoreNamingStrategy.class.getName());
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        factory.setJpaProperties(properties);

        // This will trigger the creation of the entity manager factory
        factory.afterPropertiesSet();

        return factory.getObject();
    }
}
