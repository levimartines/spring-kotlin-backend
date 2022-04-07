package io.levimartines.springbackend.handlers

open class StandardError(
    val timestamp: Long? = null,
    val status: Int? = null,
    val error: String? = null,
    val message: String? = null,
    val path: String? = null
)