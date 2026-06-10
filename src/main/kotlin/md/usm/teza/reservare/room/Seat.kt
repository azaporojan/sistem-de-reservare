package md.usm.teza.reservare.room

import jakarta.persistence.*

@Entity
@Table(
    name = "seats",
    uniqueConstraints = [UniqueConstraint(columnNames = ["room_id", "label"])],
)
class Seat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: StudyRoom,

    @Column(nullable = false)
    val label: String,

    @Column(name = "row_no", nullable = false)
    val rowNo: Int,

    @Column(name = "col_no", nullable = false)
    val colNo: Int,
)
