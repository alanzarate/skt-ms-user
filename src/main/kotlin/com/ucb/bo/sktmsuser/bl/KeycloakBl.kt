package com.ucb.bo.sktmsuser.bl

import com.ucb.bo.sktmsuser.dto.KeycloakTokenDto
import com.ucb.bo.sktmsuser.dto.KeycloakUserDto
import com.ucb.bo.sktmsuser.model.CustomException
import com.ucb.bo.sktmsuser.service.KeycloakService
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.keycloak.TokenVerifier
import org.keycloak.representations.AccessToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@AllArgsConstructor
@NoArgsConstructor
class KeycloakBl @Autowired constructor(
    private val keycloakService: KeycloakService,
){
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    @Value("\${custom-config.master-access-key}")
    lateinit var username: String

    @Value("\${custom-config.master-secret-key}")
    lateinit var password: String

    fun getKeycloakIdFromToken(token: String): String{
        try {
            val tokenValue: AccessToken = TokenVerifier.create(token, AccessToken::class.java).token
            return tokenValue.subject
            /*
            val maps: Map<String, Any> = mapOf(
                "subject" to tokenValue.subject,
                "scope" to tokenValue.scope,
                "resourceAccess" to tokenValue.resourceAccess.values.toString(),
                "authorization" to tokenValue.authorization,
                "realmAccess" to tokenValue.realmAccess,
                "isVerifyCaller" to tokenValue.isVerifyCaller,
                "getResourceAccess()" to tokenValue.resourceAccess,
                "phoneNumber" to tokenValue.phoneNumber,
                "locale" to tokenValue.locale,
                "email" to tokenValue.email,
                "birthdate" to tokenValue.birthdate,
                "category" to tokenValue.category,
                "picture" to tokenValue.picture,
                "emailVerified" to tokenValue.emailVerified,
                "preferredUsername" to tokenValue.preferredUsername
            )

             */

        }catch (ex: Exception){
            logger.error("#getKeycloakIdFromToken: Hubo problemas para extrer el token:")
            throw CustomException("Hubo problemas para extrer el token");
        }

    }

    fun getUserInfoFromKeycloak(userId: String): KeycloakUserDto {
        val masterToken = getKeycloakDtoMaster()
        logger.info("#getUserInfoFromKeycloak: solicita informacion de keycloak, fue a API-KEYCLOAK")
        return keycloakService.getUserInformation(
            "Bearer ${masterToken.access_token}",
            userId
        )

    }
    private fun getKeycloakDtoMaster() : KeycloakTokenDto {
        val mapBody: Map<String,String> = mapOf(
            "client_id" to "admin-cli",
            "username" to username,
            "password" to password,
            "grant_type" to "password"
        )
        return  keycloakService.getMasterToken(mapBody)
    }

    fun changeUserKeycloak(userUuid: String, enabled: Boolean) {
        val masterToken = getKeycloakDtoMaster()
        val mapBody: Map<String,Any> = mapOf(
            "enabled" to enabled
        )
         keycloakService.updateEnabledUser(
             "Bearer ${masterToken.access_token}",
             userUuid ,
             mapBody
             )
    }

    fun updateUserKeycloak(userUuid: String,  fName: String, lName: String,
                           email:String ){

        val mapBody: Map<String,Any> = mapOf(
            "firstname" to fName,
            "lastname" to lName,
            "email" to email,
            "emailVerified" to false
        )
        val masterToken = getKeycloakDtoMaster()
        keycloakService.updateUserInfo(
            "Bearer ${masterToken.access_token}",
            userUuid,
            mapBody
        )

    }


}