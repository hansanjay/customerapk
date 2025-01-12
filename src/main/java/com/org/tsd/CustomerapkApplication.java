package com.org.tsd;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootApplication
public class CustomerapkApplication {
	
	@Value("${mail.host}")
    private String host;
	@Value("${mail.port}")
    private String port;
	@Value("${mail.username}")
    private String username;
	@Value("${mail.password}")
    private String password;

	public static void main(String[] args) {
		SpringApplication.run(CustomerapkApplication.class, args);
	}
	
	@Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(port));
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
	
	@Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            if (factory instanceof org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory) {
                ((org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory) factory)
                        .setContextPath("/cj");
            }
        };
    }

}
