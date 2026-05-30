package md.usm.teza.reservare.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import md.usm.teza.reservare.config.AppProperties
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(private val props: AppProperties) {

    private val signingKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(props.secret.toByteArray())
    }

    fun generateToken(
        userId: String,
        email: String,
        fullName: String,
        role: String,
        permissions: List<String>,
    ): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId)
            .claim("email", email)
            .claim("fullName", fullName)
            .claim("role", role)
            .claim("permissions", permissions)
            .issuedAt(now)
            .expiration(Date(now.time + props.expirationMs))
            .signWith(signingKey)
            .compact()
    }

    fun extractUserId(token: String): String = claims(token).subject

    fun extractPermissions(token: String): List<String> {
        @Suppress("UNCHECKED_CAST")
        return claims(token)["permissions"] as? List<String> ?: emptyList()
    }

    fun isTokenValid(token: String): Boolean = runCatching {
        claims(token).expiration.after(Date())
    }.getOrDefault(false)

    private fun claims(token: String): Claims =
        Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
}
