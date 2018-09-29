package org.projectfk.blog.services

import org.projectfk.blog.data.AttachmentRepo
import org.projectfk.blog.data.TempUploadRequests
import org.projectfk.blog.data.TempUploadRequestsRepo
import org.projectfk.blog.data.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service
import java.security.SecureRandom
import javax.annotation.PostConstruct

@PropertySource("classpath:common_settings_config.properties")
@Service
class UploadService {

    @Autowired
    private lateinit var attachmentRepo: AttachmentRepo

    @Autowired
    private lateinit var requestRepo: TempUploadRequestsRepo

    @Autowired
    private lateinit var OSSSTSService: OSSSTSService

    @Value("\${upload.targetBucket}")
    private lateinit var targetBucket: String

    @Value("\${upload.pathPrefix}")
    private lateinit var pathPrefix: String

    private val compiledBucketWithPathPrefix by lazy {
        val value = "$targetBucket/$pathPrefix"
        if (!value.endsWith("/")) "$value/"
        else value
    }

    @PostConstruct
    fun clearRepo() = requestRepo.deleteAll()

    fun requestNewToken(user: User) {
        TODO("NOT FINISHED")
        val region = getRandom(5)
        val requestObj = requestRepo.save(TempUploadRequests(region, user))
        val sts = OSSSTSService.obtainSTS("${user.id}-$region",
                bucketWithPath = arrayOf(compiledBucketWithPathPrefix + region))
        sts.thenApply {
            it.credentials.securityToken
        }.thenApply {

        }
    }

    private val secureRandom = SecureRandom()
    private val allowed = "ABCDEFGJKLMNPRSTUVWXYZ0123456789"
    private val size = allowed.length

    private fun getRandom(stringLength: Int): String {
        val sb = StringBuilder(stringLength)
        for (i: Int in 1..stringLength) {
            sb.append(allowed[secureRandom.nextInt(size)])
        }
        return sb.toString()
    }


}