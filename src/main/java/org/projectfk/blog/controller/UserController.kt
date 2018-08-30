package org.projectfk.blog.controller

import org.projectfk.blog.common.CreatedResponseBody
import org.projectfk.blog.common.NotFoundException
import org.projectfk.blog.common.ResultBean
import org.projectfk.blog.common.created
import org.projectfk.blog.data.User
import org.projectfk.blog.services.UserService
import org.projectfk.blog.services.findUserAsThisIsAnIDName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("api/user/")
class UserController {

    @Autowired
    private lateinit var userService: UserService

    @GetMapping("{id}")
    fun getUser(
            @PathVariable("id")
            id: Int
    ): ResultBean<User> = ResultBean(
            id
                    .findUserAsThisIsAnIDName()
                    .orElseThrow { NotFoundException("user with id: $id do not found") }
    )

    @PostMapping("registry")
//    TODO: Place holder
    fun createUser(@RequestParam("name") name: String): ResponseEntity<ResultBean<CreatedResponseBody<User>>> {
        val body = userService.registryNewUser(name, "")
        return created(URI.create("/user/${body.id}"), body)
    }
    
}

class UserAuthorizationDTO(
        val name: String,
        val password: String,
        val recaptcha_token: String,
        val time: Long
)