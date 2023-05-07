package com.ucb.bo.sktmsuser.dao

import com.ucb.bo.sktmsuser.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.*

@Repository
interface UserDao:CrudRepository<UserEntity, Long> {
    fun findByUserUuid(userUuid: String): List<UserEntity>
    fun findByUserId(userId: Long): UserEntity?
}