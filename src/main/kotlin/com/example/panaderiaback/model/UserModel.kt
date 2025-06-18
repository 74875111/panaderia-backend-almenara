package com.example.panaderiaback.model

import jakarta.persistence.*
import jakarta.validation.constraints.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    val id: Long = 0,

    @field:NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    val name: String = "",

    @field:NotBlank(message = "El apellido es obligatorio")
    @Column(nullable = false, length = 100)
    val surname: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val role: RoleModel = RoleModel.USER,

    @field:NotBlank(message = "El email es obligatorio")
    @field:Email(message = "Debe ser un email válido")
    @Column(nullable = false, unique = true, length = 100)
    val email: String = "",

    @field:NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    var password: String = "",

    @field:Pattern(regexp = "\\d*", message = "El teléfono debe contener solo números")
    @Column(name = "phone_number", length = 20)
    val phoneNumber: String? = null,

    @Column(length = 100)
    val district: String? = null,

    @Column(length = 255)
    val address: String? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)