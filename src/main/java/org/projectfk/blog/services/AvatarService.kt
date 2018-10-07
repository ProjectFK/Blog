package org.projectfk.blog.services

import org.projectfk.blog.data.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AvatarService(
        @Value("\${avatar.bucket}")
        targetBucket: String,
        @Value("\${avatar.pathPrefix}")
        pathPrefix: String
) : UploadService(targetBucket, pathPrefix) {

    @Autowired
    private lateinit var storageRegionService: StorageRegionService

    fun newUploadAttempt(user: User) =
            super.requestToken(user, storageRegionService.requestNewRegion(user), STSService.defaultDuration)


}