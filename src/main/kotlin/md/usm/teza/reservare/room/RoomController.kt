package md.usm.teza.reservare.room

import md.usm.teza.reservare.room.dto.*
import org.springframework.http.HttpStatus
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
