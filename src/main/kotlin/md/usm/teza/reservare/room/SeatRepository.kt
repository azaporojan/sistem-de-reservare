package md.usm.teza.reservare.room

import org.springframework.data.jpa.repository.JpaRepository

interface SeatRepository : JpaRepository<Seat, Long> {
    fun findByRoomIdOrderByRowNoAscColNoAsc(roomId: Long): List<Seat>
    fun countByRoomId(roomId: Long): Long
    fun findByRoomIdAndLabelIn(roomId: Long, labels: Collection<String>): List<Seat>
    fun deleteByRoomId(roomId: Long)
}
