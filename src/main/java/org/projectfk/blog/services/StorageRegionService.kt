package org.projectfk.blog.services

import org.projectfk.blog.common.RandomStringGenerator
import org.projectfk.blog.data.StorageRegion
import org.projectfk.blog.data.StorageRegionRepo
import org.projectfk.blog.data.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StorageRegionService {

    @Autowired
    private lateinit var regionRepo: StorageRegionRepo

    fun requestNewRegion(
            user: User,
            name: String = RandomStringGenerator.getRandom(5)
    ) = regionRepo.save(StorageRegion(user, name))

}