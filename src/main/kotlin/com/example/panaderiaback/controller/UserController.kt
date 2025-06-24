package com.example.panaderiaback.controller

import com.example.panaderiaback.model.RoleModel
import com.example.panaderiaback.model.User
import com.example.panaderiaback.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import com.example.panaderiaback.dto.UserResponseDTO


@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody user: User): ResponseEntity<UserResponseDTO> {
        val registeredUser = userService.register(user)

        val userResponse = UserResponseDTO(
            id = registeredUser.id,
            name = registeredUser.name,
            surname = registeredUser.surname,
            email = registeredUser.email,
            phoneNumber = registeredUser.phoneNumber,
            district = registeredUser.district,
            address = registeredUser.address,
            role = registeredUser.role
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginData: Map<String, String>): ResponseEntity<Map<String, Any>> {
        val email = loginData["email"] ?: return ResponseEntity.badRequest().body(mapOf("error" to "Email requerido"))
        val password = loginData["password"] ?: return ResponseEntity.badRequest().body(mapOf("error" to "Contraseña requerida"))

        val result = userService.login(email, password)

        // El servicio ya proporciona la estructura correcta
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userSecurity.isCurrentUser(#id)")
    fun getUser(@PathVariable id: Long): ResponseEntity<User> {
        val user = userService.getUser(id)
        return if (user.isPresent) {
            ResponseEntity.ok(user.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @userSecurity.isCurrentUser(#id)")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody userData: Map<String, Any>
    ): ResponseEntity<User> {
        val updatedUser = userService.updateUser(id, userData)
        return ResponseEntity.ok(updatedUser)
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun changeRole(
        @PathVariable id: Long,
        @RequestBody roleData: Map<String, String>
    ): ResponseEntity<User> {
        val roleName = roleData["role"] ?: return ResponseEntity.badRequest().body(null)

        val role = try {
            RoleModel.valueOf(roleName.uppercase())
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(null)
        }

        val updatedUser = userService.changeRole(id, role)
        return ResponseEntity.ok(updatedUser)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}