package org.projectfk.blog.common

abstract class KnownException(val msg: String) : Exception(msg)

class IllegalParametersException(msg: String) : KnownException(msg)
