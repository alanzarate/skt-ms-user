package com.ucb.bo.sktmsuser.controller

import com.ucb.bo.sktmsuser.bl.UserBl
import com.ucb.bo.sktmsuser.dto.AddressDto
import com.ucb.bo.sktmsuser.dto.CardDto
import com.ucb.bo.sktmsuser.dto.ResponseDto
import com.ucb.bo.sktmsuser.exception.AuthenticationException
import com.ucb.bo.sktmsuser.exception.ParameterException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import lombok.extern.slf4j.Slf4j
import org.springframework.web.bind.annotation.*

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
class UserController (
    private val userBl: UserBl
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /*
    READ: this function gets the information about a user, based on token
    first checks the main db, if not found, then checks keycloak db and save in main-db
     */
    @GetMapping("/information")
    fun getUserInformation(
        @RequestHeader headers: Map<String, String> ,
        @RequestParam customQuery:Map<String, String>
    ): ResponseEntity<ResponseDto<*>>{
        val token = headers["authorization"]?.substring(7)
            ?: return ResponseEntity(
                ResponseDto(null, "Parametro 'authorization' ausente", false),
                HttpStatus.FORBIDDEN)
        logger.info("#getUserInformation: El usuario requiere su propia informacion")

        return ResponseEntity(
            userBl.getUserInformation(token) ,
            HttpStatus.OK)

    }



    @PutMapping("/{userId}/information/update")
    fun updateUserInformation(
        @PathVariable userId: Long,
        @RequestBody body: Map<String, Any>,
        @RequestHeader headers: Map<String, String> ,
    ): ResponseEntity<ResponseDto<Any>>{
        val token = headers["authorization"]!!.substring(7)

        return userBl.updateUser( userId, token ,
            body["firstname"] as String?, body["lastname"] as String?, body["email"] as String? , body["cel"] as String?  )
    }

    @PostMapping("/{userId}/address/new")
    fun createNewAddressForUser(
        @RequestBody body: AddressDto,
        @PathVariable userId: Long,
        @RequestHeader headers: Map<String, String> ,
    ): ResponseEntity<ResponseDto<Any>>{
        val token = headers["authorization"]!!.substring(7)
        return userBl.createNewAddressForUser( userId, token ,
            body.address, body.latitude,  body.longitude )
    }

    @PutMapping("/{userId}/address/{id}")
    fun editAddressById(
        @RequestBody body: AddressDto,
        @RequestHeader headers: Map<String, String> ,
        @PathVariable id: Long,
        @PathVariable userId: Long
    ): ResponseEntity<ResponseDto<Any>>{
        val token = headers["authorization"]!!.substring(7)
        return userBl.editAddressById( userId, token ,
            body.address, body.latitude,  body.longitude , id)
    }

    @GetMapping("/{userId}/address/all")
    fun getAddressForUser(
        @RequestHeader headers: Map<String, String> ,
        @PathVariable userId: Long
    ): ResponseEntity<ResponseDto<Any>>{
        val token = headers["authorization"]!!.substring(7)
        return userBl.getAddressForUser( userId, token)
    }



    @GetMapping("/{userId}/cards")
    fun getCardsOfUser(
        @RequestHeader headers: Map<String, String> ,
        @PathVariable userId: Long
    ): ResponseEntity<ResponseDto<Any>>{
        val token = headers["authorization"]!!.substring(7)
        try{
            val res = userBl.getCardsOfUser( userId, token)
            return ResponseEntity
                .ok()
                .body(
                    ResponseDto( res,null, true)
                )
        }catch (aex: AuthenticationException){
            logger.info("Usuario no authorizado accediendo a registros de tercer los disenios ${userBl.getSubjectOfToken(token)}")
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, aex.message, false )
                )
        }catch (ex: Exception){
            logger.info("Ocurrio una exception consiguiendo una tarjeta ${ex.message}")
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, ex.message, false )
                )
        }

    }

    @PostMapping("/{userId}/cards")
    fun createCard(
        @RequestHeader headers: Map<String, String> ,
        @PathVariable userId: Long,
        @RequestBody body: CardDto
    ): ResponseEntity<ResponseDto<Any>>{
        val token = headers["authorization"]!!.substring(7)
        try{
            val res = userBl.createNewCard( userId, body, token)
            return ResponseEntity
                .ok()
                .body(
                    ResponseDto( res,"Created Successfully", true)
                )
        }catch (aex: AuthenticationException) {
            logger.info(
                "Usuario no authorizado accediendo a registros de tercer los disenios ${
                    userBl.getSubjectOfToken(
                        token
                    )
                }"
            )
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, aex.message, false)
                )
        }catch (pex: ParameterException){
            logger.info("Ocurrio una exception consiguiendo una tarjeta ${pex.message}")
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, pex.message, false )
                )
        }catch (ex: Exception){
            logger.info("Ocurrio una exception consiguiendo una tarjeta ${ex.message}")
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, ex.message, false )
                )
        }

    }

    @DeleteMapping("/{userId}/cards/{cardId}")
    fun deleteCard(
        @RequestHeader headers: Map<String, String> ,
        @PathVariable userId: Long,
        @PathVariable cardId: Long,
    ): ResponseEntity<ResponseDto<Any>>{
        val token = headers["authorization"]!!.substring(7)
        try{
            val res = userBl.deleteLogicalCard( userId, cardId, token)
            return ResponseEntity
                .ok()
                .body(
                    ResponseDto( res,"Deleted Successfully", true)
                )
        }catch (aex: AuthenticationException) {
            logger.info(
                "Usuario no authorizado accediendo a registros de tercer los disenios ${
                    userBl.getSubjectOfToken(
                        token
                    )
                }"
            )
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, aex.message, false)
                )
        }catch (pex: ParameterException){
            logger.info("Ocurrio una exception consiguiendo una tarjeta ${pex.message}")
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, pex.message, false )
                )
        }catch (ex: Exception){
            logger.info("Ocurrio una exception consiguiendo una tarjeta ${ex.message}")
            return ResponseEntity
                .badRequest()
                .body(
                    ResponseDto(null, ex.message, false )
                )
        }

    }



}