package com.ucb.bo.sktmsuser.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class FederateEntityDto(
    var identityProvider: String,
    var userId: String,
    var userName: String,
) {
}