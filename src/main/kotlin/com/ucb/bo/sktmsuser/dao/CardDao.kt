package com.ucb.bo.sktmsuser.dao



import com.ucb.bo.sktmsuser.entity.CardEntity
import com.ucb.bo.sktmsuser.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CardDao: CrudRepository<CardEntity, Long> {
    fun findByUserEntity(userEntity: UserEntity): List<CardEntity>

    fun findByCardIdAndUserEntity(cardId: Long, userEntity: UserEntity): CardEntity?
}