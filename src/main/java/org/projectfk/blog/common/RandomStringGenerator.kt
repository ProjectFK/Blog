package org.projectfk.blog.common

import org.hibernate.annotations.GenericGenerator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import org.springframework.stereotype.Service
import java.security.SecureRandom

// Collision is possible
@Service
@GenericGenerator(name = "random-string", strategy = "org.projectfk.blog.common.RandomStringGenerator")
class RandomStringGenerator : IdentifierGenerator {

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

    override fun generate(session: SharedSessionContractImplementor, `object`: Any) = getRandom(stringLength)

}