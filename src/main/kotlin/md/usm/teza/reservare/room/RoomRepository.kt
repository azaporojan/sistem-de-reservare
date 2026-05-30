package md.usm.teza.reservare.room

import org.springframework.data.jpa.repository.JpaRepository

interface RoomRepository : JpaRepository<StudyRoom, Long> {
    fun existsByName(name: String): Boolean
    fun existsByNameAndIdNot(name: String, id: Long): Boolean
    fun findAllByActiveTrue(): List<StudyRoom>
}
