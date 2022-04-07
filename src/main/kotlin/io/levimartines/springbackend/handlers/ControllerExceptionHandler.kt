package io.levimartines.springbackend.handlers

import io.levimartines.springbackend.exceptions.ObjectNotFoundException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletRequest


@ControllerAdvice
class ControllerExceptionHandler {
    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(ObjectNotFoundException::class)
    fun objectNotFound(
        e: ObjectNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<StandardError?>? {

        val err = StandardError(
            System.currentTimeMillis(),
            HttpStatus.NOT_FOUND.value(), "Not found",
            e.message, request.requestURI
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body<StandardError>(err)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validation(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationError> {
        val err = ValidationError(
            System.currentTimeMillis(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error",
            "Validation error", request.requestURI
        )
        for (fieldErr in e.bindingResult.fieldErrors) {
            err.addError(fieldErr.field, fieldErr.defaultMessage)
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun validation(
        e: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<StandardError?>? {
        val err = StandardError(
            System.currentTimeMillis(),
            HttpStatus.BAD_REQUEST.value(), "Bad request",
            "Bad request", request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err)
    }

    @ExceptionHandler(Exception::class)
    fun validation(
        e: Exception,
        request: HttpServletRequest
    ): ResponseEntity<StandardError?>? {
        val err = StandardError(
            System.currentTimeMillis(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal error",
            "Internal error", request.requestURI
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err)
    }

}