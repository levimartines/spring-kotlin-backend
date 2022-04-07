package io.levimartines.springbackend.security

import io.levimartines.springbackend.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
        if (user.isEmpty) {
            throw UsernameNotFoundException(email)
        }
        return UserDetailsImpl(user.get())
    }
}