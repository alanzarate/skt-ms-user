package com.ucb.bo.sktmsuser.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.bouncycastle.asn1.its.Longitude

@JsonIgnoreProperties(ignoreUnknown = true)
data class AddressDto (
    var address: String,
    var latitude: Double,
    var longitude: Double,
){
}