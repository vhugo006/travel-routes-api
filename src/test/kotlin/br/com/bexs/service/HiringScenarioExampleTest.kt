package br.com.bexs.service

import br.com.bexs.domain.Connection
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
internal class HiringScenarioExampleTest {

    @Mock
    private lateinit var connectionService: ConnectionService

    @InjectMocks
    private lateinit var travelService: TravelService

    @Test
    fun `when finding the best available travel route from GRU to CDG should return the cheapest one`() {

        val from = "GRU"
        val to = "CDG"

        `when`(connectionService.findConnectionsFrom("GRU")).thenReturn(getConnectionsFromGRU())
        `when`(connectionService.findConnectionsFrom("BRC")).thenReturn(getConnectionsFromBRC())
        `when`(connectionService.findConnectionsFrom("SCL")).thenReturn(getConnectionsFromSCL())
        `when`(connectionService.findConnectionsFrom("ORL")).thenReturn(getConnectionsFromORL())

        val connectionGRUtoCDG = Connection(100, "GRU", "CDG", BigDecimal(75.00))
        `when`(connectionService.isEndConnection(connectionGRUtoCDG, to)).thenReturn(true)

        val connectionORLtoCDG = Connection(100, "ORL", "CDG", BigDecimal(5.00))
        `when`(connectionService.isEndConnection(connectionORLtoCDG, to)).thenReturn(true)

        val bestTravelRoute = travelService.findBestTravelRoute(from, to)
        val travelCost =
            bestTravelRoute
                ?.connections
                ?.map { it.cost }
                ?.fold(BigDecimal.ZERO, BigDecimal::add)

        assertAll(
            { assertNotNull(bestTravelRoute) },
            { assertEquals(4, bestTravelRoute?.connections?.size) },
            { assertEquals("GRU", bestTravelRoute?.connections?.first()?.from) },
            { assertEquals("BRC", bestTravelRoute?.connections?.first()?.to) },
            { assertEquals("BRC", bestTravelRoute?.connections?.get(1)?.from) },
            { assertEquals("SCL", bestTravelRoute?.connections?.get(1)?.to) },
            { assertEquals("SCL", bestTravelRoute?.connections?.get(2)?.from) },
            { assertEquals("ORL", bestTravelRoute?.connections?.get(2)?.to) },
            { assertEquals("ORL", bestTravelRoute?.connections?.get(3)?.from) },
            { assertEquals("CDG", bestTravelRoute?.connections?.get(3)?.to) },
            { assertEquals(BigDecimal(40), travelCost) }
        )
    }

    @Test
    fun `when finding the best available travel route from BRC to CDG should return the cheapest one`() {

        val from = "BRC"
        val to = "CDG"

        `when`(connectionService.findConnectionsFrom("GRU")).thenReturn(getConnectionsFromGRU())
        `when`(connectionService.findConnectionsFrom("BRC")).thenReturn(getConnectionsFromBRC())
        `when`(connectionService.findConnectionsFrom("SCL")).thenReturn(getConnectionsFromSCL())
        `when`(connectionService.findConnectionsFrom("ORL")).thenReturn(getConnectionsFromORL())

        val connectionGRUtoCDG = Connection(100, "GRU", "CDG", BigDecimal(75))
        `when`(connectionService.isEndConnection(connectionGRUtoCDG, to)).thenReturn(true)

        val connectionORLtoCDG = Connection(100, "ORL", "CDG", BigDecimal(5))
        `when`(connectionService.isEndConnection(connectionORLtoCDG, to)).thenReturn(true)
        val bestTravelRoute = travelService.findBestTravelRoute(from, to)

        val travelCost =
            bestTravelRoute
                ?.connections
                ?.map { it.cost }
                ?.fold(BigDecimal.ZERO, BigDecimal::add)

        assertAll(
            { assertNotNull(bestTravelRoute) },
            { assertEquals(3, bestTravelRoute?.connections?.size) },
            { assertEquals("BRC", bestTravelRoute?.connections?.first()?.from) },
            { assertEquals("SCL", bestTravelRoute?.connections?.first()?.to) },
            { assertEquals("SCL", bestTravelRoute?.connections?.get(1)?.from) },
            { assertEquals("ORL", bestTravelRoute?.connections?.get(1)?.to) },
            { assertEquals("ORL", bestTravelRoute?.connections?.get(2)?.from) },
            { assertEquals("CDG", bestTravelRoute?.connections?.get(2)?.to) },
            { assertEquals(BigDecimal(30), travelCost) }
        )
    }

    private fun getConnectionsFromGRU(): List<Connection> {

        return listOf(
            Connection(100, "GRU", "BRC", BigDecimal(10.00)),
            Connection(100, "GRU", "CDG", BigDecimal(75.00)),
            Connection(100, "GRU", "SCL", BigDecimal(20.00)),
            Connection(100, "GRU", "ORL", BigDecimal(56.00))
        )
    }

    private fun getConnectionsFromBRC() = listOf(Connection(100, "BRC", "SCL", BigDecimal(5.00)))

    private fun getConnectionsFromSCL() = listOf(Connection(100, "SCL", "ORL", BigDecimal(20.00)))

    private fun getConnectionsFromORL() = listOf(Connection(100, "ORL", "CDG", BigDecimal(5.00)))
}
