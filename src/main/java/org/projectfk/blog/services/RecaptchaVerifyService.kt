package org.projectfk.blog.services

import org.apache.commons.logging.LogFactory
import org.projectfk.blog.common.KnownException
import org.projectfk.blog.common.debugIfEnable
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
@PropertySource("classpath:recaptcha_config.properties")
open class RecaptchaVerifyService {

    private val logger = LogFactory.getLog(RecaptchaVerifyService::class.java)!!

    private val validateURL by lazy {
        "https://www.recaptcha.net/recaptcha/api/siteverify?secret=${site_key}&response=%s"
    }

    private val formatter = Formatter()

    @Value("\${recaptcha.site_key}")
    private lateinit var site_key: String

    private val restTemplate = RestTemplate()

    @Async
    open fun validate(token: String): CompletableFuture<Pair<Boolean, String>> {
        logger.debugIfEnable { "validating token: $token" }

        return CompletableFuture.supplyAsync {
            var product: Pair<Boolean, String> = true to "success!"
            val url = formatter.format(validateURL, token).toString()
            try {
                logger.debugIfEnable { "request valid via url: $url" }

                val returnV: Map<String, Any> = restTemplate.postForObject(url)
                        ?: throwError(NullPointerException("server returned nothing"))

                if (returnV["error-codes"] != null) {
                    val errorCodes = returnV["error-codes"] as List<*>
                    logger.debugIfEnable {
                        "error-codes property in validation return value occurs! error-code: " +
                                errorCodes.joinToString { any -> any.toString() + " " }
                    }
                    if (errorCodes.contains("invalid-input-response")) product = false to "invalid token"
                    if (errorCodes.contains("timeout-or-duplicate")) product = false to "timeout or duplicate"
                    val message = "unaccepted error-codes received from recaptcha server in validating recaptcha token!" +
                            "\n error codes: ${errorCodes.joinToString { any -> any.toString() }}"
                    logger.error(message)
                    throwError(IllegalStateException(message))
                }

                if (returnV["success"] != true) product = false to "recaptcha says you're a robot"

                if (!product.first)
                    logger.debugIfEnable { "recaptcha validate failed, message: ${product.second}" }
                else
                    logger.debugIfEnable {
                        "recaptcha validation passed! reported time: ${returnV["challenge_ts"]} " +
                                "on host: ${returnV["hostname"]}"
                    }


                return@supplyAsync product
            } catch (e: RestClientException) {
                logger.warn("RestClientException while validating recaptcha token", e)
                throwError(e)
            }
        }
    }

    private fun throwError(cause: Throwable): Nothing = throw RecaptchaFatal(
            "server is unable to connect to recaptcha service to valid your request, " +
                    "try again or contact manager (Brad Wu 08426)",
            cause
    )


}

sealed class RecaptchaException(msg: String, cause: Throwable? = null) : KnownException(msg, cause)

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class RecaptchaFatal(msg: String, cause: Throwable) : RecaptchaException(msg, cause)

@ResponseStatus(HttpStatus.FORBIDDEN)
class RecaptchaFailed(msg: String) : RecaptchaException(msg)