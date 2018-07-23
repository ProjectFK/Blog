package org.projectfk.blog.common

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

open class KnownException(msg: String) : Exception(msg)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class IllegalParametersException(msg: String) : KnownException(msg)

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(message: String) : KnownException(message) {
    constructor() : this("content not found")
}