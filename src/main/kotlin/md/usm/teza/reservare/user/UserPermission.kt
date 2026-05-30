package md.usm.teza.reservare.user

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "user_permissions")
class UserPermission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    val permission: Permission,
)
