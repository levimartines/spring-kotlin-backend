package io.levimartines.springbackend.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties("settings")
class Settings(
    var frontendUrl: String = ""
)