package com.example.panaderiaback.security

import com.example.panaderiaback.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component("userSecurity")
class UserSecurity(private val userService: UserService) {

    fun isCurrentUser(userId: Long): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication.name

        val currentUser = userService.getUserByEmail(email)

        return currentUser.isPresent && currentUser.get().id == userId
    }
}