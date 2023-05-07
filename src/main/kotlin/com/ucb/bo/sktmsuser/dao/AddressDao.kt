package com.ucb.bo.sktmsuser.dao

import com.ucb.bo.sktmsuser.entity.AddressEntity
import com.ucb.bo.sktmsuser.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressDao:CrudRepository<AddressEntity, Long> {
    fun findByUserEntity(userEntity: UserEntity): List<AddressEntity>
}