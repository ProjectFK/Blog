package org.projectfk.blog.services

import org.projectfk.blog.data.AttachmentRepo
import org.projectfk.blog.data.StorageRegion
import org.projectfk.blog.data.StorageRegionRepo
import org.projectfk.blog.data.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@PropertySource("classpath:common_settings_config.properties")
@Service
class UploadService {

    @Autowired
    private lateinit var attachmentRepo: AttachmentRepo

    @Autowired
    private lateinit var requestRepo: StorageRegionRepo

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
        val region = requestRepo.save(StorageRegion(user))
        val sts = OSSSTSService.obtainSTS("user@${user.id}-region@${region.name}",
                bucketWithPath = arrayOf(compiledBucketWithPathPrefix + region))
        sts.thenApply {
            it.credentials.securityToken
        }.thenApply {

        }
    }


}