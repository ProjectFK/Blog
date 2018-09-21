package org.projectfk.blog.configurations

import org.projectfk.blog.security.CustomAuthenticationFilter
import org.projectfk.blog.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
@PropertySource("classpath:security_config.properties")
open class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Value("\${login.loginPath}")
    lateinit var loginPath: String

    @Value("\${login.logoutPath}")
    lateinit var logoutPath: String

    @Value("\${login.logoutSuccessRedirect}")
    lateinit var logoutSuccessRedirect: String

    @Autowired
    lateinit var userService: UserService

    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                    .antMatchers(loginPath).anonymous()
                .anyRequest().permitAll()
                .and()
                .addFilterAfter(authorizationFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .logout()
                .logoutUrl(logoutPath)
                .logoutSuccessUrl(logoutSuccessRedirect)
                .and()
                .csrf().disable()

//        Headers managed in reverse proxy server placed in front of this service
    }

    @Bean
    open fun authProvider(): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }

    @Autowired
    open fun configAuthProvider(builder: AuthenticationManagerBuilder) {
        builder.authenticationProvider(authProvider())
    }

    @Bean
    open fun authorizationFilter(): CustomAuthenticationFilter {
        val filter = CustomAuthenticationFilter()
        filter.setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher(loginPath, "POST"))
        filter.setPostOnly(true)
        filter.setAuthenticationManager(authenticationManager())
        return filter
    }

    @Bean
    open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}