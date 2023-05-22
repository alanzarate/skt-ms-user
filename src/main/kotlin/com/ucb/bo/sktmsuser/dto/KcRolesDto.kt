package com.ucb.bo.sktmsuser.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class KcRolesDto (
    var id: String,
    var name: String,
    var description: String,
    var composite: Boolean,
    var clientRole: Boolean,
    var containerId: String
){
}