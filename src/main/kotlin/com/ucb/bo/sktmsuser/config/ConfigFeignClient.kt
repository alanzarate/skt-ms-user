package com.ucb.bo.sktmsuser.config

import feign.codec.Encoder
import feign.form.FormEncoder
import feign.form.spring.SpringFormEncoder
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.SpringEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Scope

class ConfigFeignClient{

//@Autowired constructor(
//    private val messageConverters: ObjectFactory<HttpMessageConverters>
//) {




    @Bean
    fun encoder(converters: ObjectFactory<HttpMessageConverters>): Encoder{
        return SpringFormEncoder(SpringEncoder(converters))
    }


}