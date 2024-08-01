package org.sid.cvthequespringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) pour désactiver la connexion à la base de données
@SpringBootApplication
public class CvthequeSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(CvthequeSpringBootApplication.class, args);
    }

}
