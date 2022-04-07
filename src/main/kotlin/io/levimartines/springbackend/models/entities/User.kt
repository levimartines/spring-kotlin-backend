package io.levimartines.springbackend.models.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "user_account")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Column(name = "name", nullable = false)
    var name: String?,

    @Column(name = "email", nullable = false)
    var email: String?,

    @Column(name = "password", nullable = false)
    @JsonIgnore
    var password: String?,

    )