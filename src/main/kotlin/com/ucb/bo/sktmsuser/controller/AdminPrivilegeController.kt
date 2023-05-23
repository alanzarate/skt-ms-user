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
    /*
    Disable or enable user in Local BD and Keycloak
    PUT /api/v1/admin/user/{idUser}/status
    Ex.
    REQUEST BODY:
    {
        "enabled": true
    }

    "enabled" param determines if user gonna be enabled or disabled

     - status of user determines if user will be able to use protected endpoints
     */
    @PutMapping("/user/{idUser}/status")
    fun getStatusUser(
        @RequestBody body: Map<String, Any>,
        @PathVariable idUser: Long
    ): ResponseEntity<ResponseDto<Any>> {
        return adminUserBl.enableUser(idUser, body["enabled"] as Boolean?)
    }


    /*
    GET /api/v1/admin/user/all

    gets all user at once

    TODO: NEEDS PAGINATION
     */

    @GetMapping("/user/all")
    fun getAllUsers(

    ): ResponseEntity<ResponseDto<Any>>{
        return adminUserBl.getAllUser();
    }
}