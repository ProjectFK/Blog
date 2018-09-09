package org.projectfk.blog.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ResponseStatus
import java.io.Serializable
import java.net.URI

@JsonPropertyOrder("state", "message", "result")
@JsonInclude(Include.NON_NULL)
open class ResultBean<T>(
        result: T?,

        @field:JsonProperty
        val message: String? = null,

        state: State = SuccessState
) : Serializable {

    @JsonProperty
    @field:JsonInclude(Include.NON_NULL)
    val result = result

    @JsonUnwrapped
    @JsonProperty
    val state = state

}

@JsonPropertyOrder("state", "state_message")
sealed class State(
        val state: String,
        @field:JsonInclude(Include.NON_EMPTY)
        val state_message: String
)

@ResponseStatus(code = HttpStatus.OK)
object SuccessState : State("success", "")

@ResponseStatus(code = HttpStatus.FORBIDDEN)
class ExceptionState(
        @JsonInclude(Include.NON_EMPTY)
        val exception_msg: String
) : State("failed", "exception")

@ResponseStatus(code = HttpStatus.CREATED)
private class CreatedState(
        @JsonProperty("location")
        val location: String
) : State("success", "created")

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
object ErrorState : State("failed", "Internal Error (whaaaaaaaaaat!)")

fun <T> created(
        uri: URI,
        content: T,
        builder: ResponseEntity.BodyBuilder = ResponseEntity.created(uri)
): ResponseEntity<ResultBean<CreatedResponseBody<T>>> =
        builder.body(ResultBean(CreatedResponseBody(content), state = CreatedState(uri.toString())))

class CreatedResponseBody<T> internal constructor(
        @JsonUnwrapped
        val content: T
)

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
object BadRequestState : State("failed", "bad request")

class ExceptionResultBean(
        message: String = "Exception occured, process failed",
        exception_msg: String = ""
) : ResultBean<Any>(
        null,
        message,
        state = ExceptionState(exception_msg)
)