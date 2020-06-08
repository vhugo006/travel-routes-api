package br.com.bexs.repository

import br.com.bexs.domain.Route
import org.springframework.data.jpa.repository.JpaRepository

interface RouteRepository : JpaRepository<Route, Long> {

    fun findByFrom(from: String): List<Route>
    fun findByFromAndTo(from: String, to: String): List<Route>
}