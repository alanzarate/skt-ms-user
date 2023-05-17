package com.ucb.bo.sktmsuser

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
class SktMsUserApplication

fun main(args: Array<String>) {
	runApplication<SktMsUserApplication>(*args)
}
