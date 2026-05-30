package md.usm.teza.reservare.user.dto

import java.time.Instant
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val email: String,
    val fullName: String,
    val role: String,
    val enabled: Boolean,
    val permissions: List<String>,
    val createdAt: Instant,
)

data class UpdateProfileRequest(
    val fullName: String?,
    val password: String?,
)

data class CreateUserRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val role: String,
)

data class UpdateUserRequest(
    val fullName: String?,
    val password: String?,
    val enabled: Boolean?,
    val role: String?,
)

data class GrantPermissionRequest(
    val permission: String,
)
