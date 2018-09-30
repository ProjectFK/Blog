package org.projectfk.blog.common

import org.hibernate.annotations.GenericGenerator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.io.Serializable
import java.security.SecureRandom

// Collision is possible
@GenericGenerator(name = "random-string", strategy = "org.projectfk.blog.common.RandomStringGenerator")
object RandomStringGenerator : IdentifierGenerator {

    private const val stringLength = 5

    private val secureRandom = SecureRandom()
    private const val allowed = "ABCDEFGJKLMNPRSTUVWXYZ0123456789"
    private const val size = allowed.length


    fun getRandom(stringLength: Int): String {
        val sb = StringBuilder(stringLength)
        for (i in 0..size) {
            sb.append(allowed[secureRandom.nextInt(size)])
        }
        return sb.toString()
    }

    override fun generate(session: SharedSessionContractImplementor, `object`: Any): Serializable {
        return getRandom(stringLength)
    }

}