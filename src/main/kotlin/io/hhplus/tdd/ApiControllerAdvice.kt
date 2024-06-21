package io.hhplus.tdd

import io.hhplus.tdd.point.InvalidAmountException
import io.hhplus.tdd.point.InvalidUserIdException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

data class ErrorResponse(val code: String, val message: String)

@RestControllerAdvice
class ApiControllerAdvice : ResponseEntityExceptionHandler() {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(InvalidUserIdException::class)
    fun handleInvalidUserIdException(e: InvalidUserIdException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse("400", "없는 유저 ID 입니다."),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(InvalidAmountException::class)
    fun handleInvalidAmountException(e: InvalidAmountException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse("400", "충전량은 양수여야 합니다."),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse("500", "에러가 발생했습니다."),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }
}