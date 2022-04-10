package io.levimartines.springbackend.it

import io.levimartines.springbackend.models.entities.User
import io.levimartines.springbackend.models.vos.LoginVO
import io.levimartines.springbackend.models.vos.UserVO
import io.levimartines.springbackend.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerIT @Autowired constructor(
    template: TestRestTemplate,
    userRepository: UserRepository,
    encoder: BCryptPasswordEncoder
) : BaseIT(template, userRepository, encoder) {

    @Test
    fun shouldRetriveTokenWhenLoginWithValidCredentials() {
        val userVO = UserVO("Test", "test@test.com", "test123")
        val user = createUser(userVO)

        val loginVO = LoginVO(user.email, userVO.password)
        val response = template.postForEntity("/login", loginVO, Void::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.headers)
        assertNotNull(response.headers["Authorization"])
    }

    fun createUser(user: UserVO): User {
        val response = template.postForEntity("/users", user, User::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertNotNull(response.body?.id)
        return response.body!!
    }

}