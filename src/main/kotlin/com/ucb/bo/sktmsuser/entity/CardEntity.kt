package com.ucb.bo.sktmsuser.entity

import net.minidev.json.annotate.JsonIgnore
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.Date
import javax.persistence.*

@Entity
@Table(name = "Card")
class CardEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "card_id")
    var cardId: Long = 0,
    var dateExp: Date,
    var lastNumber: String,
    var available: Boolean,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    var userEntity: UserEntity? = null
){

}