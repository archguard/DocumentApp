package com.thoughtworks.archguard.document

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.scheduling.annotation.EnableScheduling
import springfox.documentation.oas.annotations.EnableOpenApi

@EnableEurekaClient
@SpringBootApplication
@EnableOpenApi
@EnableScheduling
@EnableDiscoveryClient
class DocumentApplication

fun main(args: Array<String>) {
	runApplication<DocumentApplication>(*args)
}
