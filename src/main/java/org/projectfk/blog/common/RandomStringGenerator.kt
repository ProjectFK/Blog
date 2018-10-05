package org.projectfk.blog.common

import org.springframework.stereotype.Service
import java.security.SecureRandom

// Collision is possible
@Service
object RandomStringGenerator {

    private val stringLength = 5

    private val secureRandom = SecureRandom()
    private val allowed = "ABCDEFGJKLMNPRSTUVWXYZ0123456789"
    private val size = allowed.length


    fun getRandom(stringLength: Int): String {
        val sb = StringBuilder(stringLength)
        for (i in 0..size)
            sb.append(allowed[secureRandom.nextInt(size)])
        return sb.toString()
    }

}