package com.thoughtworks.archguard.document.config.swagger

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.util.*

@Component
class ServiceDescriptionUpdater(private val definitionContext: ServiceDefinitionsContext,
                                private val discoveryClient: DiscoveryClient) {
    private val template: RestTemplate = RestTemplate()

    private val logger = LoggerFactory.getLogger(ServiceDescriptionUpdater::class.java)

    @Scheduled(fixedDelayString = "\${swagger.config.refreshrate}")
    fun refreshSwaggerConfigurations() {
        logger.debug("Starting Service Definition Context refresh")
        discoveryClient.services.stream().forEach { serviceId: String ->
            logger.debug("Attempting service definition refresh for Service : {} ", serviceId)
            val serviceInstances = discoveryClient.getInstances(serviceId)
            if (serviceInstances == null || serviceInstances.isEmpty()) { //Should not be the case kept for failsafe
                logger.info(serviceInstances.toString())
                logger.info("No instances available for service : {} ", serviceId)
            } else {
                val instance = serviceInstances[0]
                val swaggerURL = getSwaggerURL(instance)
                val jsonData = getSwaggerDefinitionForAPI(serviceId, swaggerURL)
                if (jsonData.isPresent) {
                    val content = getJSON(serviceId, jsonData.get())
                    definitionContext.addServiceDefinition(serviceId, content)
                } else {
                    logger.error("Skipping service id : {} Error : Could not get Swagegr definition from API ", serviceId)
                }
                logger.info("Service Definition Context Refreshed at :  {}", LocalDate.now())
            }
        }
    }

    private fun getSwaggerURL(instance: ServiceInstance): String {
        val swaggerURL = instance.metadata[KEY_SWAGGER_URL]
        return if (swaggerURL != null) instance.uri.toString() + swaggerURL else instance.uri.toString() + DEFAULT_SWAGGER_URL
    }

    private fun getSwaggerDefinitionForAPI(serviceName: String, url: String): Optional<Any> {
        logger.debug("Accessing the SwaggerDefinition JSON for Service : {} : URL : {} ", serviceName, url)
        return try {
            val jsonData = template.getForObject(url, Any::class.java)!!
            Optional.of(jsonData)
        } catch (ex: RestClientException) {
            logger.error("Error while getting service definition for service : {} Error : {} ", serviceName, ex.message)
            Optional.empty()
        }
    }

    fun getJSON(serviceId: String?, jsonData: Any?): String {
        return try {
            ObjectMapper().writeValueAsString(jsonData)
        } catch (e: JsonProcessingException) {
            logger.error("Error : {} ", e.message)
            ""
        }
    }

    companion object {
        private const val DEFAULT_SWAGGER_URL = "/v3/api-docs"
        private const val KEY_SWAGGER_URL = "swagger_url"
    }

}