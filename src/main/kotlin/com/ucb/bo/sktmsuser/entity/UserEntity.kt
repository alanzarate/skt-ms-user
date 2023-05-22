package com.ucb.bo.sktmsuser.entity

import javax.persistence.*

@Entity
@Table(name = "SKT_USER")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="user_id")
    var userId: Long = 0,
    var name: String,
    var available: Boolean,
    var lastname: String,
    @Column(name="email_address")
    var emailAddress: String,
    @Column(name="identity_provider")
    var identityProvider: String?,
    @Column(name="user_uuid")
    var userUuid: String,
    var cel: String? = null,

)
{
    override fun toString(): String {
        return "UserEntity(user_id=$userId, name='$name', available=$available, lastname='$lastname', email_address='$emailAddress', identity_provider='$identityProvider', user_uuid='$userUuid')"
    }
}