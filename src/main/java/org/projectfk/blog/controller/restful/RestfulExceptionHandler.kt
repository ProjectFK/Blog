package org.projectfk.blog.controller.restful

import org.projectfk.blog.common.*
import org.springframework.http.HttpStatus
import org.springframework.security.access.AuthorizationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

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

}