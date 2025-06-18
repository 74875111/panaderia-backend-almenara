package com.example.panaderiaback.service

import com.example.panaderiaback.model.RoleModel
import com.example.panaderiaback.model.User
import com.example.panaderiaback.repository.UserRepository
import com.example.panaderiaback.security.JwtTokenUtil
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Optional
import kotlin.text.matches
import com.example.panaderiaback.dto.UserResponseDTO
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtTokenUtil: JwtTokenUtil
) {
    fun register(user: User): User {
        if (userRepository.existsByEmail(user.email)) {
            throw IllegalArgumentException("El email ya está registrado")
        }

        // Cifrar la contraseña con BCrypt
        val hashedPassword = passwordEncoder.encode(user.password)

        // Crear un nuevo usuario con la contraseña cifrada
        val newUser = user.copy(
            password = hashedPassword,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        return userRepository.save(newUser)
    }

    fun login(email: String, password: String): Map<String, Any> {
        val user = userRepository.findByEmail(email)
            .orElseThrow { BadCredentialsException("Email o contraseña incorrectos") }

        if (!passwordEncoder.matches(password, user.password)) {
            throw BadCredentialsException("Email o contraseña incorrectos")
        }

        val token = jwtTokenUtil.generateToken(user.email)

        // Crear el DTO de respuesta directamente en el servicio
        val userResponseDTO = UserResponseDTO(
            id = user.id,
            name = user.name,
            surname = user.surname,
            email = user.email,
            phoneNumber = user.phoneNumber,
            district = user.district,
            address = user.address,
            role = user.role
        )

        return mapOf(
            "token" to token,
            "user" to userResponseDTO
        )
    }

    fun getUser(id: Long): Optional<User> {
        return userRepository.findById(id)
    }

    fun getUserByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    fun updateUser(id: Long, userData: Map<String, Any>): User {
        val user = userRepository.findById(id)
            .orElseThrow { NoSuchElementException("Usuario no encontrado") }

        // Crear una copia del usuario con los campos actualizados
        var updatedUser = user

        // Actualizar los campos si están presentes en userData
        if (userData.containsKey("name")) {
            updatedUser = updatedUser.copy(name = userData["name"] as String)
        }

        if (userData.containsKey("surname")) {
            updatedUser = updatedUser.copy(surname = userData["surname"] as String)
        }

        if (userData.containsKey("phoneNumber")) {
            updatedUser = updatedUser.copy(phoneNumber = userData["phoneNumber"] as String)
        }

        if (userData.containsKey("district")) {
            updatedUser = updatedUser.copy(district = userData["district"] as String)
        }

        if (userData.containsKey("address")) {
            updatedUser = updatedUser.copy(address = userData["address"] as String)
        }

        if (userData.containsKey("password")) {
            val hashedPassword = passwordEncoder.encode(userData["password"] as String)
            updatedUser = updatedUser.copy(password = hashedPassword)
        }

        // Actualizar la fecha de actualización
        updatedUser = updatedUser.copy(updatedAt = LocalDateTime.now())

        return userRepository.save(updatedUser)
    }

    fun changeRole(id: Long, newRole: RoleModel): User {
        val user = userRepository.findById(id)
            .orElseThrow { NoSuchElementException("Usuario no encontrado") }

        val updatedUser = user.copy(
            role = newRole,
            updatedAt = LocalDateTime.now()
        )

        return userRepository.save(updatedUser)
    }

    fun deleteUser(id: Long) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
        } else {
            throw NoSuchElementException("Usuario no encontrado")
        }
    }
}