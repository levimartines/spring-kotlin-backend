package io.levimartines.springbackend.it

import io.levimartines.springbackend.models.entities.User
import io.levimartines.springbackend.models.vos.LoginVO
import io.levimartines.springbackend.repositories.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseIT @Autowired constructor(
    val template: TestRestTemplate,
    val userRepository: UserRepository,
    val encoder: BCryptPasswordEncoder
) {
    protected var defaultUser: User? = null

    companion object {
        @Container
        val postgresDB = PostgreSQLContainer<Nothing>("postgres:13.2")
            .apply {
                withDatabaseName("postgres")
                withUsername("postgres")
                withPassword("postgres")
            }
    }

    @BeforeEach
    fun setup() {
        userRepository.deleteAllInBatch()
        defaultUser = createDefaultUser();
    }

    fun createDefaultUser(): User {
        val user = User(null, "User", "user@user.com", encoder.encode("user"))
        return userRepository.save(user)
    }

    fun getRequestEntity(): HttpEntity<Any> {
        return HttpEntity(getAuthHeader())
    }

    fun getAuthHeader(): HttpHeaders {
        val bearerToken = getBearerToken()
        val headers = HttpHeaders()
        headers.set("Authorization", bearerToken)
        return headers
    }

    fun getBearerToken(): String {
        val loginVO = LoginVO(defaultUser?.email, "user")
        val response = template.postForEntity("/login", loginVO, Void::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertNotNull(response.headers)
        Assertions.assertNotNull(response.headers["Authorization"])

        return response.headers["Authorization"]?.get(0).toString()
    }

}
