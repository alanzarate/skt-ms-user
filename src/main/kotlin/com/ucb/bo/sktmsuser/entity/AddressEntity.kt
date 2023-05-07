package com.ucb.bo.sktmsuser.entity

import net.minidev.json.annotate.JsonIgnore
import org.bouncycastle.asn1.its.Longitude
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Table(name = "SKT_ADDRESS")
class AddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "address_id")
    var addressId: Long = 0,
    var address: String?,
    var available: Boolean,
    var latitude: Double,
    var longitude: Double,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    var userEntity: UserEntity? = null
) {


}