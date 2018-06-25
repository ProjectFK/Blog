package org.projectfk.blog.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonUnwrapped
import java.io.Serializable

@JsonPropertyOrder("state", "result")
@JsonInclude(Include.NON_NULL)
open class ResultBean<T>(
        result: T?,

        @field:JsonProperty
        val message: String? = null,

        state: State = State.SuccessState
) : Serializable {

    @JsonUnwrapped
    @JsonProperty
    @field:JsonInclude(Include.NON_NULL)
    val result = result

    @JsonUnwrapped
    @JsonProperty
    val state = state


}

class StateResultBean(state: State) : ResultBean<State>(null, state = state)

sealed class State(val state: String) {

    object SuccessState : State("success!")

    @JsonPropertyOrder("state", "exception message")
    class ExceptionState(
            @JsonProperty("exception message")
            val exception_msg: String) : State("exception :(") {
//        KnownException can not pass null message into KnownException.message
        constructor(knownException: KnownException) : this(knownException.message!!)
    }

    object ErrorState : State("Internal Error (whaaaaaaaaaat!)")

}