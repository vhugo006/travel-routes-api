package br.com.bexs.service

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class TravelServiceHealth(val serviceProperties: ServiceProperties) : HealthIndicator {
    override fun health(): Health {
        return Health
            .up()
            .withDetail(
                "details",
                "{ 'internals' : 'working well', 'profile' : '" + this.serviceProperties.name + "' }"
            )
            .status("OK")
            .build();
    }
}