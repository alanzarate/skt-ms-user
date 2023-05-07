package com.ucb.bo.sktmsuser.service

import com.ucb.bo.sktmsuser.config.ConfigFeignClient
import com.ucb.bo.sktmsuser.dto.KeycloakTokenDto
import com.ucb.bo.sktmsuser.dto.KeycloakUserDto
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader


@FeignClient(name = "keycloak",
        url="\${keycloak.auth-server-url}",
        configuration = [ConfigFeignClient::class])
interface KeycloakService {
    @PostMapping(value = ["/realms/master/protocol/openid-connect/token"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun getMasterToken(@RequestBody body: Map<String, *>):KeycloakTokenDto

    @GetMapping(value= ["/admin/realms/\${keycloak.realm}/users/{user-id}"])
    fun getUserInformation(@RequestHeader("Authorization") token: String, @PathVariable(value = "user-id") userId: String): KeycloakUserDto

    @PutMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateEnabledUser(@RequestHeader("Authorization") token: String, @PathVariable(value = "user-id") userId: String, @RequestBody body:Map<String, *>)

    @PutMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateUserInfo(@RequestHeader("Authorization") token: String, @PathVariable(value = "user-id") userId: String, @RequestBody body:Map<String, *>)

}