package io.levimartines.springbackend.security

import org.springframework.security.core.context.SecurityContextHolder

object PrincipalService {
    fun authenticated(): UserDetailsImpl? {
        return try {
            SecurityContextHolder.getContext().authentication
                .principal as UserDetailsImpl
        } catch (e: Exception) {
            null
        }
    }
}