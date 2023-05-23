package com.ucb.bo.sktmsuser.service

import com.ucb.bo.sktmsuser.config.ConfigFeignClient
import com.ucb.bo.sktmsuser.dto.KcRolesDto
import com.ucb.bo.sktmsuser.dto.KeycloakTokenDto
import com.ucb.bo.sktmsuser.dto.KeycloakUserDto
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
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

    @GetMapping(value= ["/admin/realms/\${keycloak.realm}/users"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllUsersInformation(@RequestHeader("Authorization") token: String): List<KeycloakUserDto>

    @GetMapping(value= ["/admin/realms/\${keycloak.realm}/roles"] , consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllRolesInRealm(@RequestHeader("Authorization") token: String): List<KcRolesDto>

    /*
    REQUEST BODY:
    { "type": "password",
     "temporary": false,
     "value": "NEW_PASSWORD"
    }
     */
    @PutMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/reset-password"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun changePasswordOfUser(@RequestHeader("Authorization") token: String,
                             @PathVariable(value = "user-id") userId: String,
                             @RequestBody body:Map<String, *>)


    /*
    RESPONSE BODY:
    [
        {
            "id": "uuuid",
            "name": "name",
            "path": "/path",
            "subgroups" : [ ------ GOES OTHER GROUP -------- ]

        }
    ]
     */
    @GetMapping(value = ["/admin/realms/\${keycloak.realm}/groups"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllGroups(@RequestHeader("Authorization") token: String)


    /*
    RESPONSE BODY:
    [
	{
		"id": "9e229c0c-589d-47fd-b987-d85e01159f14",
		"name": "SELLER",
		"path": "/SELLER"
	},
	{
		"id": "c5c6c468-1cb9-435a-8061-c5e2a38088a4",
		"name": "tes",
		"path": "/tes"
	}
]
     */
    @GetMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/groups"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getUsersGroup(@RequestHeader("Authorization") token: String,
                      @PathVariable(value = "user-id") userId: String)


    @PutMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/groups/{group-id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addUserToGroup(@RequestHeader("Authorization") token: String,
                      @PathVariable(value = "user-id") userId: String,
                       @PathVariable(value = "group-id") groupId: String)

    @DeleteMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/groups/{group-id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteUserFromGroup(@RequestHeader("Authorization") token: String,
                       @PathVariable(value = "user-id") userId: String,
                       @PathVariable(value = "group-id") groupId: String)


    /*
    RESPONSE BODY:
    [
	{
		"id": "9e229c0c-589d-47fd-b987-d85e01159f14",
		"name": "SELLER",
		"path": "/SELLER",
		"subGroups": [
			{
				"id": "df4e1517-c4eb-49cf-96dc-d7b07bd3e863",
				"name": "SUB-SELLER",
				"path": "/SELLER/SUB-SELLER",
				"subGroups": []
			}
		]
	}
]
     */
    @GetMapping(value = ["/admin/realms/\${keycloak.realm}/groups?search={name-group}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun searchGroupByName(@RequestHeader("Authorization") token: String,
                          @PathVariable(value = "name-group") nameGroup: String)



    /*
    RETURNS 201 CREATED
     REQUEST BODY :
     {
        "name": "secretary",
        "attributes": {
            "gid": [
                "1234"
            ]
        }
    }
     */
    @PostMapping(value = ["/admin/realms/\${keycloak.realm}/groups"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createGroup(@RequestHeader("Authorization") token: String,
                    @RequestBody body:Map<String, *>)


    @DeleteMapping(value = ["/admin/realms/\${keycloak.realm}/groups/{group-id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteGroup(@RequestHeader("Authorization") token: String,
                            @PathVariable(value = "group-id") groupId: String)


    /*
    RESPONSE BODY:
    [
        {
            "id": "12e5d04f-6e48-41db-9a6e-b36c2543050c",
            "name": "USER",
            "description": "",
            "composite": false,
            "clientRole": false,
            "containerId": "bb1c7a14-c003-455c-ba76-be14ba5a4205"
        }
    ]
     */
    @GetMapping(value = ["/admin/realms/\${keycloak.realm}/groups/{group-id}/role-mappings/realm"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getRolesOfGroup(@RequestHeader("Authorization") token: String,
                    @PathVariable(value = "group-id") groupId: String)


    /*
    RETURNS 204 NoContent
    REQUEST BODY:
    [
        {
            "id": "12e5d04f-6e48-41db-9a6e-b36c2543050c",
            "name": "USER"
        }
    ]
     */
    @PostMapping(value = ["/admin/realms/\${keycloak.realm}/groups/{group-id}/role-mappings/realm"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addRolesToGroup(@RequestHeader("Authorization") token: String,
                        @PathVariable(value = "group-id") groupId: String,
                        @RequestBody body:List<Map<String, *>>)


    /*
    RESPONSE: 204 No Content
    REQUEST BODY:
    [
        {
            "id": "12e5d04f-6e48-41db-9a6e-b36c2543050c",
            "name": "USER"
        }
    ]
     */
    @DeleteMapping(value = ["/admin/realms/\${keycloak.realm}/groups/{group-id}/role-mappings/realm"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteRoleOfGroup(@RequestHeader("Authorization") token: String,
                        @PathVariable(value = "group-id") groupId: String,
                          @RequestBody body:List<Map<String, *>>)

    /*
    RESPONSE:
    [
        {
            "id": "0e97f743-07a2-4dfa-90de-66cdbe9cf0fc",
            "name": "default-roles-testing2",
            "description": "${role_default-roles}",
            "composite": true,
            "clientRole": false,
            "containerId": "bb1c7a14-c003-455c-ba76-be14ba5a4205"
        },
        {
            "id": "12e5d04f-6e48-41db-9a6e-b36c2543050c",
            "name": "USER",
            "description": "",
            "composite": false,
            "clientRole": false,
            "containerId": "bb1c7a14-c003-455c-ba76-be14ba5a4205"
        }
    ]
     */
    @GetMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/role-mappings/realm"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getRolesOfUser(@RequestHeader("Authorization") token: String,
                          @PathVariable(value = "user-id") userId: String)


    /*
    RESPONSE: 204 NO content
    REQUEST BODY:

    [
        {
            "id": "0e97f743-07a2-4dfa-90de-66cdbe9cf0fc",
            "name": "default-roles-testing2"
        }
    ]
     */
    @DeleteMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/role-mappings/realm"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteRolesOfUser(@RequestHeader("Authorization") token: String,
                       @PathVariable(value = "user-id") userId: String,
                          @RequestBody body:List<Map<String, *>>)

    /*
    RESPONSE 204 NO CONTENT
    REQUEST BODY:
    [
        {
            "id": "0e97f743-07a2-4dfa-90de-66cdbe9cf0fc",
            "name": "default-roles-testing2"
        }
    ]
     */
    @PostMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/role-mappings/realm"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addRoleToUser(@RequestHeader("Authorization") token: String,
                          @PathVariable(value = "user-id") userId: String,
                          @RequestBody body:List<Map<String, *>>)


}