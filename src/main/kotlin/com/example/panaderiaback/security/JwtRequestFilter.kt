package com.example.panaderiaback.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import com.example.panaderiaback.repository.UserRepository

@Component
class JwtRequestFilter(
    private val jwtTokenUtil: JwtTokenUtil,
    private val userRepository: UserRepository  // Cambio UserService por UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val jwt = authHeader.substring(7)

            if (jwtTokenUtil.validateToken(jwt)) {
                val email = jwtTokenUtil.getEmailFromToken(jwt)
                val userOptional = userRepository.findByEmail(email)  // Usa directamente el repositorio

                if (userOptional.isPresent) {
                    val user = userOptional.get()

                    val authorities = listOf(SimpleGrantedAuthority(user.role.name))
                    val authentication = UsernamePasswordAuthenticationToken(
                        user.email, null, authorities
                    )

                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}