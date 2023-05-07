package com.ucb.bo.sktmsuser.bl

import com.ucb.bo.sktmsuser.dao.UserDao
import com.ucb.bo.sktmsuser.dto.ResponseDto
import com.ucb.bo.sktmsuser.model.CustomException
import com.ucb.bo.sktmsuser.service.KeycloakService
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
@AllArgsConstructor
@NoArgsConstructor
class AdminUserBl @Autowired constructor(
    private val keycloakBl: KeycloakBl,
    private val userDao: UserDao,
){

    fun enableUser(userId: Long, enabled: Boolean?): ResponseEntity<ResponseDto<Any>> {
        try{
            val userEntity = userDao.findByUserId(userId)


            if (userEntity == null ||  enabled == null)
                throw CustomException("Algo salio mal, vuelve a intentarlo")

            if (userEntity.available != enabled){
                userEntity.available = enabled
                userDao.save(userEntity)
                keycloakBl.changeUserKeycloak(userEntity.userUuid, enabled)
            }

            return ResponseEntity(ResponseDto(null, "OK", true), HttpStatus.OK)
        }catch (ex: Exception){
            return ResponseEntity(ResponseDto(null, "Algo salio mal ${ex.message}", false), HttpStatus.BAD_REQUEST)
        } catch (cex: CustomException){
            return ResponseEntity( ResponseDto(null, cex.message, true), HttpStatus.BAD_REQUEST)
        }
    }

    fun getAllUser():ResponseEntity<ResponseDto<Any>>{
        val list = userDao.findAll();
        return ResponseEntity(ResponseDto(list, null, true), HttpStatus.OK)
    }

}