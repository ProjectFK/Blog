package org.projectfk.blog.common

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

open class KnownException(msg: String, cause: Throwable? = null) : Exception(msg, cause) {
    override val message: String
        get() = super.message!!
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class IllegalParametersException(msg: String) : BadRequestException(msg)

@ResponseStatus(HttpStatus.BAD_REQUEST)
open class BadRequestException(msg: String, cause: Throwable? = null) : KnownException(msg, cause)

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(message: String) : KnownException(message) {
    constructor() : this("content not found")
}

@ResponseStatus(HttpStatus.FORBIDDEN)
class ForbiddenException(message: String) : KnownException(message)