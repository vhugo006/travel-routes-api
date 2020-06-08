package br.com.bexs.service

import br.com.bexs.domain.Connection
import br.com.bexs.exception.AlreadyExistingRouteException
import br.com.bexs.repository.ConnectionRepository
import org.springframework.stereotype.Service

@Service
class ConnectionService(private val connectionRepository: ConnectionRepository) {

    fun addConnection(connection: Connection): Connection {
        validateConnection(connection.from, connection.to)
        val formattedConnection = formatConnectionParameters(connection)
        return connectionRepository.save(formattedConnection)
    }

    fun findConnectionsFrom(from: String): List<Connection> {
        return connectionRepository.findByFrom(from.toUpperCase())
    }

    fun isEndConnection(connection: Connection, to: String): Boolean {
        return connection.to == to.toUpperCase()
    }

    private fun validateConnection(from: String, to: String) {
        if (findConnectionsFromTo(from, to).toList().isNotEmpty()) {
            throw AlreadyExistingRouteException("There is already a route departing from $from and arriving in $to")
        }
    }

    fun findConnectionsFromTo(from: String, to: String): List<Connection> {
        return connectionRepository.findByFromAndTo(from.toUpperCase(), to.toUpperCase())
    }

    private fun formatConnectionParameters(connection: Connection): Connection {
        return Connection(
            from = connection.from.toUpperCase(),
            to = connection.to.toUpperCase(),
            cost = connection.cost
        )
    }
}