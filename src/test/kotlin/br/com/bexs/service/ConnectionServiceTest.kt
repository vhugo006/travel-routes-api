package br.com.bexs.service

import br.com.bexs.domain.Connection
import br.com.bexs.exception.AlreadyExistingRouteException
import br.com.bexs.repository.ConnectionRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
internal class ConnectionServiceTest {

    @Mock
    private lateinit var connectionRepository: ConnectionRepository

    @InjectMocks
    private lateinit var connectionService: ConnectionService

    @Test
    fun `when adding a connection should return a non nullable connection`() {

        val connection = Connection(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(connectionRepository.save(connection))
            .thenReturn(Connection(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        assertNotNull(connectionService.addConnection(connection))
    }

    @Test
    fun `when adding a connection with lower case 'from' parameter should return a connection`() {

        val formattedConnection = Connection(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(connectionRepository.save(formattedConnection))
            .thenReturn(Connection(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val connectionParameter = Connection(from = "bel", to = "GRU", cost = BigDecimal(750.00))
        assertNotNull(connectionService.addConnection(connectionParameter))
    }

    @Test
    fun `when adding a connection with lower case 'to' parameter should return a connection`() {

        val formattedConnection = Connection(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(connectionRepository.save(formattedConnection))
            .thenReturn(Connection(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val connectionParameter = Connection(from = "BEL", to = "gru", cost = BigDecimal(750.00))
        assertNotNull(connectionService.addConnection(connectionParameter))
    }

    @Test
    fun `when adding a connection with lower case 'from-to' parameters should return a connection`() {

        val formattedConnection = Connection(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(connectionRepository.save(formattedConnection))
            .thenReturn(Connection(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val connectionParameter = Connection(from = "bel", to = "gru", cost = BigDecimal(750.00))
        assertNotNull(connectionService.addConnection(connectionParameter))
    }

    @Test
    fun `when adding a connection mixed lower-upper case 'from-to' parameters should return a connection`() {

        val formattedConnection = Connection(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(connectionRepository.save(formattedConnection))
            .thenReturn(Connection(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val connectionParameter = Connection(from = "bEl", to = "gRU", cost = BigDecimal(750.00))
        assertNotNull(connectionService.addConnection(connectionParameter))
    }

    @Test
    fun `when there is routes for the given origin parameter should return a non empty collection`() {

        val connectionsWithOriginInBEL = mutableListOf(
            Connection(1105, "BEL", "POA", BigDecimal(168.98)),
            Connection(1002, "BEL", "BSB", BigDecimal(100.00)),
            Connection(1105, "BSB", "POA", BigDecimal(168.98))
        )

        Mockito.`when`(connectionRepository.findByFrom("BEL")).thenReturn(connectionsWithOriginInBEL)
        val connections = connectionService.findConnectionsFrom("BEL")
        assertTrue { connections.isNotEmpty() }
    }

    @Test
    fun `when finding connections by origin should return the same origin in collection items`() {

        val connectionsWithOriginInBEL = mutableListOf(
            Connection(1105, "BEL", "POA", BigDecimal(168.98)),
            Connection(1002, "BEL", "BSB", BigDecimal(100.00))
        )

        `when`(connectionRepository.findByFrom("BEL")).thenReturn(connectionsWithOriginInBEL)
        val connections = connectionService.findConnectionsFrom("BEL")
        connections.forEach { connection -> assertEquals("BEL", connection.from) }
    }

    @Test
    fun `when adding a route and it already exists should throws AlreadyExistingRouteException`() {

        val connection = Connection(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))

        `when`(connectionRepository.findByFromAndTo("BEL", "POA")).thenReturn(mutableListOf(connection))

        assertThrows<AlreadyExistingRouteException> {
            connectionService.addConnection(Connection(from = "BEL", to = "POA", cost = BigDecimal(10.00)))
        }
    }

    @Test
    fun `when 'to' parameter is lowercase should return true`() {

        val connection = Connection(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))
        val to = "poa"

        assertTrue{ connectionService.isEndConnection(connection, to)}
    }

    @Test
    fun `when 'to' parameter is uppercase should return true`() {

        val connection = Connection(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))
        val to = "POA"

        assertTrue{ connectionService.isEndConnection(connection, to)}
    }

    @Test
    fun `when 'to' parameter is uppercase and lowercase mixed should return true`() {

        val connection = Connection(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))
        val to = "PoA"

        assertTrue{ connectionService.isEndConnection(connection, to)}
    }
}