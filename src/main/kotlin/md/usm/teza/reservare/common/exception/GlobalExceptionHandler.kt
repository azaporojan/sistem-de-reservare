package md.usm.teza.reservare.common.exception

import md.usm.teza.reservare.common.response.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException, req: HttpServletRequest) =
        error(HttpStatus.BAD_REQUEST, ex.message ?: "Bad request", req)

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException, req: HttpServletRequest) =
        error(HttpStatus.UNAUTHORIZED, ex.message ?: "Unauthorized", req)

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(ex: ForbiddenException, req: HttpServletRequest) =
        error(HttpStatus.FORBIDDEN, ex.message ?: "Forbidden", req)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException, req: HttpServletRequest) =
        error(HttpStatus.NOT_FOUND, ex.message ?: "Not found", req)

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException, req: HttpServletRequest) =
        error(HttpStatus.CONFLICT, ex.message ?: "Conflict", req)

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException, req: HttpServletRequest) =
        error(HttpStatus.FORBIDDEN, "Access denied", req)

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuth(ex: AuthenticationException, req: HttpServletRequest) =
        error(HttpStatus.UNAUTHORIZED, ex.message ?: "Authentication required", req)

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        ex.printStackTrace()
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", req)
    }

    private fun error(status: HttpStatus, message: String, req: HttpServletRequest) =
        ResponseEntity.status(status).body(
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = message,
                path = req.requestURI,
            )
        )
}
