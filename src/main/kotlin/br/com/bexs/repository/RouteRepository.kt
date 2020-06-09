package br.com.bexs.repository

import br.com.bexs.domain.Route
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface RouteRepository : JpaRepository<Route, Long>, PagingAndSortingRepository<Route, Long> {

    fun findByFrom(from: String): List<Route>
    fun findByFromAndTo(from: String, to: String): List<Route>
    override fun findAll(pageReq: Pageable): Page<Route>
}