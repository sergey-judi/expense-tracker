package com.expensetracker.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@Configuration
public class DockerPostgresConfig {

  @Value("${spring.datasource.name}")
  private String DB_NAME;

  @Value("${spring.datasource.username}")
  private String POSTGRES_USER;

  @Value("${spring.datasource.password}")
  private String POSTGRES_PASSWORD;

  @Bean(destroyMethod = "stop")
  public PostgreSQLContainer postgreSQLContainer() {
    PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:13.3")
        .withDatabaseName(DB_NAME)
        .withUsername(POSTGRES_USER)
        .withPassword(POSTGRES_PASSWORD);

    postgres.start();

    return postgres;
  }

  @Primary
  @Bean(name = "dataSource")
  public DataSource dataSource(PostgreSQLContainer postgres) {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(postgres.getJdbcUrl());
    hikariConfig.setUsername(postgres.getUsername());
    hikariConfig.setPassword(postgres.getPassword());

    return new HikariDataSource(hikariConfig);
  }
}