package org.projectfk.blog.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.commons.logging.LogFactory
import org.projectfk.blog.common.ExceptionState
import org.projectfk.blog.common.ResultBean
import org.projectfk.blog.common.debugIfEnable
import org.projectfk.blog.data.User
import org.projectfk.blog.services.RecaptchaFatal
import org.projectfk.blog.services.RecaptchaVerificationService
import org.projectfk.blog.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@PropertySource("classpath:security_config.properties")
open class CustomAuthenticationFilter : UsernamePasswordAuthenticationFilter() {

    init {
        setAuthenticationSuccessHandler { _, response, authentication ->
            response.status = HttpStatus.OK.value()
//            response.addHeader("Location", successRedirectTarget)

            response.contentType = MediaType.APPLICATION_JSON_UTF8_VALUE

            val user = authentication.principal as User
            objectMapper
                    .writer()
                    .writeValue(response.writer, ResultBean(
                            user,
                            message = "welcome!"
                    ))
        }

        setAuthenticationFailureHandler { _, response, exception ->
            if (exception is NotAcceptableAuthenticationException) {
                response.status = HttpStatus.NOT_ACCEPTABLE.value()
                return@setAuthenticationFailureHandler
            }

            response.contentType = MediaType.APPLICATION_JSON_UTF8_VALUE

            response.status = HttpStatus.FORBIDDEN.value()

            if (exception is RecaptchaAuthorizationException) {
                objectMapper
                        .writer()
                        .writeValue(
                                response.writer,
                                ResultBean(null, state = ExceptionState(exception.message ?: ""))
                        )
            } else {
                objectMapper
                        .writer()
                        .writeValue(
                                response.writer,
                                ResultBean(null, state = ExceptionState("Name or Password is wrong."))
                        )
            }
        }

    }

    @Value("\${login.checkRecaptcha}")
    private lateinit var _enableRecaptcha: String

    @Value("\${login.successRedirect}")
    private lateinit var successRedirectTarget: String

    private val enableRecaptcha: Boolean by lazy {
        !(_enableRecaptcha.toLowerCase().equals("false"))
    }

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var verificationService: RecaptchaVerificationService

    private val LOG = LogFactory.getLog(CustomAuthenticationFilter::class.java)!!

    /**
     * Will throw BadCredentialsException or BadRequestAuthorizationException
     */
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {

//        Filter level do not have any
        val acceptHeader = request.getHeader("accept") ?: request.getHeader("Accept")
        if (!(
                        acceptHeader == null ||
                                acceptHeader.contains("*/*") ||
                                acceptHeader.contains("json", true)))
            throw NotAcceptableAuthenticationException()

        val body = obtainUserDTO(request) ?: throw BadRequestAuthorizationException()

        val (name, password) = body.obtainNameAndPassword()

        if (!UserService.validateUserName(name) || !UserService.validatePassword(password))
            throw BadCredentialsException("Name or Password is wrong.")

        LOG.debugIfEnable { "$name is attempting to authorize" }

        val validate: CompletableFuture<Pair<Boolean, String>>

        if (enableRecaptcha) validate = verificationService.validate(body.recaptcha_token)
        else validate = CompletableFuture.completedFuture(true to "Skiped recaptcha by configuration")

        val token = UsernamePasswordAuthenticationToken(name, password)

        setDetails(request, token)

        val authentication: Authentication

        try {
            LOG.debugIfEnable { "Authorization with user: ${body.name} passing to authorization manager" }
            authentication = this.authenticationManager.authenticate(token)
            LOG.debugIfEnable { "Authorization with authorization manager success with authentication $authentication" }
        } catch (e: Throwable) {
            LOG.debugIfEnable { "Authorization with user: ${body.name} failed with an exception ${e.javaClass}" }
            validate.cancel(true)
            throw e
        }

        try {
            val (validationResult, failMessage) = validate.get()
            if (!validationResult) {
                LOG.info("User ${body.name} failed to pass recaptcha vaildation, fail message: $failMessage")
                throw RecaptchaAuthorizationException("Recaptcha Failed: $failMessage")
            }
        } catch (e: CancellationException) {
            throw InternalAuthenticationServiceException("Internal Error", e)
        } catch (e: InterruptedException) {
            throw InternalAuthenticationServiceException("Internal Error", e)
        } catch (e: ExecutionException) {
            val baseException = e.cause ?: throw InternalAuthenticationServiceException("Internal Error", e)
            if (baseException is RecaptchaFatal)
                throw InternalAuthenticationServiceException(baseException.message, baseException)
            throw InternalAuthenticationServiceException("Internal Error", baseException)
        }

        return authentication
    }

    private fun obtainUserDTO(request: HttpServletRequest): UserAuthorizationDTO? {
        val paraMap = request.parameterMap
//        Already parased by framework
//        Probably form-data or x-www-form-urlencoded
        if (paraMap != null && paraMap.isNotEmpty()) {
            val name = paraMap["name"]?.elementAtOrNull(0)
            val password = paraMap["password"]?.elementAtOrNull(0)
            val recaptcha_token = paraMap["recaptcha_token"]?.elementAtOrNull(0)
            if (name == null || password == null || recaptcha_token == null)
                return null
            return UserAuthorizationDTO(name, password, recaptcha_token)
        }
        val requestContent = request.reader.readText()
        if (requestContent.length < 2) return null
        return objectMapper.readValue(requestContent)
    }

    protected fun UserAuthorizationDTO.obtainNameAndPassword(): Pair<String, String> = this.name to this.password

}

class UserAuthorizationDTO(
        val name: String,
        val password: String,
        val recaptcha_token: String
)

class BadRequestAuthorizationException : AuthenticationException("Bad Request")

private class NotAcceptableAuthenticationException : AuthenticationException("Content Negotiation Not Acceptable")

private class RecaptchaAuthorizationException(msg: String) : BadCredentialsException(msg)