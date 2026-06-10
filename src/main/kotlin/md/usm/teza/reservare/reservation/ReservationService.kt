package md.usm.teza.reservare.reservation

import md.usm.teza.reservare.common.exception.BadRequestException
import md.usm.teza.reservare.common.exception.ForbiddenException
import md.usm.teza.reservare.common.exception.NotFoundException
import md.usm.teza.reservare.reservation.dto.*
import md.usm.teza.reservare.room.RoomRepository
import md.usm.teza.reservare.room.SeatRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ReservationService(
    private val reservationRepo: ReservationRepository,
    private val roomRepo: RoomRepository,
    private val seatRepo: SeatRepository,
    private val validator: BookingRulesValidator,
) {

    fun getAll(): List<ReservationResponse> = reservationRepo.findAll().map { it.toResponse() }

    fun getMy(userId: UUID): List<ReservationResponse> =
        reservationRepo.findByUserId(userId).map { it.toResponse() }

    fun getById(id: UUID, auth: Authentication): ReservationResponse {
        val reservation = reservationRepo.findById(id).orElseThrow { NotFoundException("Reservation not found: $id") }
        val callerId = UUID.fromString(auth.name)
        val hasReadAll = auth.authorities.any { it.authority in listOf("RESERVATION_READ_ALL", "ALL") }
        if (!hasReadAll && reservation.userId != callerId) throw ForbiddenException("Access denied")
        return reservation.toResponse()
    }

    @Transactional
    fun bookRoom(userId: UUID, req: BookRoomRequest, isAdmin: Boolean): ReservationResponse {
        val room = roomRepo.findById(req.roomId).orElseThrow { NotFoundException("Room not found: ${req.roomId}") }
        if (!room.active) throw BadRequestException("Room is not active")

        validator.validateRoomBooking(userId, room, req.startDateTime, req.endDateTime)

        val status = if (isAdmin) ReservationStatus.CONFIRMED else ReservationStatus.PENDING

        val reservation = reservationRepo.save(
            Reservation(
                userId = userId,
                room = room,
                type = ReservationType.ROOM,
                startDateTime = req.startDateTime,
                endDateTime = req.endDateTime,
                seatsRequested = room.capacity,
                status = status,
                notes = req.notes,
            )
        )
        return reservation.toResponse()
    }

    @Transactional
    fun bookSeat(userId: UUID, req: BookSeatRequest, isAdmin: Boolean): ReservationResponse {
        val room = roomRepo.findById(req.roomId).orElseThrow { NotFoundException("Room not found: ${req.roomId}") }
        if (!room.active) throw BadRequestException("Room is not active")

        val seat = seatRepo.findById(req.seatId).orElseThrow { NotFoundException("Seat not found: ${req.seatId}") }
        if (seat.room.id != room.id) throw BadRequestException("Seat ${req.seatId} does not belong to room ${req.roomId}")

        validator.validateSeatBooking(userId, room, seat, req.startDateTime, req.endDateTime)

        val status = if (isAdmin) ReservationStatus.CONFIRMED else ReservationStatus.PENDING

        val reservation = reservationRepo.save(
            Reservation(
                userId = userId,
                room = room,
                seat = seat,
                type = ReservationType.SEAT,
                startDateTime = req.startDateTime,
                endDateTime = req.endDateTime,
                seatsRequested = 1,
                status = status,
                notes = req.notes,
            )
        )
        return reservation.toResponse()
    }

    @Transactional
    fun confirm(id: UUID, reviewerId: UUID, req: ReviewRequest): ReservationResponse {
        val reservation = reservationRepo.findById(id).orElseThrow { NotFoundException("Reservation not found: $id") }
        if (reservation.status != ReservationStatus.PENDING)
            throw BadRequestException("Only PENDING reservations can be confirmed")
        reservation.status = ReservationStatus.CONFIRMED
        reservation.reviewedBy = reviewerId
        reservation.reviewNote = req.note
        return reservationRepo.save(reservation).toResponse()
    }

    @Transactional
    fun decline(id: UUID, reviewerId: UUID, req: ReviewRequest): ReservationResponse {
        val reservation = reservationRepo.findById(id).orElseThrow { NotFoundException("Reservation not found: $id") }
        if (reservation.status != ReservationStatus.PENDING)
            throw BadRequestException("Only PENDING reservations can be declined")
        reservation.status = ReservationStatus.DECLINED
        reservation.reviewedBy = reviewerId
        reservation.reviewNote = req.note
        return reservationRepo.save(reservation).toResponse()
    }

    @Transactional
    fun cancel(id: UUID, auth: Authentication): ReservationResponse {
        val reservation = reservationRepo.findById(id).orElseThrow { NotFoundException("Reservation not found: $id") }
        val callerId = UUID.fromString(auth.name)
        val hasManage = auth.authorities.any { it.authority in listOf("RESERVATION_MANAGE", "ALL") }
        val isOwner = reservation.userId == callerId

        if (!hasManage && !isOwner) throw ForbiddenException("Access denied")

        if (reservation.status !in listOf(ReservationStatus.PENDING, ReservationStatus.CONFIRMED))
            throw BadRequestException("Only PENDING or CONFIRMED reservations can be cancelled")

        if (reservation.startDateTime.isBefore(LocalDateTime.now()))
            throw BadRequestException("Cannot cancel a reservation that has already started")

        reservation.status = ReservationStatus.CANCELLED
        return reservationRepo.save(reservation).toResponse()
    }

    // ── mapping ──────────────────────────────────────────────

    private fun Reservation.toResponse() = ReservationResponse(
        id = id,
        userId = userId,
        roomId = room.id,
        roomName = room.name,
        seatId = seat?.id,
        seatLabel = seat?.label,
        type = type,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        seatsRequested = seatsRequested,
        status = status,
        notes = notes,
        reviewedBy = reviewedBy,
        reviewNote = reviewNote,
        createdAt = createdAt,
    )

}
