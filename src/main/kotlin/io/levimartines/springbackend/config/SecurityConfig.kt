package io.levimartines.springbackend.config

import io.levimartines.springbackend.properties.Settings
import io.levimartines.springbackend.security.JWTAuthenticationFilter
import io.levimartines.springbackend.security.JWTAuthorizationFilter
import io.levimartines.springbackend.security.JWTUtils
import io.levimartines.springbackend.security.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(val userDetailsService: UserDetailsServiceImpl, val jwtUtils: JWTUtils) :
    WebSecurityConfigurerAdapter() {

    companion object {
        val PUBLIC_MATCHERS_GET = arrayOf("/users")
        val PUBLIC_MATCHERS_POST = arrayOf("/users")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable()
        http.authorizeHttpRequests()
            .antMatchers(HttpMethod.GET, *PUBLIC_MATCHERS_GET).permitAll()
            .antMatchers(HttpMethod.POST, *PUBLIC_MATCHERS_POST).permitAll()
            .anyRequest().authenticated()
        http.addFilter(JWTAuthenticationFilter(authenticationManager(), jwtUtils))
        http.addFilter(JWTAuthorizationFilter(authenticationManager(), jwtUtils, userDetailsService))
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Bean
    fun encoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder();
    }

    @Bean
    fun corsConfigurationSource(settings: Settings): CorsConfigurationSource? {
        val configuration = CorsConfiguration().applyPermitDefaultValues()
        configuration.allowedOrigins = mutableListOf(settings.frontendUrl)
        configuration.allowedHeaders = mutableListOf("Authorization", "content-type")
        configuration.allowedMethods = mutableListOf("POST", "GET", "PUT", "DELETE", "OPTIONS")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}