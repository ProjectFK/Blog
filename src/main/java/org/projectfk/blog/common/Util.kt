package org.projectfk.blog.common

import org.apache.commons.logging.Log

//better performance for more bsting
//No need to lazy load throwable
inline fun Log.debugIfEnable(throwable: Throwable? = null, message: () -> String) {
    if (isDebugEnabled) debug(message.invoke(), throwable)
}
