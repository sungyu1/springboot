package jpabook.jpashop.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@Profile("oracle") // 추후 오라클 사용할 때만 이 설정 활성화
@EnableJpaRepositories(
        basePackages = "jpabook.jpashop.repository.oracle",
        entityManagerFactoryRef = "oracleEntityManager",
        transactionManagerRef = "oracleTransactionManager"
)
public class OracleDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource-oracle")
    public DataSource oracleDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean oracleEntityManager(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(oracleDataSource())
                .packages("jpabook.jpashop.entity.oracle")
                .persistenceUnit("oracle")
                .build();
    }

    @Bean
    public PlatformTransactionManager oracleTransactionManager(
            @Qualifier("oracleEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}


