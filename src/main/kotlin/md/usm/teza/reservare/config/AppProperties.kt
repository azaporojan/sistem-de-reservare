package md.usm.teza.reservare.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.jwt")
class AppProperties {
    lateinit var secret: String
    var expirationMs: Long = 86_400_000L
}
