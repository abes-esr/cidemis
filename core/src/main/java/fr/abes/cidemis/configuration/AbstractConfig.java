package fr.abes.cidemis.configuration;

import fr.abes.cidemis.constant.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.HashMap;

public abstract class AbstractConfig {
    @Value("${spring.jpa.show-sql}")
    protected String showsql;
    @Value("${spring.jpa.properties.hibernate.dialect}")
    protected String dialect;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    protected String ddlAuto;

    protected void configHibernate(LocalContainerEntityManagerFactoryBean em) {
        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.show_sql", showsql);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.dialect", dialect);
        em.setJpaPropertyMap(properties);
    }

    @Bean
    public Constant constant() {
        return new Constant();
    }

}
