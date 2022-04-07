package io.levimartines.springbackend.controllers

import io.levimartines.springbackend.models.entities.User
import io.levimartines.springbackend.models.vos.UserVO
import io.levimartines.springbackend.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UserController(private val service: UserService) {

    @PostMapping
    fun save(@Valid @RequestBody userVO: UserVO): ResponseEntity<User> {
        val user = service.save(userVO)
        return ResponseEntity.ok(user)
    }

}