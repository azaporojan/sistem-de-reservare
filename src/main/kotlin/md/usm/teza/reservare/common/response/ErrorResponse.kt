package md.usm.teza.reservare.common.response

import java.time.Instant

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val path: String? = null,
)
