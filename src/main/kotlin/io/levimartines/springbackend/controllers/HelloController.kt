package io.levimartines.springbackend.controllers

import io.levimartines.springbackend.security.PrincipalService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hello")
class HelloController {

    @GetMapping
    fun getHello(): ResponseEntity<String> {
        val email = PrincipalService.authenticated()?.username
        val message = "Hello World from $email"
        return ResponseEntity.ok(message)
    }

}