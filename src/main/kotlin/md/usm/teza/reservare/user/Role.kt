package md.usm.teza.reservare.user

import jakarta.persistence.*

enum class RoleName { ADMIN, TEACHER, STUDENT }

@Entity
@Table(name = "roles")
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    val name: RoleName,
)
