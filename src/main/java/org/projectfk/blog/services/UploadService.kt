package org.projectfk.blog.services

import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse
import org.projectfk.blog.data.AttachmentRepo
import org.projectfk.blog.data.StorageRegion
import org.projectfk.blog.data.StorageRegionRepo
import org.projectfk.blog.data.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@PropertySource("classpath:common_settings_config.properties")
@Service
class UploadService {

    @Autowired
    private lateinit var attachmentRepo: AttachmentRepo

    private val attachmentLock = ReentrantLock()

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

    fun requestNewTokenForUser(user: User): CompletableFuture<AssumeRoleResponse.Credentials> {
        val region = requestRepo.save(StorageRegion(user))
        val sts = OSSSTSService.obtainSTS("user@${user.id}-region@${region.name}",
                bucketWithPath = arrayOf(compiledBucketWithPathPrefix + region))
        return sts.thenApply {
            it.credentials
        }
    }

    fun cleanIfNoUpload(region: StorageRegion): CompletableFuture<Boolean>? {
        return CompletableFuture.supplyAsync {
            val dto = requestRepo.findById(region.name)
            if (!dto.isPresent) return@supplyAsync true
            attachmentLock.withLock {
                val attachments = attachmentRepo.findByRegion(dto.get())
                if (attachments.isNotEmpty()) return@supplyAsync false
                requestRepo.delete(dto.get())
            }
            true
        }
    }


}