package br.com.bexs.service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "travel-route.service", ignoreUnknownFields = false)
@Component
class ServiceProperties(var name: String = "Empty")