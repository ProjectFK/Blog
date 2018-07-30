package org.projectfk.blog

import com.fasterxml.jackson.annotation.JsonAnyGetter
import org.projectfk.blog.common.ResultBean
import org.springframework.http.ResponseEntity
import java.net.URI

fun <T> created(
        uri: URI,
        content: T,
        fieldName: String = "content",
        builder: ResponseEntity.BodyBuilder = ResponseEntity.created(uri)
): ResponseEntity<ResultBean<CreatedResponseBody<T>>> =
        builder.body(ResultBean(CreatedResponseBody(content, url = uri, fieldName = fieldName)))

class CreatedResponseBody<T> internal constructor(
        val content: T,
        val fieldName: String = "created object",
        val url: URI
) {

    @JsonAnyGetter
    fun jsonEntry(): Map<String, Any?> =
            mapOf(
                    fieldName to content,
                    "url" to url
            )

}

fun fasterStringCompare(one: String, another: String): Boolean {
    if (one.length != another.length) return false
    return one.hashCode() == another.hashCode()
}