package br.com.bexs.api.dto

import java.math.BigDecimal
import javax.persistence.Column

data class RouteDTO(val from: String, val to: String, val cost: BigDecimal, val url: String)