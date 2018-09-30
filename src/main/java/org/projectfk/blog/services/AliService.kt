package org.projectfk.blog.services

import com.aliyun.oss.ClientConfiguration
import com.aliyun.oss.OSSClient
import com.aliyun.oss.common.auth.DefaultCredentialProvider
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern

@PropertySource("classpath:ali_config.properties")
@Configuration
open class OSSService {

    @Value("\${oss.endpoint}")
    private lateinit var endpoint: String

    @Value("\${ali.accessKeyID}")
    private lateinit var accessKeyID: String

    @Value("\${ali.accessKeySecret}")
    private lateinit var accessKeySecret: String

    @get:Bean
    open val ossClient by lazy {
        OSSClient(endpoint, DefaultCredentialProvider(accessKeyID, accessKeySecret), ClientConfiguration())
    }


}

@PropertySource("classpath:ali_config.properties")
@Service
open class STSService {

    @Value("\${ali.accessKeyID}")
    private lateinit var accessKeyID: String

    @Value("\${ali.accessKeySecret}")
    private lateinit var accessKeySecret: String

    @Value("\${ali.roleArn}")
    private lateinit var roleArn: String

    @Value("\${sts.endpoint}")
    private lateinit var endPoint: String

    private val acsClient by lazy {
        val profile = DefaultProfile.getProfile(endPoint, accessKeyID, accessKeySecret)
        DefaultAcsClient(profile)
    }

    private val roleSessionNameRegex = Pattern.compile("^[a-zA-Z0-9.@\\-_].{0,32}\$")

    @Async
    open fun requestUploadOSSSTS(name: String, policy: List<STSPolicyStatement>): CompletableFuture<AssumeRoleResponse> {
        if (!roleSessionNameRegex.matcher(name).matches())
            throw IllegalArgumentException("role session name do not match requirement. Input: $name")
        val assumeRoleRequest = AssumeRoleRequest()
        assumeRoleRequest.method = MethodType.POST
        assumeRoleRequest.roleArn = roleArn
        assumeRoleRequest.policy = policyGen(policy)
        assumeRoleRequest.roleSessionName = name
        return CompletableFuture.supplyAsync { acsClient.getAcsResponse(assumeRoleRequest) }
    }

    private fun policyGen(policies: List<STSPolicyStatement>) =
            ObjectMapper().writeValueAsString((Policy(policies)))

    class STSPolicyStatement(
            @get:JsonGetter("Action")
            val action: Array<String>,
            @get:JsonGetter("Resource")
            val resource: Array<String>
    ) {
        @get:JsonGetter("Effect")
        val effect = "Allow"
    }

    private class Policy(
            @get:JsonGetter("Statement")
            val statement: List<STSPolicyStatement>) {
        @get:JsonGetter("Version")
        val version = "1"
    }

}

@Service
class OSSSTSService {

    @Autowired
    private lateinit var stsService: STSService

    fun obtainSTS(
            name: String,
            action: String = "PutBucket",
            bucketWithPath: Array<String>
    ): CompletableFuture<AssumeRoleResponse> {
        return stsService
                .requestUploadOSSSTS(
                        name,
                        generatePolicyForOSS(action, bucketWithPath)
                )
    }

    fun generatePolicyForOSS(action: String, bucketWithPath: Array<String>): List<STSService.STSPolicyStatement> {
        return listOf(
                STSService.STSPolicyStatement(
                        action = arrayOf("oss:$action"),
                        resource = bucketWithPath
                                .asSequence()
                                .map { a -> "acs:oss:*:*:$a" }
                                .toList()
                                .toTypedArray()
                )
        )
    }

}