package org.sid.cvthequespringboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

   /* @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/cv-theque-store");
        dataSource.setUsername("admin");
        dataSource.setPassword("password");
        return dataSource;
    }
@Bean
   public JdbcTemplate jdbcTemplate(DataSource dataSource) {
       return new JdbcTemplate(dataSource);
   }

    */

}
