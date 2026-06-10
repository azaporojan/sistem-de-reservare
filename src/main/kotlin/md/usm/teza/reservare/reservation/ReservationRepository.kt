package md.usm.teza.reservare.reservation

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.UUID

interface ReservationRepository : JpaRepository<Reservation, UUID> {

    fun findByUserId(userId: UUID): List<Reservation>

    /** Count active (PENDING/CONFIRMED) ROOM reservations for a user. */
    fun countByUserIdAndTypeAndStatusIn(
        userId: UUID,
        type: ReservationType,
        statuses: Collection<ReservationStatus>,
    ): Long

    /** Count active SEAT reservations for a user. */
    fun countByUserIdAndStatusIn(userId: UUID, statuses: Collection<ReservationStatus>): Long

    /** Find reservations that overlap with [start, end) for the given room and statuses. */
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.status IN :activeStatuses
          AND r.startDateTime < :end
          AND r.endDateTime > :start
    """)
    fun findConflicting(
        @Param("roomId") roomId: Long,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime,
        @Param("activeStatuses") activeStatuses: Collection<ReservationStatus>,
    ): List<Reservation>

    /**
     * Find reservations that conflict with booking a specific seat in [start, end):
     * any full ROOM reservation of the room, or a SEAT reservation on the same seat.
     */
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.status IN :activeStatuses
          AND r.startDateTime < :end
          AND r.endDateTime > :start
          AND (r.type = md.usm.teza.reservare.reservation.ReservationType.ROOM
               OR (r.seat IS NOT NULL AND r.seat.id = :seatId))
    """)
    fun findSeatConflicting(
        @Param("roomId") roomId: Long,
        @Param("seatId") seatId: Long,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime,
        @Param("activeStatuses") activeStatuses: Collection<ReservationStatus>,
    ): List<Reservation>

    /**
     * Active reservations overlapping [start, end) in the room, with the booker's full name.
     * Includes both SEAT and ROOM reservations. Tuple: (reservation, fullName).
     */
    @Query("""
        SELECT r, u.fullName FROM Reservation r, md.usm.teza.reservare.user.User u
        WHERE u.id = r.userId
          AND r.room.id = :roomId
          AND r.status IN :activeStatuses
          AND r.startDateTime < :end
          AND r.endDateTime > :start
    """)
    fun findOccupancyWithBooker(
        @Param("roomId") roomId: Long,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime,
        @Param("activeStatuses") activeStatuses: Collection<ReservationStatus>,
    ): List<Array<Any>>

    /** IDs of seats with an active SEAT reservation overlapping [start, end) in the room. */
    @Query("""
        SELECT DISTINCT r.seat.id FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.seat IS NOT NULL
          AND r.status IN :activeStatuses
          AND r.startDateTime < :end
          AND r.endDateTime > :start
    """)
    fun findBookedSeatIds(
        @Param("roomId") roomId: Long,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime,
        @Param("activeStatuses") activeStatuses: Collection<ReservationStatus>,
    ): List<Long>

    /** True if any active full-ROOM reservation overlaps [start, end) for the room. */
    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.type = md.usm.teza.reservare.reservation.ReservationType.ROOM
          AND r.status IN :activeStatuses
          AND r.startDateTime < :end
          AND r.endDateTime > :start
    """)
    fun existsRoomConflict(
        @Param("roomId") roomId: Long,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime,
        @Param("activeStatuses") activeStatuses: Collection<ReservationStatus>,
    ): Boolean

    /** True if any active reservation references a seat of this room (used before regenerating seats). */
    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.seat IS NOT NULL
          AND r.status IN :activeStatuses
    """)
    fun existsActiveSeatReservations(
        @Param("roomId") roomId: Long,
        @Param("activeStatuses") activeStatuses: Collection<ReservationStatus>,
    ): Boolean

    /** Find CONFIRMED reservations whose endDateTime is in the past (for scheduler). */
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.status = :status
          AND r.endDateTime <= :now
    """)
    fun findConfirmedPastEnd(
        @Param("now") now: LocalDateTime,
        @Param("status") status: ReservationStatus = ReservationStatus.CONFIRMED,
    ): List<Reservation>
}
