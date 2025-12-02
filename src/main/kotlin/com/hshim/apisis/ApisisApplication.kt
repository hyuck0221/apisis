package com.hshim.apisis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.TimeZone

@SpringBootApplication
class ApisisApplication

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
    runApplication<ApisisApplication>(*args)
}
