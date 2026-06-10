package md.usm.teza.reservare.room

import md.usm.teza.reservare.common.exception.BadRequestException
import md.usm.teza.reservare.common.exception.ConflictException
import md.usm.teza.reservare.common.exception.NotFoundException
import md.usm.teza.reservare.reservation.ReservationRepository
import md.usm.teza.reservare.reservation.ReservationStatus
import md.usm.teza.reservare.room.dto.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.math.ceil
import kotlin.math.sqrt

@Service
class RoomService(
    private val roomRepo: RoomRepository,
    private val seatRepo: SeatRepository,
    private val reservationRepo: ReservationRepository,
) {
    private val activeStatuses = listOf(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)

    fun getAll(): List<RoomResponse> = roomRepo.findAllByActiveTrue().map { it.toResponse() }

    fun getById(id: Long): RoomResponse = roomRepo.findById(id)
        .orElseThrow { NotFoundException("Room not found: $id") }
        .toResponse()

    @Transactional
    fun create(req: CreateRoomRequest): RoomResponse {
        if (req.capacity <= 0) throw BadRequestException("Capacity must be > 0")
        if (roomRepo.existsByName(req.name)) throw ConflictException("Room name already exists")

        val room = roomRepo.save(
            StudyRoom(name = req.name, location = req.location, capacity = req.capacity, description = req.description)
        )
        generateSeats(room)
        return room.toResponse()
    }

    @Transactional
    fun update(id: Long, req: UpdateRoomRequest): RoomResponse {
        val room = roomRepo.findById(id).orElseThrow { NotFoundException("Room not found: $id") }

        req.name?.let {
            if (roomRepo.existsByNameAndIdNot(it, id)) throw ConflictException("Room name already exists")
            room.name = it
        }
        req.location?.let { room.location = it }
        req.capacity?.let {
            if (it <= 0) throw BadRequestException("Capacity must be > 0")
            if (it != room.capacity) {
                if (reservationRepo.existsActiveSeatReservations(id, activeStatuses))
                    throw ConflictException("Cannot change capacity while there are active seat reservations")
                room.capacity = it
                seatRepo.deleteByRoomId(id)
                seatRepo.flush()
                generateSeats(room)
            }
        }
        req.description?.let { room.description = it }
        req.active?.let { room.active = it }

        return roomRepo.save(room).toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        if (!roomRepo.existsById(id)) throw NotFoundException("Room not found: $id")
        roomRepo.deleteById(id)
    }

    fun availability(id: Long, req: AvailabilityRequest): AvailabilityResponse {
        if (!req.end.isAfter(req.start)) throw BadRequestException("end must be after start")
        roomRepo.findById(id).orElseThrow { NotFoundException("Room not found: $id") }

        val conflicts = reservationRepo.findConflicting(
            roomId = id,
            start = req.start,
            end = req.end,
            activeStatuses = listOf(ReservationStatus.PENDING, ReservationStatus.CONFIRMED),
        )

        return AvailabilityResponse(
            roomId = id,
            available = conflicts.isEmpty(),
            conflictingSlots = conflicts.map { SlotDto(it.startDateTime, it.endDateTime) },
        )
    }

    fun seatMap(id: Long, start: LocalDateTime?, end: LocalDateTime?): SeatMapResponse {
        val room = roomRepo.findById(id).orElseThrow { NotFoundException("Room not found: $id") }

        // Default window: "now" (current occupancy)
        val from = start ?: LocalDateTime.now()
        val to = end ?: from.plusMinutes(1)
        if (!to.isAfter(from)) throw BadRequestException("end must be after start")

        val seats = seatRepo.findByRoomIdOrderByRowNoAscColNoAsc(id)
        val roomBooked = reservationRepo.existsRoomConflict(id, from, to, activeStatuses)
        val bookedSeatIds = reservationRepo.findBookedSeatIds(id, from, to, activeStatuses).toSet()

        return SeatMapResponse(
            roomId = room.id,
            roomName = room.name,
            rows = seats.maxOfOrNull { it.rowNo } ?: 0,
            cols = seats.maxOfOrNull { it.colNo } ?: 0,
            roomBooked = roomBooked,
            seats = seats.map {
                SeatStatusDto(
                    id = it.id,
                    label = it.label,
                    row = it.rowNo,
                    col = it.colNo,
                    booked = roomBooked || it.id in bookedSeatIds,
                )
            },
        )
    }

    /** Auto-generate seats S1..Sn laid out in a near-square grid. */
    private fun generateSeats(room: StudyRoom) {
        val cols = ceil(sqrt(room.capacity.toDouble())).toInt().coerceAtLeast(1)
        val seats = (1..room.capacity).map { n ->
            Seat(
                room = room,
                label = "S$n",
                rowNo = (n - 1) / cols + 1,
                colNo = (n - 1) % cols + 1,
            )
        }
        seatRepo.saveAll(seats)
    }

    private fun StudyRoom.toResponse() = RoomResponse(
        id = id, name = name, location = location,
        capacity = capacity, description = description, active = active,
    )
}
