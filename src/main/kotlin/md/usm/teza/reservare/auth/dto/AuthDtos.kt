package md.usm.teza.reservare.auth.dto

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val role: String = "STUDENT",
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class RefreshRequest(
    val refreshToken: String,
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
)
