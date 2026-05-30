package md.usm.teza.reservare.reservation

import md.usm.teza.reservare.reservation.dto.*
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/reservations")
class ReservationController(private val reservationService: ReservationService) {

    @GetMapping
    @PreAuthorize("hasAuthority('RESERVATION_READ_ALL') or hasAuthority('ALL')")
    fun listAll(): List<ReservationResponse> = reservationService.getAll()

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('RESERVATION_READ_OWN') or hasAuthority('ALL')")
    fun listMy(auth: Authentication): List<ReservationResponse> =
        reservationService.getMy(UUID.fromString(auth.name))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID, auth: Authentication): ReservationResponse =
        reservationService.getById(id, auth)

    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROOM_BOOK') or hasAuthority('ALL')")
    fun bookRoom(@RequestBody req: BookRoomRequest, auth: Authentication): ReservationResponse {
        val isAdmin = auth.authorities.any { it.authority == "ALL" }
        return reservationService.bookRoom(UUID.fromString(auth.name), req, isAdmin)
    }

    @PostMapping("/seat")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SEAT_BOOK') or hasAuthority('ALL')")
    fun bookSeat(@RequestBody req: BookSeatRequest, auth: Authentication): ReservationResponse {
        val isAdmin = auth.authorities.any { it.authority == "ALL" }
        return reservationService.bookSeat(UUID.fromString(auth.name), req, isAdmin)
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('RESERVATION_MANAGE') or hasAuthority('ALL')")
    fun confirm(@PathVariable id: UUID, @RequestBody req: ReviewRequest, auth: Authentication): ReservationResponse =
        reservationService.confirm(id, UUID.fromString(auth.name), req)

    @PostMapping("/{id}/decline")
    @PreAuthorize("hasAuthority('RESERVATION_MANAGE') or hasAuthority('ALL')")
    fun decline(@PathVariable id: UUID, @RequestBody req: ReviewRequest, auth: Authentication): ReservationResponse =
        reservationService.decline(id, UUID.fromString(auth.name), req)

    @DeleteMapping("/{id}")
    fun cancel(@PathVariable id: UUID, auth: Authentication): ReservationResponse =
        reservationService.cancel(id, auth)
}
