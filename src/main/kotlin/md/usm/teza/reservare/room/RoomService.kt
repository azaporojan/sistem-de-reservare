package md.usm.teza.reservare.room

import md.usm.teza.reservare.common.exception.BadRequestException
import md.usm.teza.reservare.common.exception.ConflictException
import md.usm.teza.reservare.common.exception.NotFoundException
import md.usm.teza.reservare.reservation.ReservationRepository
import md.usm.teza.reservare.reservation.ReservationStatus
import md.usm.teza.reservare.room.dto.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoomService(
    private val roomRepo: RoomRepository,
    private val reservationRepo: ReservationRepository,
) {

    fun getAll(): List<RoomResponse> = roomRepo.findAllByActiveTrue().map { it.toResponse() }

    fun getById(id: Long): RoomResponse = roomRepo.findById(id)
        .orElseThrow { NotFoundException("Room not found: $id") }
        .toResponse()

    @Transactional
    fun create(req: CreateRoomRequest): RoomResponse {
        if (req.capacity <= 0) throw BadRequestException("Capacity must be > 0")
        if (roomRepo.existsByName(req.name)) throw ConflictException("Room name already exists")

        return roomRepo.save(
            StudyRoom(name = req.name, location = req.location, capacity = req.capacity, description = req.description)
        ).toResponse()
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
            room.capacity = it
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

    private fun StudyRoom.toResponse() = RoomResponse(
        id = id, name = name, location = location,
        capacity = capacity, description = description, active = active,
    )
}
