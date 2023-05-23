package com.ucb.bo.sktmsuser.bl

import com.ucb.bo.sktmsuser.dao.AddressDao
import com.ucb.bo.sktmsuser.dao.UserDao
import com.ucb.bo.sktmsuser.dto.AddressResponseDto
import com.ucb.bo.sktmsuser.dto.CardDto
import com.ucb.bo.sktmsuser.dto.ResponseDto
import com.ucb.bo.sktmsuser.entity.AddressEntity
import com.ucb.bo.sktmsuser.entity.CardEntity
import com.ucb.bo.sktmsuser.entity.UserEntity
import com.ucb.bo.sktmsuser.exception.AuthenticationException
import com.ucb.bo.sktmsuser.exception.ParameterException
import com.ucb.bo.sktmsuser.model.CustomException
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service


@Service
@AllArgsConstructor
@NoArgsConstructor
class UserBl @Autowired constructor(
    private val keycloakBl: KeycloakBl,
    private val cardBl: CardBl,
    private val userDao: UserDao,
    private val addressDao: AddressDao,

){
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun getUserInformation(token: String): ResponseDto<Any> {
        try {
            val userIdToken = keycloakBl.getKeycloakIdFromToken(token);
            val userInDb = userDao.findByUserUuid(userIdToken);
            // if there is data in list, means that main db contains the user
            if (userInDb.isNotEmpty()) {
                logger.info("#getUserInformation: usuario ${userInDb[0].userId} + ${userInDb[0].userUuid} encontrado en la base de datos")
                return ResponseDto(userInDb[0], null, true)
            }
            // the user is not in main db
            // check for user in keycloak db
            logger.info("#getUserInformation: no encontrado en la base de datos - pero si por api keycloak $userIdToken")
            val userKeycloak = keycloakBl.getUserInfoFromKeycloak(userIdToken);
            // save the user information in db
            val userEntity = UserEntity(
                name = userKeycloak.firstName,
                available = true,
                emailAddress = userKeycloak.email,
                userUuid = userKeycloak.id,
                identityProvider = if (userKeycloak.federatedIdentities?.isNotEmpty() == true) userKeycloak.federatedIdentities!![0].identityProvider
                                    else null,
                lastname = userKeycloak.lastName)

            return ResponseDto(userDao.save(userEntity), null, true)
        }catch(cex:CustomException){
            return ResponseDto(null, "Algo salio mal", false)
        }
    }



    fun updateUser(userId: Long, token: String,
                   fName: String?, lName: String?, email:String? , cel: String?): ResponseEntity<ResponseDto<Any>>{
       val currentUser = validateUserIdAndToken(userId, token) ?: return ResponseEntity(ResponseDto(null, "Unauthorized", false), HttpStatus.UNAUTHORIZED)


        if(!email.isNullOrEmpty()) currentUser.emailAddress = email
        if(!fName.isNullOrEmpty()) currentUser.name = fName
        if(!lName.isNullOrEmpty()) currentUser.lastname = lName
        if(!cel.isNullOrEmpty()) currentUser.cel = cel
        userDao.save(currentUser)
        keycloakBl.updateUserKeycloak(currentUser.userUuid, currentUser.name, currentUser.lastname, currentUser.emailAddress )
        return ResponseEntity(ResponseDto(currentUser, null, true), HttpStatus.OK)

    }

    fun validateUserIdAndToken(userId: Long, token: String): UserEntity?{
        val userIdToken = getSubjectOfToken(token)
        val userInDb = userDao.findByUserUuid(userIdToken);
        if (userInDb.isEmpty()) return null
        val currentUser = userInDb[0]
        if(userId != currentUser.userId) return null
        return currentUser
    }
    fun getSubjectOfToken(token: String):String{
        return keycloakBl.getKeycloakIdFromToken(token)
    }

    fun createNewAddressForUser(userId: Long, token: String, address: String, latitude: Double, longitude: Double): ResponseEntity<ResponseDto<Any>>{
        val currentUser = validateUserIdAndToken(userId, token) ?: return ResponseEntity(ResponseDto(null, "Unauthorized", false), HttpStatus.UNAUTHORIZED)
        val addressEntity = AddressEntity(
            address = address,
            available = true,
            latitude = latitude,
            longitude = longitude,
            userEntity = currentUser
        )

        addressDao.save(addressEntity)
        return ResponseEntity(ResponseDto(addressEntity, null, true), HttpStatus.CREATED)

    }

    fun editAddressById(userId: Long, token: String, address: String?, latitude: Double?, longitude: Double?, idAddress: Long): ResponseEntity<ResponseDto<Any>>{
        val currentUser = validateUserIdAndToken(userId, token)
            ?: return ResponseEntity(ResponseDto(null, "Unauthorized", false), HttpStatus.UNAUTHORIZED)
        //val addressEntity = idAddress?.let { addressDao.findById(idAddress) }
        //    ?: return ResponseEntity(ResponseDto(null, "NOT FOUND", false), HttpStatus.NOT_FOUND)
         val addressEntity: AddressEntity = addressDao.findById(idAddress)
             .orElseThrow{ ResourceNotFoundException("Address id: $idAddress , not found" ) }

        if(!address.isNullOrEmpty()) addressEntity.address = address
        latitude?.let {
            addressEntity.latitude = it
        }
        longitude?.let { addressEntity.longitude = it }

        /*
        a?.let {
               println("not null")
               println("Wop-bop-a-loom-a-boom-bam-boom")
            } ?: run {
                println("null")
                println("When things go null, don't go with them")
            }
         */
        addressDao.save(addressEntity)
        return ResponseEntity(ResponseDto(addressEntity, null, true), HttpStatus.CREATED)

    }

    fun getAddressForUser(userId: Long, token: String):ResponseEntity<ResponseDto<Any>>{
        val currentUser = validateUserIdAndToken(userId, token)
            ?: return ResponseEntity(ResponseDto(null,"Unauthorized", false), HttpStatus.UNAUTHORIZED)
        val listAdd: ArrayList<AddressResponseDto> = ArrayList()
        addressDao.findByUserEntity(currentUser).forEach {
            value -> listAdd.add(
            AddressResponseDto(
                address = value.address,
                available = value.available,
                longitude = value.longitude,
                latitude = value.latitude,
                addressId = value.addressId
            )
        )
        }


        return ResponseEntity(ResponseDto(listAdd, null, true) , HttpStatus.OK)
    }

    fun getCardsOfUser(userId: Long, token: String): ArrayList<CardDto> {
        val currentUser = validateUserIdAndToken(userId, token)
            ?: throw AuthenticationException("UNAUTHORIZED")
        val currentCardsOfUser:ArrayList<CardDto> = ArrayList()
        cardBl.getCardsByUser(currentUser).forEach {
            value -> currentCardsOfUser.add(
                CardDto(
                    dateExp = value.dateExp,
                    lastNumber = value.lastNumber
                )
            )
        }
        return currentCardsOfUser

    }

    fun createNewCard(userId: Long, body: CardDto, token: String): CardEntity {
        val currentUser = validateUserIdAndToken(userId, token)
            ?: throw AuthenticationException("UNAUTHORIZED")
        if (body.dateExp == null || body.lastNumber == null)
            throw ParameterException("Bad Request")

        val res = cardBl.createCardWithUser(body, currentUser)
        logger.info("Card created successfully ${res.cardId} for user ${currentUser.userId}")
        return res
    }

    fun deleteLogicalCard(userId: Long, cardId: Long, token: String ): CardEntity {
        val currentUser = validateUserIdAndToken(userId, token)
            ?: throw AuthenticationException("UNAUTHORIZED")
        val res = cardBl.deleteLogical(currentUser, cardId)
        logger.info("Card deleted logical successfully ${res.cardId} for user ${currentUser.userId}")
        return res
    }

}
