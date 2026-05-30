package md.usm.teza.reservare.user

import jakarta.persistence.*

enum class PermissionName {
    ALL,
    ROOM_READ, ROOM_WRITE, ROOM_BOOK,
    SEAT_BOOK,
    RESERVATION_READ_OWN, RESERVATION_READ_ALL,
    RESERVATION_CANCEL_OWN, RESERVATION_MANAGE,
    USER_READ, USER_MANAGE,
}

@Entity
@Table(name = "permissions")
class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    val name: PermissionName,

    val description: String? = null,
)
