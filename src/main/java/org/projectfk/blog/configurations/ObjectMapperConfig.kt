package org.projectfk.blog.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Configuration

@Configuration
open class ObjectMapperConfig {

    fun objectMapper(): ObjectMapper = jacksonObjectMapper()

}