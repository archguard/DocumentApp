package com.thoughtworks.archguard.document.config.swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Primary
import org.springframework.web.client.RestTemplate
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider
import springfox.documentation.swagger.web.SwaggerResource
import springfox.documentation.swagger.web.SwaggerResourcesProvider
import java.util.*

@Configuration
class SwaggerUIConfiguration(private val definitionContext: ServiceDefinitionsContext) {

    @Bean
    fun configureTemplate(): RestTemplate? {
        return RestTemplate()
    }

    @Primary
    @Bean
    @Lazy
    fun swaggerResourcesProvider(defaultResourcesProvider: InMemorySwaggerResourcesProvider, temp: RestTemplate?): SwaggerResourcesProvider? {
        return SwaggerResourcesProvider {
            val resources: MutableList<SwaggerResource> = ArrayList(defaultResourcesProvider.get())
            resources.clear()
            resources.addAll(definitionContext.swaggerDefinitions)
            resources
        }
    }
}