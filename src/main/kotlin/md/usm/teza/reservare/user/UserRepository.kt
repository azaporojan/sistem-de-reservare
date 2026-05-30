package md.usm.teza.reservare.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}

interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: RoleName): Role?
}

interface UserRoleRepository : JpaRepository<UserRole, Long> {
    fun findByUserId(userId: UUID): UserRole?
}

interface PermissionRepository : JpaRepository<Permission, Long> {
    fun findByName(name: PermissionName): Permission?
}

interface RolePermissionRepository : JpaRepository<RolePermission, Long> {
    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.role.id = :roleId")
    fun findPermissionsByRoleId(roleId: Long): List<Permission>
}

interface UserPermissionRepository : JpaRepository<UserPermission, Long> {
    @Query("SELECT up.permission FROM UserPermission up WHERE up.userId = :userId")
    fun findPermissionsByUserId(userId: UUID): List<Permission>

    fun deleteByUserIdAndPermissionId(userId: UUID, permissionId: Long)

    fun existsByUserIdAndPermissionId(userId: UUID, permissionId: Long): Boolean
}
