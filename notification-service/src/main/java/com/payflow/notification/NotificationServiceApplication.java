package com.payflow.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    /**
     * @Bean = register RestTemplate as a Spring-managed object
     * Now we can inject it anywhere with @Autowired or constructor injection
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}