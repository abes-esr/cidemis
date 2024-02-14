package fr.abes.cidemis.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "fr.abes.cidemis.dao.cidemis",
        entityManagerFactoryRef = "cidemisEntityManagerFactory",
        transactionManagerRef = "cidemisTransactionManager")
public class CidemisOracleConfig extends AbstractConfig {
    @Value("${cidemis.datasource.url}")
    private String url;
    @Value("${cidemis.datasource.username}")
    private String username;
    @Value("${cidemis.datasource.password}")
    private String password;
    @Value("${cidemis.datasource.driver-class-name}")
    private String driver;

    @Primary
    @Bean
    public DataSource cidemisDataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean cidemisEntityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(cidemisDataSource());
        em.setPackagesToScan(new String[]{"fr.abes.cidemis.model.cidemis"});
        configHibernate(em);
        return em;
    }

    @Primary
    @Bean
    public JpaTransactionManager cidemisTransactionManager(EntityManagerFactory entityManagerFactory) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Primary
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean(name = "cidemisJdbcTemplate")
    public JdbcTemplate cidemisJdbcTemplate() { return new JdbcTemplate(cidemisDataSource());}

}
