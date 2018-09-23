package org.projectfk.blog.controller.restful

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.projectfk.blog.common.*
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.MethodNotAllowedException

@RestController
@ControllerAdvice(annotations = [RestController::class])
class RestfulExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFound(exception: NotFoundException): ExceptionResultBean
            = ExceptionResultBean("Requested information not found", exception.message)

    @ExceptionHandler(
            BadRequestException::class,
            MethodNotAllowedException::class,
            MismatchedInputException::class,
            IllegalParametersException::class
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun badRequest(exception: Exception): ExceptionResultBean = ExceptionResultBean("Bad Request", exception.message
            ?: "")

    @ExceptionHandler(
            ForbiddenException::class,
            AuthenticationException::class,
            AccessDeniedException::class
    )
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun forbiddenException(exception: Exception): ExceptionResultBean = ExceptionResultBean("Forbidden", exception.message
            ?: "")

}