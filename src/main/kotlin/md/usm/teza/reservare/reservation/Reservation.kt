package md.usm.teza.reservare.reservation

import jakarta.persistence.*
import md.usm.teza.reservare.room.Seat
import md.usm.teza.reservare.room.StudyRoom
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

enum class ReservationType { ROOM, SEAT }

enum class ReservationStatus { PENDING, CONFIRMED, COMPLETED, DECLINED, CANCELLED }

@Entity
@Table(name = "reservations")
class Reservation(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: StudyRoom,

    /** The concrete seat for SEAT reservations; null for ROOM reservations. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    val seat: Seat? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: ReservationType,

    @Column(name = "start_date_time", nullable = false)
    val startDateTime: LocalDateTime,

    @Column(name = "end_date_time", nullable = false)
    val endDateTime: LocalDateTime,

    @Column(name = "seats_requested", nullable = false)
    val seatsRequested: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReservationStatus = ReservationStatus.PENDING,

    var notes: String? = null,

    @Column(name = "reviewed_by")
    var reviewedBy: UUID? = null,

    @Column(name = "review_note")
    var reviewNote: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
