package org.projectfk.blog.controller.restful

import org.projectfk.blog.common.CreatedResponseBody
import org.projectfk.blog.common.NotFoundException
import org.projectfk.blog.common.ResultBean
import org.projectfk.blog.common.created
import org.projectfk.blog.data.User
import org.projectfk.blog.services.UserService
import org.projectfk.blog.services.supplyNotFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("api/user/")
class UserController {

    @Autowired
    private lateinit var userService: UserService

    @GetMapping("{name}")
    fun getUser(
            @PathVariable("name")
            name: String
    ): ResultBean<User> {
        try {
            val id = Integer.parseInt(name)
            return ResultBean(
                    userService
                            .findByID(id)
                            .orElseThrow { supplyNotFound(id) }
            )
        } catch (e: NumberFormatException) {}

        if (!UserService.validateUserName(name))
            throw supplyNotFound(name)

        return ResultBean(
                try {
                    userService
                            .loadUserByUsername(name)
                } catch (e: UsernameNotFoundException) {
                    throw NotFoundException(e.message!!)
                }
        )
    }

    @PostMapping("registry")
//    TODO: Place holder
    fun createUser(
            @RequestParam("name")
                name: String,
            @RequestParam("password", required = false, defaultValue = "")
                password: String
    ): ResponseEntity<ResultBean<CreatedResponseBody<User>>> {
        val body = userService.registryNewUser(name, password)
        return created(URI.create("/user/${body.id}"), body)
    }

}
