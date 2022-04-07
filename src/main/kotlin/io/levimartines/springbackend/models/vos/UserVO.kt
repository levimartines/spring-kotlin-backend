package io.levimartines.springbackend.models.vos

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class UserVO(
    @field:NotEmpty(message = "Name cannot be null")
    @field:NotNull(message = "Name cannot be null")
    var name: String?,

    @field:NotEmpty(message = "Email cannot be empty")
    @field:NotNull(message = "Email cannot be null")
    @field:Email(message = "Email need to be valid")
    var email: String?,

    @field:NotEmpty(message = "Password cannot be empty")
    @field:NotNull(message = "Password cannot be null")
    var password: String?,
)