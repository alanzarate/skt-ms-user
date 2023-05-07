package com.ucb.bo.sktmsuser.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class KeycloakUserDto (
    var id: String,
    var createdTimestamp: Long,
    var username: String,
    var enabled: Boolean,
    var totp: Boolean,
    var emailVerified: Boolean,
    var firstName: String,
    var lastName: String,
    var email: String,
    var disableableCredentialTypes: ArrayList<Any>,
    var requiredActions: ArrayList<Any>,
    var federatedIdentities: ArrayList<FederateEntityDto>?,
    var notBefore: Long,
    var access: Map<String, *>
) {
    override fun toString(): String {
        return "KeycloakUserDto(id='$id', createdTimestamp=$createdTimestamp, username='$username', enabled=$enabled, totp=$totp, emailVerified=$emailVerified, firstName='$firstName', lastName='$lastName', email='$email', disableableCredentialTypes=$disableableCredentialTypes, requiredActions=$requiredActions, federatedIdentities=$federatedIdentities, notBefore=$notBefore, access=$access)"
    }
}