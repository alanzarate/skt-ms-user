package com.ucb.bo.sktmsuser.controller

import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Slf4j
@RequestMapping("/api/test")
class Test {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    @GetMapping("/")
    fun dummy(): Any{
        logger.info("SIUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU@22222222222222")
        return mapOf("main" to 2)
    }
}