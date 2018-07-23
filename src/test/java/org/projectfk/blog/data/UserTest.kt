package org.projectfk.blog.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.intellij.lang.annotations.Language
import org.junit.Test
import kotlin.test.fail

class UserTest {

    @Test
    fun deserializationTest(): Unit {
        val objectMapper = ObjectMapper().registerKotlinModule()
        @Language("JSON")
        val jsonValue = "{\n  \"id\": 1\n}"
        val tree = objectMapper.readTree(jsonValue)
        try {
            val convertValue = objectMapper.convertValue<User>(tree, User::class.java)
            fail()
        } catch (e: IllegalArgumentException) {
            val cause = e.cause
            assert(cause == null || cause is InvalidDefinitionException)
            var deeperLoop: Throwable? = cause
            while(deeperLoop?.cause != null) deeperLoop = deeperLoop.cause
            assert(deeperLoop is UninitializedPropertyAccessException)
        }
    }

}