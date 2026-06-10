package md.usm.teza.reservare.room.dto

import java.time.LocalDateTime

data class RoomResponse(
    val id: Long,
    val name: String,
    val location: String,
    val capacity: Int,
    val description: String?,
    val active: Boolean,
)

data class CreateRoomRequest(
    val name: String,
    val location: String,
    val capacity: Int,
    val description: String? = null,
)

data class UpdateRoomRequest(
    val name: String? = null,
    val location: String? = null,
    val capacity: Int? = null,
    val description: String? = null,
    val active: Boolean? = null,
)

data class AvailabilityRequest(
    val start: LocalDateTime,
    val end: LocalDateTime,
)

data class AvailabilityResponse(
    val roomId: Long,
    val available: Boolean,
    val conflictingSlots: List<SlotDto>,
)

data class SlotDto(
    val start: LocalDateTime,
    val end: LocalDateTime,
)

data class SeatMapResponse(
    val roomId: Long,
    val roomName: String,
    val rows: Int,
    val cols: Int,
    /** True when a full-room reservation covers the requested window (all seats unavailable). */
    val roomBooked: Boolean,
    val seats: List<SeatStatusDto>,
)

data class SeatStatusDto(
    val id: Long,
    val label: String,
    val row: Int,
    val col: Int,
    val booked: Boolean,
)
