package org.projectfk.blog.controller

import org.projectfk.blog.common.NotFoundException
import org.projectfk.blog.common.ResultBean
import org.projectfk.blog.data.User
import org.projectfk.blog.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/user/")
class UserController {

    @Autowired
    private lateinit var userService: UserService

    @GetMapping("{id}")
    fun getUser(
            @PathVariable("id")
            id: Int): ResultBean<User> = ResultBean(userService.findByID(id).orElseThrow(::NotFoundException))

    @PostMapping("registry")
    fun createUser(@RequestParam("name") name: String): ResponseEntity<User> {
        val created = userService.registryNewUser(name)
        return ResponseEntity.created(URI.create("/user/${created.id}")).body(created)
    }
    
}