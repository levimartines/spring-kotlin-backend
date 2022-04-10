package io.levimartines.springbackend.it

import io.levimartines.springbackend.handlers.ValidationError
import io.levimartines.springbackend.models.entities.User
import io.levimartines.springbackend.models.vos.UserVO
import io.levimartines.springbackend.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIT @Autowired constructor(
    template: TestRestTemplate,
    userRepository: UserRepository,
    encoder: BCryptPasswordEncoder
) : BaseIT(template, userRepository, encoder) {

    @Nested
    inner class Create {
        @Test
        fun shouldCreateUserWhenAllDataIsCorrect() {
            val userVO = UserVO("Test", "test@test.com", "test")
            val user = createUser(userVO)

            assertNotNull(user.id)
        }

        @Test
        fun shouldNotCreateUserWhenDataIsIncorrect() {
            var userVO = UserVO(null, null, null)
            var response = template.postForEntity("/users", userVO, ValidationError::class.java)
            validateUnprocessableEntity(response)
            assertEquals(6, response.body?.errors?.size)

            userVO = UserVO("", "", "")
            response = template.postForEntity("/users", userVO, ValidationError::class.java)
            validateUnprocessableEntity(response)
            assertEquals(3, response.body?.errors?.size)

            userVO = UserVO("Test", "test", "test")
            response = template.postForEntity("/users", userVO, ValidationError::class.java)
            validateUnprocessableEntity(response)
            assertEquals(1, response.body?.errors?.size)
        }
    }

    @Nested
    inner class Read {

        @Test
        fun shouldRetrieveUserWhenRequestedUserAndLoggedUserAreTheSame() {
            val response = template.exchange("/users/${defaultUser?.id}", HttpMethod.GET, getRequestEntity(), User::class.java)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
            assertEquals(defaultUser?.id, response.body?.id)
            assertEquals(defaultUser?.email, response.body?.email)
        }

        @Test
        fun shouldNotRetrieveUserWhenRequestedUserAndLoggedUserAreDifferent() {
            val userVO = UserVO("Test", "test@test.com", "test")
            createUser(userVO)

            val response = template.exchange("/users/0", HttpMethod.GET, getRequestEntity(), User::class.java)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }
    }

    private fun createUser(userVO: UserVO): User {
        val response = template.postForEntity("/users", userVO, User::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertNotNull(response.body?.id)
        return response.body!!
    }

    private fun validateUnprocessableEntity(response: ResponseEntity<ValidationError>) {
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        assertNotNull(response.body)
        assertNotNull(response.body?.errors)
    }

}