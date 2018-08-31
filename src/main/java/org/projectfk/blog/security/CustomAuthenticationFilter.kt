package org.projectfk.blog.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.commons.logging.LogFactory
import org.projectfk.blog.common.debugIfEnable
import org.projectfk.blog.services.RecaptchaInternalError
import org.projectfk.blog.services.RecaptchaVerifyService
import org.projectfk.blog.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomAuthenticationFilter : UsernamePasswordAuthenticationFilter() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var verifyService: RecaptchaVerifyService

    private val LOG = LogFactory.getLog(CustomAuthenticationFilter::class.java)!!

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val body = objectMapper.readValue<UserAuthorizationDTO>(request.reader.readText())

        if (!UserService.validateUserName(body.name) || !UserService.validatePassword(body.password))
            throw BadCredentialsException("Name or Password is wrong.")

        LOG.debugIfEnable { "${body.name} is attempting to authorize" }

        val validate = verifyService.validate(body.recaptcha_token)

        val token = UsernamePasswordAuthenticationToken(body.name, body.password)

        setDetails(request, token)

        val authentication: Authentication

        try {
            LOG.debugIfEnable { "Authorization with user: ${body.name} passing to authorization manager" }
            authentication = this.getAuthenticationManager().authenticate(token)
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
                throw BadCredentialsException("Recaptcha Failed: $failMessage")
            }
        } catch (e: CancellationException) {
            throw InternalAuthenticationServiceException("Internal Error", e)
        } catch (e: InterruptedException) {
            throw InternalAuthenticationServiceException("Internal Error", e)
        } catch (e: ExecutionException) {
            val baseException = e.cause
            if (baseException is RecaptchaInternalError)
                throw InternalAuthenticationServiceException("Internal Error", e)
        }

        return authentication
    }

}

class UserAuthorizationDTO(
        val name: String,
        val password: String,
        val recaptcha_token: String
)
