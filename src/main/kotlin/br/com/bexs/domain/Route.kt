package br.com.bexs.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Route(
    @Id @GeneratedValue
    @JsonIgnore
    val id: Long? = null,
    @Column(name = "_from")
    val from: String,
    val to: String,
    @Column
    val cost: BigDecimal
)

data class TravelRoute(val routes: List<Route> = emptyList(), var totalCost: BigDecimal? = null)