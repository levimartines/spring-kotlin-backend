package io.levimartines.springbackend.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*


@Component
class JWTUtils {

    @Value("\${jwt.secret}")
    private val secret: String? = null

    @Value("\${jwt.expiration}")
    private val expiration: Long? = null

    fun generateToken(username: String?): String? {
        return Jwts.builder().setSubject(username)
            .setExpiration(Date(System.currentTimeMillis() + expiration!!))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

    fun isTokenValid(token: String): Boolean {
        val claims = getClaims(token)
        if (claims != null) {
            val username = claims.subject
            val expirationDate: Date? = claims.expiration
            val now = Date(System.currentTimeMillis())
            return username != null && expirationDate != null && now.before(expirationDate)
        }
        return false
    }

    private fun getClaims(token: String): Claims? {
        return try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
        } catch (e: Exception) {
            null
        }
    }

    fun getUsername(token: String): String? {
        val claims = getClaims(token)
        return claims?.subject
    }
}