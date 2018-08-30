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

@JsonPropertyOrder("state", "result")
@JsonInclude(Include.NON_NULL)
open class ResultBean<T> (
        result: T?,

        @field:JsonProperty
        val message: String? = null,

        state: State = SuccessState
) : Serializable {

//    should I?
//    @JsonUnwrapped
    @JsonProperty
    @field:JsonInclude(Include.NON_NULL)
    val result = result

    @JsonUnwrapped
    @JsonProperty
    val state = state

}

class StateResultBean(state: State) : ResultBean<State>(null, state = state) {
    constructor() : this(SuccessState)
}

sealed class State(val state: String)


@ResponseStatus(code = HttpStatus.OK)
object SuccessState : State("success!")

@ResponseStatus(code = HttpStatus.FORBIDDEN)
@JsonPropertyOrder("state", "exception message")
class ExceptionState(
        @JsonProperty("exception message")
        val exception_msg: String
) : State("exception :(") {
    //        KnownException can not pass null message into KnownException.message
    constructor(knownException: KnownException) : this(knownException.message!!)
}

@ResponseStatus(code = HttpStatus.CREATED)
private class CreatedState(
        val url: String
) : State("created")

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
object ErrorState : State("Internal Error (whaaaaaaaaaat!)")

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