package org.projectfk.blog.services

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class STSServiceTest {

    @Autowired
    private lateinit var STSService: STSService

    @Test
    fun requestUploadOSSSTS() {
        val future = STSService.requestUploadOSSSTS("test",
                arrayListOf(
                        org.projectfk.blog.services.STSService.STSPolicyStatement(
                                arrayOf("oss:ListObjects"),
                                arrayOf("acs:oss:*:*:somethinglalalala")
                        )
                )
        )
        future.get()
    }
}