package org.projectfk

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import org.junit.Test

class JsonUnwarppedTest {

    @JsonUnwrapped
    @JsonProperty
    val test = object : Any() {
        val something: String = "yes"
    }

    @Test
    fun tryIfWorks(): Unit {
        val writer: ObjectWriter = ObjectMapper().writer().withDefaultPrettyPrinter()
        println(writer.writeValueAsString(JsonUnwarppedTest()))
    }

}