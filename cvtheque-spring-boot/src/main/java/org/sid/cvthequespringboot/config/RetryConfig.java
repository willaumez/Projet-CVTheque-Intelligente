package org.sid.cvthequespringboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Configuration du délai d'attente entre les tentatives
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(5000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // Politique de retry simple (nombre maximum de tentatives)
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        //retryPolicy.setMaxAttempts(); // Nombre maximum de tentatives
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
