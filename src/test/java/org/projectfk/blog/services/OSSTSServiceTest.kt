package org.projectfk.blog.services

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class OSSTSServiceTest {

    @Autowired
    private lateinit var service: OSSTSService

    @Test
    fun obtainSTS() {
        println(service.obtainSTS("test", bucketWithPath = arrayOf("somethinglalala/")).get())
    }
}