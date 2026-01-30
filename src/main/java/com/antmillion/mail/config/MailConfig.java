package com.antmillion.mail.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:db.properties")
public class MailConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public JavaMailSender javaMailSender(
            @Value("${mail.host}") String host,
            @Value("${mail.port}") int port,
            @Value("${mail.username}") String username,
            @Value("${mail.password}") String password,
            @Value("${mail.debug:false}") boolean debug,

            @Value("${mail.smtp.auth:true}") boolean auth,
            @Value("${mail.smtp.starttls.enable:true}") boolean starttlsEnable,
            @Value("${mail.smtp.starttls.required:true}") boolean starttlsRequired,
            @Value("${mail.smtp.ssl.trust:smtp.gmail.com}") String sslTrust
    ) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);
        sender.setDefaultEncoding("UTF-8");

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", String.valueOf(auth));
        props.put("mail.smtp.starttls.enable", String.valueOf(starttlsEnable));
        props.put("mail.smtp.starttls.required", String.valueOf(starttlsRequired));
        props.put("mail.smtp.ssl.trust", sslTrust);
        props.put("mail.debug", String.valueOf(debug));

        return sender;
    }
}
