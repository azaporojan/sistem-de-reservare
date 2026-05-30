package md.usm.teza.reservare.user

import md.usm.teza.reservare.user.dto.*
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping("/me")
    fun getMe(auth: Authentication): UserResponse =
        userService.getMe(UUID.fromString(auth.name))

    @PatchMapping("/me")
    fun updateMe(auth: Authentication, @RequestBody req: UpdateProfileRequest): UserResponse =
        userService.updateMe(UUID.fromString(auth.name), req)

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ALL')")
    fun listUsers(): List<UserResponse> = userService.getAll()

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ') or hasAuthority('ALL')")
    fun getUser(@PathVariable id: UUID): UserResponse = userService.getById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasAuthority('ALL')")
    fun createUser(@RequestBody req: CreateUserRequest): UserResponse = userService.createUser(req)

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasAuthority('ALL')")
    fun updateUser(@PathVariable id: UUID, @RequestBody req: UpdateUserRequest): UserResponse =
        userService.updateUser(id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasAuthority('ALL')")
    fun deleteUser(@PathVariable id: UUID) = userService.deleteUser(id)

    @PostMapping("/{id}/permissions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasAuthority('ALL')")
    fun grantPermission(@PathVariable id: UUID, @RequestBody req: GrantPermissionRequest) =
        userService.grantPermission(id, req)

    @DeleteMapping("/{id}/permissions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('USER_MANAGE') or hasAuthority('ALL')")
    fun revokePermission(@PathVariable id: UUID, @RequestBody req: GrantPermissionRequest) =
        userService.revokePermission(id, req)
}
