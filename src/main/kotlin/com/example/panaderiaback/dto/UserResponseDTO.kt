package com.example.panaderiaback.dto

import com.example.panaderiaback.model.RoleModel

data class UserResponseDTO(
    val id: Long?,
    val name: String,
    val surname: String,
    val email: String,
    val phoneNumber: String?,
    val district: String?,
    val address: String?,
    val role: RoleModel
)