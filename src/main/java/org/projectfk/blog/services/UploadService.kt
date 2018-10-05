package org.projectfk.blog.services

import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse
import org.projectfk.blog.data.AttachmentRepo
import org.projectfk.blog.data.StorageRegion
import org.projectfk.blog.data.StorageRegionRepo
import org.projectfk.blog.data.User
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class UploadService(targetBucket: String, pathPrefix: String) {

    @Autowired
    private lateinit var attachmentRepo: AttachmentRepo

    private val attachmentLock = ReentrantLock()

    @Autowired
    private lateinit var requestRepo: StorageRegionRepo

    @Autowired
    private lateinit var OSSSTSService: OSSSTSService

    private val compiledBucketWithPathPrefix by lazy {
        val value = "$targetBucket/$pathPrefix"
        if (!value.endsWith("/")) "$value/"
        else value
    }

    protected fun requestToken(
            user: User,
            region: StorageRegion,
            duration: Duration = OSSSTSService.stsService.durationDefault
    ): CompletableFuture<AssumeRoleResponse.Credentials> =
            OSSSTSService.obtainSTS("user@${user.id}-region@${region.name}",
                    bucketWithPath = arrayOf(compiledBucketWithPathPrefix + region),
                    duration = duration
            ).thenApply {
                it.credentials
            }

    fun cleanIndexIfNoReference(region: StorageRegion): CompletableFuture<Boolean> {
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