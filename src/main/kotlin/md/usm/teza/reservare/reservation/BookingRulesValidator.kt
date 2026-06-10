package md.usm.teza.reservare.reservation

import md.usm.teza.reservare.common.exception.BadRequestException
import md.usm.teza.reservare.common.exception.ConflictException
import md.usm.teza.reservare.room.Seat
import md.usm.teza.reservare.room.StudyRoom
import md.usm.teza.reservare.user.RoleName
import md.usm.teza.reservare.user.UserRoleRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Component
class BookingRulesValidator(
    private val reservationRepo: ReservationRepository,
    private val userRoleRepo: UserRoleRepository,
) {
    private val activeStatuses = listOf(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)

    fun validateSlot(start: LocalDateTime, end: LocalDateTime) {
        if (!end.isAfter(start)) throw BadRequestException("end must be after start")

        // 30-minute alignment
        if (start.minute % 30 != 0 || start.second != 0 || start.nano != 0)
            throw BadRequestException("startDateTime must be aligned to 30-minute boundaries")
        if (end.minute % 30 != 0 || end.second != 0 || end.nano != 0)
            throw BadRequestException("endDateTime must be aligned to 30-minute boundaries")

        // Max 2-day duration
        if (ChronoUnit.MINUTES.between(start, end) > 2 * 24 * 60)
            throw BadRequestException("Reservation duration cannot exceed 2 days")
    }

    fun validateRoomBooking(userId: UUID, room: StudyRoom, start: LocalDateTime, end: LocalDateTime) {
        validateSlot(start, end)

        val role = userRoleRepo.findByUserId(userId)?.role?.name

        if (role == RoleName.TEACHER) {
            // Max capacity 200
            if (room.capacity > 200) throw BadRequestException("Teachers can only book rooms with capacity ≤ 200")

            // 30-day advance window
            val maxAdvance = LocalDateTime.now().plusDays(30)
            if (start.isAfter(maxAdvance)) throw BadRequestException("Teachers can book at most 30 days in advance")

            // Max 2 concurrent active room reservations
            val activeCount = reservationRepo.countByUserIdAndTypeAndStatusIn(userId, ReservationType.ROOM, activeStatuses)
            if (activeCount >= 2) throw BadRequestException("Teachers can have at most 2 active room reservations")
        }

        // Check for conflicts
        checkNoConflict(room.id, start, end)
    }

    fun validateSeatBooking(userId: UUID, room: StudyRoom, seat: Seat, start: LocalDateTime, end: LocalDateTime) {
        validateSlot(start, end)

        val role = userRoleRepo.findByUserId(userId)?.role?.name

        if (role == RoleName.STUDENT) {
            // 14-day advance window
            val maxAdvance = LocalDateTime.now().plusDays(14)
            if (start.isAfter(maxAdvance)) throw BadRequestException("Students can book at most 14 days in advance")

            // Max 1 active seat reservation
            val activeCount = reservationRepo.countByUserIdAndStatusIn(userId, activeStatuses)
            if (activeCount >= 1) throw BadRequestException("Students can have at most 1 active seat reservation")
        }

        // Conflict if the whole room is booked, or this specific seat is already taken
        val conflicts = reservationRepo.findSeatConflicting(room.id, seat.id, start, end, activeStatuses)
        if (conflicts.isNotEmpty()) throw ConflictException("Seat ${seat.label} is not available for the requested time slot")
    }

    private fun checkNoConflict(roomId: Long, start: LocalDateTime, end: LocalDateTime) {
        val conflicts = reservationRepo.findConflicting(roomId, start, end, activeStatuses)
        if (conflicts.isNotEmpty()) throw ConflictException("Room is not available for the requested time slot")
    }
}
