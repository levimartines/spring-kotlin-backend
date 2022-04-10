package io.levimartines.springbackend.services

import io.levimartines.springbackend.exceptions.AuthorizationException
import io.levimartines.springbackend.exceptions.ObjectNotFoundException
import io.levimartines.springbackend.models.entities.User
import io.levimartines.springbackend.models.vos.UserVO
import io.levimartines.springbackend.repositories.UserRepository
import io.levimartines.springbackend.security.PrincipalService
import mu.KotlinLogging
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository,
    private val encoder: BCryptPasswordEncoder
) {
    private val logger = KotlinLogging.logger {}

    fun save(userVO: UserVO): User {
        val encryptedPassword = encoder.encode(userVO.password)
        val user = User(null, userVO.name, userVO.email, encryptedPassword)
        logger.info { "Creating User for email: ${userVO.email}" }
        return repository.save(user)
    }

    fun findById(id: Long): User {
        val authenticatedUser = PrincipalService.authenticated()?.user
        if (authenticatedUser == null || id != authenticatedUser.id) {
            throw AuthorizationException("Forbidden")
        }
        return repository.findById(id)
            .orElseThrow { ObjectNotFoundException("User ID: $id not found") }
    }
}