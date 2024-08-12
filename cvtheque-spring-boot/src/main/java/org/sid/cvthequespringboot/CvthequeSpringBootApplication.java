package org.sid.cvthequespringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;


//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) pour désactiver la connexion à la base de données
@SpringBootApplication
@EnableAsync
public class CvthequeSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(CvthequeSpringBootApplication.class, args);
    }

}
