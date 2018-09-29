package org.projectfk.blog.services

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class OSSSTSServiceTest {

    @Autowired
    private lateinit var service: OSSSTSService

    @Test
    fun obtainSTS() {
        val get = service.obtainSTS("test", bucketWithPath = arrayOf("somethinglalala/")).get()
        println("ak:" + get.credentials.accessKeyId)
        println("as:" + get.credentials.accessKeySecret)
        println("security token:" + get.credentials.securityToken)
    }
}