package md.usm.teza.reservare.user

import md.usm.teza.reservare.common.exception.BadRequestException
import md.usm.teza.reservare.common.exception.ConflictException
import md.usm.teza.reservare.common.exception.NotFoundException
import md.usm.teza.reservare.user.dto.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val userRepo: UserRepository,
    private val roleRepo: RoleRepository,
    private val userRoleRepo: UserRoleRepository,
    private val rolePermRepo: RolePermissionRepository,
    private val userPermRepo: UserPermissionRepository,
    private val permRepo: PermissionRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun getAll(): List<UserResponse> = userRepo.findAll().map { it.toResponse() }

    fun getById(id: UUID): UserResponse = userRepo.findById(id)
        .orElseThrow { NotFoundException("User not found: $id") }
        .toResponse()

    fun getMe(userId: UUID): UserResponse = getById(userId)

    @Transactional
    fun updateMe(userId: UUID, req: UpdateProfileRequest): UserResponse {
        val user = userRepo.findById(userId).orElseThrow { NotFoundException("User not found") }
        req.fullName?.let { user.fullName = it }
        req.password?.let { user.password = passwordEncoder.encode(it)!! }
        return userRepo.save(user).toResponse()
    }

    @Transactional
    fun createUser(req: CreateUserRequest): UserResponse {
        if (userRepo.existsByEmail(req.email)) throw ConflictException("Email already taken")

        val roleName = runCatching { RoleName.valueOf(req.role.uppercase()) }
            .getOrElse { throw BadRequestException("Invalid role: ${req.role}") }
        val role = roleRepo.findByName(roleName) ?: throw BadRequestException("Role not found")

        val user = userRepo.save(
            User(email = req.email, password = passwordEncoder.encode(req.password)!!, fullName = req.fullName)
        )
        userRoleRepo.save(UserRole(userId = user.id, role = role))
        return user.toResponse()
    }

    @Transactional
    fun updateUser(id: UUID, req: UpdateUserRequest): UserResponse {
        val user = userRepo.findById(id).orElseThrow { NotFoundException("User not found: $id") }

        req.fullName?.let { user.fullName = it }
        req.password?.let { user.password = passwordEncoder.encode(it)!! }
        req.enabled?.let { user.enabled = it }

        req.role?.let { roleName ->
            val rn = runCatching { RoleName.valueOf(roleName.uppercase()) }
                .getOrElse { throw BadRequestException("Invalid role: $roleName") }
            val role = roleRepo.findByName(rn) ?: throw BadRequestException("Role not found")
            val existing = userRoleRepo.findByUserId(id)
            if (existing != null) userRoleRepo.delete(existing)
            userRoleRepo.save(UserRole(userId = id, role = role))
        }

        return userRepo.save(user).toResponse()
    }

    @Transactional
    fun deleteUser(id: UUID) {
        if (!userRepo.existsById(id)) throw NotFoundException("User not found: $id")
        userRepo.deleteById(id)
    }

    @Transactional
    fun grantPermission(userId: UUID, req: GrantPermissionRequest) {
        val permName = runCatching { PermissionName.valueOf(req.permission.uppercase()) }
            .getOrElse { throw BadRequestException("Unknown permission: ${req.permission}") }
        val perm = permRepo.findByName(permName) ?: throw NotFoundException("Permission not found")

        if (!userRepo.existsById(userId)) throw NotFoundException("User not found: $userId")

        if (!userPermRepo.existsByUserIdAndPermissionId(userId, perm.id)) {
            userPermRepo.save(UserPermission(userId = userId, permission = perm))
        }
    }

    @Transactional
    fun revokePermission(userId: UUID, req: GrantPermissionRequest) {
        val permName = runCatching { PermissionName.valueOf(req.permission.uppercase()) }
            .getOrElse { throw BadRequestException("Unknown permission: ${req.permission}") }
        val perm = permRepo.findByName(permName) ?: throw NotFoundException("Permission not found")
        userPermRepo.deleteByUserIdAndPermissionId(userId, perm.id)
    }

    // ── mapping ──────────────────────────────────────────────

    private fun User.toResponse(): UserResponse {
        val userRole = userRoleRepo.findByUserId(id)
        val role = userRole?.role
        val rolePerms = if (role != null) rolePermRepo.findPermissionsByRoleId(role.id).map { it.name.name } else emptyList()
        val extraPerms = userPermRepo.findPermissionsByUserId(id).map { it.name.name }
        val allPerms = if (PermissionName.ALL.name in rolePerms) listOf(PermissionName.ALL.name)
                       else (rolePerms + extraPerms).distinct()
        return UserResponse(
            id = id,
            email = email,
            fullName = fullName,
            role = role?.name?.name ?: "NONE",
            enabled = enabled,
            permissions = allPerms,
            createdAt = createdAt,
        )
    }
}
