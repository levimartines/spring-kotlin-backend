package io.levimartines.springbackend.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JWTAuthorizationFilter(
    authenticationManager: AuthenticationManager?, jwtUtil: JWTUtils,
    userDetailsService: UserDetailsService
) : BasicAuthenticationFilter(authenticationManager) {
    private val jwtUtil: JWTUtils
    private val userDetailsService: UserDetailsService

    init {
        this.jwtUtil = jwtUtil
        this.userDetailsService = userDetailsService
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse,
        chain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            val auth = getAuthentication(
                request,
                header.substring(7)
            )
            if (auth != null) {
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        chain.doFilter(request, response)
    }

    private fun getAuthentication(
        request: HttpServletRequest,
        token: String
    ): UsernamePasswordAuthenticationToken? {
        if (jwtUtil.isTokenValid(token)) {
            val username: String? = jwtUtil.getUsername(token)
            val userDetails = userDetailsService.loadUserByUsername(username)
            return UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails.authorities
            )
        }
        return null
    }
}