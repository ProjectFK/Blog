package org.projectfk.blog.services

import com.vaptcha.Vaptcha
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
open class VaptchaService(
//        this two object is passed from configurations.secrets.VaptchaConfig
        @Autowired
        val vaptcha: Vaptcha,
        @Autowired
        val senseID: String
) {

        @Async
        open fun newChallenge(senseID: String = this.senseID): CompletableFuture<String>
                = CompletableFuture.supplyAsync { vaptcha.getChallenge(senseID) }

        @Async
        open fun verify(challenge: String, token: String, senseID: String = this.senseID): CompletableFuture<Boolean>
                = CompletableFuture.supplyAsync { vaptcha.validate(challenge, token, senseID) }

}
