package io.levimartines.springbackend.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.levimartines.springbackend.models.vos.LoginVO
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JWTAuthenticationFilter(authenticationManager: AuthenticationManager, private val jwtUtils: JWTUtils) :
    UsernamePasswordAuthenticationFilter(authenticationManager) {

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(req: HttpServletRequest, res: HttpServletResponse?): Authentication? {
        return try {
            val creds: LoginVO = ObjectMapper().readValue(req.inputStream, LoginVO::class.java)
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    creds.email,
                    creds.password,
                    ArrayList()
                )
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun successfulAuthentication(
        req: HttpServletRequest?,
        res: HttpServletResponse,
        chain: FilterChain?,
        auth: Authentication
    ) {
        val token: String? = jwtUtils.generateToken((auth.principal as UserDetailsImpl).username)
        res.addHeader("Authorization", "Bearer $token")
        res.addHeader("access-control-expose-headers", "Authorization")
    }

    private class JWTAuthenticationFailureHandler : AuthenticationFailureHandler {
        @Throws(IOException::class)
        override fun onAuthenticationFailure(
            request: HttpServletRequest?,
            response: HttpServletResponse,
            exception: AuthenticationException?
        ) {
            response.status = 401
            response.contentType = "application/json"
            response.writer.append(json())
        }

        private fun json(): String {
            val date: Long = Date().time
            return "{\"timestamp\": $date, \"status\": 401, \"error\": \"Not authorized\", \"message\": \"Invalid username or password\", \"path\": \"/login\"}"
        }
    }
}