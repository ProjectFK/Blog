package org.projectfk.blog.controller

import org.projectfk.blog.services.VaptchaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/vaptcha/")
class VaptchaController {

    @Autowired
    private lateinit var vaptchaService: VaptchaService

    @GetMapping("getChallenge", produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getChallenge(): CompletableFuture<String> = vaptchaService.newChallenge()

}