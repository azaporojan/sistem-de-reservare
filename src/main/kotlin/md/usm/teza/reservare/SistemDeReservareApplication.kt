package md.usm.teza.reservare

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import md.usm.teza.reservare.config.AppProperties

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AppProperties::class)
class SistemDeReservareApplication

fun main(args: Array<String>) {
    runApplication<SistemDeReservareApplication>(*args)
}
