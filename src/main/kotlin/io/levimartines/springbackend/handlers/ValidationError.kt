package io.levimartines.springbackend.handlers

class ValidationError(
    timestamp: Long? = null,
    status: Int? = null,
    error: String? = null,
    message: String? = null,
    path: String? = null
) : StandardError(timestamp, status, error, message, path) {
    var errors = ArrayList<FieldMessage>()

    fun addError(errorName: String, message: String?) {
        errors.add(FieldMessage(errorName, message))
    }
}