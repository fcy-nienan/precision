package com.cpiclife.precisionMarketing;

import com.cpiclife.precisionMarketing.servlet.PrecisionServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PrecisionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrecisionApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        return new ServletRegistrationBean(new PrecisionServlet(),"/precisionServlet.do");
    }
}
