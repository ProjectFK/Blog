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
@ControllerAdvice("org.projectfk.blog.controller.restful")
class RestfulExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFound(exception: NotFoundException): ExceptionResultBean
            = ExceptionResultBean("Requested information not found", exception.message)

    @ExceptionHandler(IllegalParametersException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun illegalParameters(exception: IllegalParametersException): ExceptionResultBean
            = ExceptionResultBean("Illegal Parameter(s)", exception.message)

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun badRequest(exception: BadRequestException): ExceptionResultBean
            = ExceptionResultBean("Bad Request", exception.message)

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun forbiddenException(exception: ForbiddenException): ExceptionResultBean
            = ExceptionResultBean("Forbidden", exception.message)

    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun authorizationExeption(exception: AuthenticationException): ExceptionResultBean
            = ExceptionResultBean("Not Authorized", exception.message?: "")

    @ExceptionHandler(MismatchedInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun MismatchedInputException(exception: MismatchedInputException): ExceptionResultBean = ExceptionResultBean("Bad Request")

    @ExceptionHandler(MethodNotAllowedException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun methodNotAllowed(exception: MethodNotAllowedException): ExceptionResultBean = ExceptionResultBean("Bad Request")

}