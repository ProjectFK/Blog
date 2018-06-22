package org.projectfk.blog.controller

import com.vaptcha.Vaptcha
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/vaptcha/")
class VaptchaController {

    @Autowired
    private lateinit var vaptcha: Vaptcha

    @Autowired
    private lateinit var sceneId: String

    @GetMapping("getChallenge", produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getChallenge(): String = vaptcha.getChallenge(sceneId)

}