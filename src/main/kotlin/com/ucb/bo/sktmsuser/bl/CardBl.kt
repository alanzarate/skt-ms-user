package com.ucb.bo.sktmsuser.bl

import com.ucb.bo.sktmsuser.dao.CardDao
import com.ucb.bo.sktmsuser.dto.CardDto
import com.ucb.bo.sktmsuser.entity.CardEntity
import com.ucb.bo.sktmsuser.entity.UserEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CardBl @Autowired constructor(
    private val cardDao: CardDao
    ){
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)


    fun getCardsByUser(currentUser: UserEntity): ArrayList<CardEntity> {
        return ArrayList(cardDao.findByUserEntity(currentUser) )
    }
    fun createCardWithUser(body: CardDto, user: UserEntity): CardEntity {
        val cardEntity = CardEntity(
            dateExp = body.dateExp!!,
            lastNumber = body.lastNumber!!,
            available = true,
            userEntity = user
        )
        return cardDao.save(cardEntity)
    }

    fun deleteLogical(currentUser: UserEntity, cardId: Long): CardEntity{
        val res = cardDao.findByCardIdAndUserEntity(cardId, currentUser)
        if( res == null) {
            logger.info("El usuario ${currentUser.userId} trato de acceder a la tarjeta $cardId y no encontro nada")
            throw Exception("NOT FOUNDED")
        }

        res.available = false
        return cardDao.save(res)

    }
}