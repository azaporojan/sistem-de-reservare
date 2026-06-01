package md.usm.teza.reservare.auth

import md.usm.teza.reservare.auth.dto.AuthResponse
import md.usm.teza.reservare.auth.dto.LoginRequest
import md.usm.teza.reservare.auth.dto.RefreshRequest
import md.usm.teza.reservare.auth.dto.RegisterRequest
import md.usm.teza.reservare.common.exception.BadRequestException
import md.usm.teza.reservare.common.exception.UnauthorizedException
import md.usm.teza.reservare.security.JwtService
import md.usm.teza.reservare.user.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class AuthService(
    private val userRepo: UserRepository,
    private val roleRepo: RoleRepository,
    private val userRoleRepo: UserRoleRepository,
    private val rolePermRepo: RolePermissionRepository,
    private val userPermRepo: UserPermissionRepository,
    private val refreshTokenRepo: RefreshTokenRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
) {
    private val refreshTokenTtlDays: Long = 30

    @Transactional
    fun register(req: RegisterRequest): AuthResponse {
        if (userRepo.existsByEmail(req.email)) {
            throw BadRequestException("Email already registered")
        }

        val roleName = runCatching { RoleName.valueOf(req.role.uppercase()) }
            .getOrElse { throw BadRequestException("Invalid role: ${req.role}") }

        // Only STUDENT/TEACHER can self-register; ADMIN must be created by admin
        if (roleName == RoleName.ADMIN) throw BadRequestException("Cannot self-register as ADMIN")

        val role = roleRepo.findByName(roleName) ?: throw BadRequestException("Role not found: $roleName")

        val user = userRepo.save(
            User(
                email = req.email,
                password = passwordEncoder.encode(req.password)!!,
                fullName = req.fullName,
            )
        )

        userRoleRepo.save(UserRole(userId = user.id, role = role))

        return issueTokens(user, role)
    }

    @Transactional
    fun login(req: LoginRequest): AuthResponse {
        val user = userRepo.findByEmail(req.email)
            ?: throw UnauthorizedException("Invalid credentials")

        if (!user.enabled) throw UnauthorizedException("Account is disabled")

        if (!passwordEncoder.matches(req.password, user.password)) {
            throw UnauthorizedException("Invalid credentials")
        }

        val userRole = userRoleRepo.findByUserId(user.id)
            ?: throw UnauthorizedException("User has no role assigned")

        return issueTokens(user, userRole.role)
    }

    @Transactional
    fun refresh(req: RefreshRequest): AuthResponse {
        val stored = refreshTokenRepo.findByToken(req.refreshToken)
            ?: throw UnauthorizedException("Invalid refresh token")

        if (stored.revoked || stored.expiresAt.isBefore(Instant.now())) {
            // Revoke all tokens for this user on reuse attempt
            refreshTokenRepo.revokeAllByUserId(stored.userId)
            throw UnauthorizedException("Refresh token expired or revoked")
        }

        stored.revoked = true
        refreshTokenRepo.save(stored)

        val user = userRepo.findById(stored.userId).orElseThrow { UnauthorizedException("User not found") }
        val userRole = userRoleRepo.findByUserId(user.id) ?: throw UnauthorizedException("User has no role")

        return issueTokens(user, userRole.role)
    }

    // ── helpers ─────────────────────────────────────────────

    private fun issueTokens(user: User, role: Role): AuthResponse {
        val permissions = effectivePermissions(user.id, role)

        val accessToken = jwtService.generateToken(
            userId = user.id.toString(),
            email = user.email,
            fullName = user.fullName,
            role = role.name.name,
            permissions = permissions,
        )

        val rawRefresh = UUID.randomUUID().toString()
        refreshTokenRepo.save(
            RefreshToken(
                token = rawRefresh,
                userId = user.id,
                expiresAt = Instant.now().plusSeconds(refreshTokenTtlDays * 86_400),
            )
        )

        return AuthResponse(accessToken = accessToken, refreshToken = rawRefresh)
    }

    private fun effectivePermissions(userId: UUID, role: Role): List<String> {
        val rolePerms = rolePermRepo.findPermissionsByRoleId(role.id).map { it.name.name }
        // If role has ALL, return early
        if (PermissionName.ALL.name in rolePerms) return listOf(PermissionName.ALL.name)

        val extraPerms = userPermRepo.findPermissionsByUserId(userId).map { it.name.name }
        return (rolePerms + extraPerms).distinct()
    }
}
