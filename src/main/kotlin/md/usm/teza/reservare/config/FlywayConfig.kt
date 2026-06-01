package md.usm.teza.reservare.config

import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class FlywayConfig(private val dataSource: DataSource) {

    /**
     * Explicit Flyway bean — needed because Spring Boot 4 split its autoconfiguration
     * into per-concern modules and the Flyway module is not pulled in transitively by
     * spring-boot-starter-data-jpa.  Defining the bean here guarantees migration runs
     *
     * before Hibernate validates / uses the schema.
     *
     * The bean name "flyway" is recognised by Spring Boot's JPA autoconfiguration, which
     * declares a [org.springframework.boot.orm.jpa.EntityManagerFactoryDependsOnPostProcessor]
     * that makes the EntityManagerFactory wait for any bean named "flyway" to be fully
     * initialised — so ordering is automatic.
     */
    @Bean(initMethod = "migrate")
    fun flyway(): Flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .baselineVersion("0")
        .validateOnMigrate(true)
        .load()
}
