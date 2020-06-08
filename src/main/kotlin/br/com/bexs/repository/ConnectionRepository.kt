package br.com.bexs.repository

import br.com.bexs.domain.Connection
import org.springframework.data.jpa.repository.JpaRepository

interface ConnectionRepository : JpaRepository<Connection, Long> {

    fun findByFrom(from: String): List<Connection>
    fun findByFromAndTo(from: String, to: String): List<Connection>
}