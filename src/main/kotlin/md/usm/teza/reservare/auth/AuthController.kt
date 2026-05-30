package md.usm.teza.reservare.auth

import md.usm.teza.reservare.auth.dto.AuthResponse
import md.usm.teza.reservare.auth.dto.LoginRequest
import md.usm.teza.reservare.auth.dto.RefreshRequest
import md.usm.teza.reservare.auth.dto.RegisterRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody req: RegisterRequest): AuthResponse = authService.register(req)

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): AuthResponse = authService.login(req)

    @PostMapping("/refresh")
    fun refresh(@RequestBody req: RefreshRequest): AuthResponse = authService.refresh(req)
}
