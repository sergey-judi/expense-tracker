package com.expensetracker.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@Configuration
public class DockerPostgresConfig {

  private static final String DB_NAME = "expense-tracker";
  private static final String POSTGRES_USER = "postgres";
  private static final String POSTGRES_PASSWORD = "password";

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