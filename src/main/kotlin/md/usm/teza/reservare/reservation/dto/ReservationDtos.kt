package md.usm.teza.reservare.reservation.dto

import md.usm.teza.reservare.reservation.ReservationStatus
import md.usm.teza.reservare.reservation.ReservationType
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

data class BookRoomRequest(
    val roomId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val notes: String? = null,
)

data class BookSeatRequest(
    val roomId: Long,
    val seatId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val notes: String? = null,
)

data class ReviewRequest(
    val note: String? = null,
)

data class ReservationResponse(
    val id: UUID,
    val userId: UUID,
    val roomId: Long,
    val roomName: String,
    val seatId: Long?,
    val seatLabel: String?,
    val type: ReservationType,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val seatsRequested: Int,
    val status: ReservationStatus,
    val notes: String?,
    val reviewedBy: UUID?,
    val reviewNote: String?,
    val createdAt: Instant,
)
