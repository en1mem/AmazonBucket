package ru.german.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import java.util.Properties;

@Configuration
public class DbConfiguration {

    @Value("${dbHost}")
    private String host;

    @Value("${dbUser}")
    private String user;

    @Value("${dbPassword}")
    private String password;

    @Value("${databaseName}")
    private String databaseName;

    @Value("${dbPortNumber}")
    private String portNumber;

    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource()));
    }

    @Bean
    @Primary
    public DSLContext dslContext() {
        return DSL.using(connectionProvider(), SQLDialect.POSTGRES_9_5);
    }

    @Bean(destroyMethod = "close")
    @Primary
    public HikariDataSource dataSource() {
        final HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setMaximumPoolSize(15);
        hikariDataSource.setMinimumIdle(10);
        hikariDataSource.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");

        Properties properties = new Properties();
        properties.put("user", user);
        properties.put("password", password);
        properties.put("databaseName", databaseName);
        properties.put("serverName", host);
        properties.put("portNumber", portNumber);
        hikariDataSource.setDataSourceProperties(properties);

        return hikariDataSource;
    }

    @Bean
    public org.jooq.Configuration configuration() {
        return new DefaultConfiguration().set(connectionProvider()).set(SQLDialect.POSTGRES_9_5);
    }

}
