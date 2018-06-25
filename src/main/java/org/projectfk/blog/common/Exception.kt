package org.projectfk.blog.common

open class KnownException(msg: String) : Exception(msg)

class IllegalParametersException(msg: String) : KnownException(msg)
