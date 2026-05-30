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
