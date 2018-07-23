@file:Suppress("UNUSED_PARAMETER")

package org.projectfk.jackson

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test

class JsonCreatorTest {

    class stru{

        private constructor() {
            println("constructor called")
        }

        var id: Int = 0
        var msg: String = "233"

        companion object {
            val instance = stru()

//           Jvm Static !!! That's why!!!!!!!!!!!
            @JvmStatic
            @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
            fun jsonEntry(
                    @JsonProperty("id")
                    id: Int,
                    @JsonProperty("msg")
                    msg: String): stru {
                println("creator called")
                return stru()
            }

        }

    }

    @Test
    fun entry(): Unit {
        val mapper = ObjectMapper()
        val writer = mapper.writer()
        val jsonString = writer.writeValueAsString(stru.instance)
        println(jsonString)
        val readTree = mapper.readTree(jsonString)
        val convertValue = mapper.convertValue<stru>(readTree, stru::class.java)
        println(convertValue)
    }

}