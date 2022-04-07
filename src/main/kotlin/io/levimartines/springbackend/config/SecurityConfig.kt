package io.levimartines.springbackend.config

import io.levimartines.springbackend.security.JWTAuthenticationFilter
import io.levimartines.springbackend.security.JWTAuthorizationFilter
import io.levimartines.springbackend.security.JWTUtils
import io.levimartines.springbackend.security.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@Configuration
class SecurityConfig(val userDetailsService: UserDetailsServiceImpl, val jwtUtils: JWTUtils) :
    WebSecurityConfigurerAdapter() {

    companion object {
        val PUBLIC_MATCHERS_GET = arrayOf("/users")
        val PUBLIC_MATCHERS_POST = arrayOf("/users")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable()
        http.addFilter(JWTAuthenticationFilter(authenticationManager(), jwtUtils))
        http.addFilter(JWTAuthorizationFilter(authenticationManager(), jwtUtils, userDetailsService))
        http.authorizeHttpRequests()
            .antMatchers(HttpMethod.GET, *PUBLIC_MATCHERS_GET).permitAll()
            .antMatchers(HttpMethod.POST, *PUBLIC_MATCHERS_POST).permitAll()
            .anyRequest().authenticated()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Bean
    fun encoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder();
    }
}