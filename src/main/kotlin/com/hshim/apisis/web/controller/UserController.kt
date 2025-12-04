package com.hshim.apisis.web.controller

import com.hshim.apisis.user.service.UserCommandService
import com.hshim.apisis.user.service.UserUtil
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/web/user")
class UserController(
    private val userCommandService: UserCommandService,
) {
    @DeleteMapping
    fun delete() {
        val userId = UserUtil.getCurrentUserId()
        userCommandService.delete(userId)
    }
}