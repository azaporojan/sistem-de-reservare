package md.usm.teza.reservare.reservation

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class CompletionScheduler(private val reservationRepo: ReservationRepository) {

    private val log = LoggerFactory.getLogger(CompletionScheduler::class.java)

    @Scheduled(cron = "0 0/30 * * * *")
    @Transactional
    fun completeExpiredReservations() {
        val now = LocalDateTime.now()
        val expired = reservationRepo.findConfirmedPastEnd(now)
        if (expired.isEmpty()) return

        expired.forEach { it.status = ReservationStatus.COMPLETED }
        reservationRepo.saveAll(expired)
        log.info("Completed ${expired.size} reservation(s) past their end time")
    }
}
