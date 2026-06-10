package md.usm.teza.reservare.room

import md.usm.teza.reservare.room.dto.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rooms")
class RoomController(private val roomService: RoomService) {

    @GetMapping
    @PreAuthorize("hasAuthority('ROOM_READ') or hasAuthority('ALL')")
    fun listRooms(): List<RoomResponse> = roomService.getAll()

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROOM_READ') or hasAuthority('ALL')")
    fun getRoom(@PathVariable id: Long): RoomResponse = roomService.getById(id)

    @GetMapping("/{id}/availability")
    @PreAuthorize("hasAuthority('ROOM_READ') or hasAuthority('ALL')")
    fun availability(@PathVariable id: Long, @RequestBody req: AvailabilityRequest): AvailabilityResponse =
        roomService.availability(id, req)

    @GetMapping("/{id}/seats")
    @PreAuthorize("hasAuthority('ROOM_READ') or hasAuthority('ALL')")
    fun seatMap(
        @PathVariable id: Long,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) start: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) end: LocalDateTime?,
    ): SeatMapResponse = roomService.seatMap(id, start, end)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROOM_WRITE') or hasAuthority('ALL')")
    fun createRoom(@RequestBody req: CreateRoomRequest): RoomResponse = roomService.create(req)

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROOM_WRITE') or hasAuthority('ALL')")
    fun updateRoom(@PathVariable id: Long, @RequestBody req: UpdateRoomRequest): RoomResponse =
        roomService.update(id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROOM_WRITE') or hasAuthority('ALL')")
    fun deleteRoom(@PathVariable id: Long) = roomService.delete(id)
}
