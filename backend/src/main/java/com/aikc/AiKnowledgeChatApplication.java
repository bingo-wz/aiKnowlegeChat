package com.aikc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * AI Knowledge Chat Application
 * Online Classroom & Research Room with AI Assistant
 *
 * @author AIKC Team
 * @since 1.0.0
 */
@SpringBootApplication
@EnableAsync
public class AiKnowledgeChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiKnowledgeChatApplication.class, args);
    }
}
