package com.ucb.bo.sktmsuser.bl

import com.ucb.bo.sktmsuser.dao.AddressDao
import com.ucb.bo.sktmsuser.dao.UserDao
import com.ucb.bo.sktmsuser.dto.AddressResponseDto
import com.ucb.bo.sktmsuser.dto.ResponseDto
import com.ucb.bo.sktmsuser.entity.AddressEntity
import com.ucb.bo.sktmsuser.entity.UserEntity
import com.ucb.bo.sktmsuser.model.CustomException
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
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
    private val userDao: UserDao,
    private val addressDao: AddressDao,

){


    fun getUserInformation(token: String): ResponseDto<Any> {
        try {
            val userIdToken = keycloakBl.getKeycloakIdFromToken(token);
            val userInDb = userDao.findByUserUuid(userIdToken);
            // if there is data in list, means that main db contains the user
            if (userInDb.isNotEmpty())
                return ResponseDto(userInDb[0], null, true)

            // the user is not in main db
            // check for user in keycloak db
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
        val userIdToken = keycloakBl.getKeycloakIdFromToken(token);
        val userInDb = userDao.findByUserUuid(userIdToken);
        if (userInDb.isEmpty()) return null
        val currentUser = userInDb[0]
        if(userId != currentUser.userId) return null
        return currentUser
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
}
