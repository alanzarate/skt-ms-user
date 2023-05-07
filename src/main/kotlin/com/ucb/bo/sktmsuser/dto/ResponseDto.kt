package com.ucb.bo.sktmsuser.dto

class ResponseDto<T>(
    val data: T?,
    val message: String?,
    val success: Boolean
){
}