package com.thoughtworks.archguard.document.config.swagger

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import springfox.documentation.swagger.web.SwaggerResource
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
class ServiceDefinitionsContext private constructor() {
    private val serviceDescriptions: ConcurrentHashMap<String, String> = ConcurrentHashMap()
    fun addServiceDefinition(serviceName: String, serviceDescription: String) {
        serviceDescriptions[serviceName] = serviceDescription
    }

    fun getSwaggerDefinition(serviceId: String): String? {
        return serviceDescriptions[serviceId]
    }

    val swaggerDefinitions: List<SwaggerResource>
        get() = serviceDescriptions.entries.stream().map { serviceDefinition: Map.Entry<String, String> ->
            val resource = SwaggerResource()
            resource.location = "/service/" + serviceDefinition.key
            resource.name = serviceDefinition.key
            resource.swaggerVersion = "2.0"
            resource
        }.collect(Collectors.toList())

}