package md.usm.teza.reservare.config

import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class FlywayConfig(private val dataSource: DataSource) {

    // Spring Boot 4 no longer auto-configures Flyway unless the dedicated
    // flyway autoconfigure module is on the classpath. This bean fills that gap.
    @Bean(initMethod = "migrate")
    fun flyway(): Flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .baselineVersion("0")
        .load()
}
