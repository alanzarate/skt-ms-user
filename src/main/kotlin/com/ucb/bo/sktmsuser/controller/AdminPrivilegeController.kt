package com.ucb.bo.sktmsuser.controller

import com.ucb.bo.sktmsuser.bl.AdminUserBl
import com.ucb.bo.sktmsuser.bl.UserBl
import com.ucb.bo.sktmsuser.dto.ResponseDto
import lombok.extern.slf4j.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@Slf4j
@RequestMapping("/api/v1/admin")
class AdminPrivilegeController (
    private val adminUserBl: AdminUserBl
){
    @PutMapping("/user/{idUser}/status")
    fun getStatusUser(
        @RequestBody body: Map<String, Any>,
        @PathVariable idUser: Long
    ): ResponseEntity<ResponseDto<Any>> {
        return adminUserBl.enableUser(idUser, body["enabled"] as Boolean?)
    }

    @GetMapping("/user/all")
    fun getAllUsers(

    ): ResponseEntity<ResponseDto<Any>>{
        return adminUserBl.getAllUser();
    }
}