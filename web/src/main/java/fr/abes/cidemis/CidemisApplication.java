package fr.abes.cidemis;

import fr.abes.cidemis.configuration.CidemisContextListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletContextListener;

@SpringBootApplication
public class CidemisApplication extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CidemisApplication.class);
    }


    // Register ServletContextListener
    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> listenerRegistrationBean() {
        ServletListenerRegistrationBean<ServletContextListener> bean =
                new ServletListenerRegistrationBean<>();
        bean.setListener(new CidemisContextListener());
        return bean;

    }

    public static void main(String[] args) {
        SpringApplication.run(CidemisApplication.class, args);
    }

}
