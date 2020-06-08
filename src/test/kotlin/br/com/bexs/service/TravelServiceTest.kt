package br.com.bexs.service

import br.com.bexs.domain.Connection
import br.com.bexs.exception.NoTravelRouteFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
internal class TravelServiceTest {

    @Mock
    private lateinit var connectionService: ConnectionService

    @InjectMocks
    private lateinit var travelService: TravelService

    @Test
    fun `when finding available travel route should return one with one connection`() {

        val from = "BEL"
        val to = "POA"

        val connection = Connection(1105, "BEL", "POA", BigDecimal(168.98))

        val connectionsFromBEL = mutableListOf(connection)

        `when`(connectionService.findConnectionsFrom("BEL")).thenReturn(connectionsFromBEL)
        `when`(connectionService.isEndConnection(connection, to)).thenReturn(true)

        val travelRoutes = travelService.findAvailableTravelRoutes(from, to)

        assertAll(
            { assertEquals("BEL", travelRoutes.first().connections.first().from) },
            { assertEquals("POA", travelRoutes.first().connections.first().to) },
            { assertEquals(BigDecimal(168.98), travelRoutes.first().connections.first().cost) }
        )
    }

    @Test
    fun `when finding available travel routes from BEL to POA should return two travel routes`() {

        val from = "BEL"
        val to = "POA"

        val connectionBELtoPOA = Connection(1105, "BEL", "POA", BigDecimal(168.98))
        val connectionBSBtoPOA = Connection(1105, "BSB", "POA", BigDecimal(168.98))

        val connectionsFromBEL = mutableListOf(
            Connection(1002, "BEL", "BSB", BigDecimal(100.00)),
            connectionBELtoPOA
        )

        val connectionsWithOriginInBSB = mutableListOf(connectionBSBtoPOA)

        `when`(connectionService.findConnectionsFrom("BEL")).thenReturn(connectionsFromBEL)
        `when`(connectionService.findConnectionsFrom("BSB")).thenReturn(connectionsWithOriginInBSB)

        `when`(connectionService.isEndConnection(connectionBELtoPOA, to)).thenReturn(true)
        `when`(connectionService.isEndConnection(connectionBSBtoPOA, to)).thenReturn(true)

        val travelRoutes = travelService.findAvailableTravelRoutes(from, to)

        assertEquals(2, travelRoutes.size)
    }

    @Test
    fun `when finding the best available travel route should return the cheapest one`() {
        val from = "BEL"
        val to = "POA"

        val connectionBELtoPOA = Connection(1105, "BEL", "POA", BigDecimal(750.00))
        val connectionBSBtoPOA = Connection(1106, "BSB", "POA", BigDecimal(168.98))

        val connectionsFromBEL =
            mutableListOf(Connection(1002, "BEL", "BSB", BigDecimal(100.00)), connectionBELtoPOA)

        val connectionsWithOriginInBSB = mutableListOf(connectionBSBtoPOA)

        `when`(connectionService.findConnectionsFrom("BEL")).thenReturn(connectionsFromBEL)
        `when`(connectionService.findConnectionsFrom("BSB")).thenReturn(connectionsWithOriginInBSB)

        `when`(connectionService.isEndConnection(connectionBELtoPOA, to)).thenReturn(true)
        `when`(connectionService.isEndConnection(connectionBSBtoPOA, to)).thenReturn(true)

        val bestTravelRoute = travelService.findBestTravelRoute(from, to)

        val travelCost =
            bestTravelRoute
                ?.connections
                ?.map { it.cost }
                ?.fold(BigDecimal.ZERO, BigDecimal::add)

        assertAll(
            { assertNotNull(bestTravelRoute) },
            { assertEquals(2, bestTravelRoute?.connections?.size) },
            { assertEquals("BEL", bestTravelRoute?.connections?.first()?.from) },
            { assertEquals("BSB", bestTravelRoute?.connections?.first()?.to) },
            { assertEquals("BSB", bestTravelRoute?.connections?.get(1)?.from) },
            { assertEquals("POA", bestTravelRoute?.connections?.get(1)?.to) },
            {
                assertEquals(
                    BigDecimal(268.98).round(MathContext(5, RoundingMode.HALF_UP)),
                    travelCost?.round(MathContext(5, RoundingMode.HALF_UP))
                )
            }
        )
    }

    @Test
    fun `when there is no travel route available should throw a NoTravelRouteFoundException`(){

    }

    @Test
    fun `when from-to parameters is lowercase should return the existing travel route`(){
        val from = "bel"
        val to = "poa"

        val connection = Connection(1105, "BEL", "POA", BigDecimal(168.98))

        val connectionsFromBEL = mutableListOf(connection)

        `when`(connectionService.findConnectionsFrom("bel")).thenReturn(connectionsFromBEL)
        `when`(connectionService.isEndConnection(connection, to)).thenReturn(true)

        val travelRoutes = travelService.findAvailableTravelRoutes(from, to)

        assertAll(
            { assertEquals("BEL", travelRoutes.first().connections.first().from) },
            { assertEquals("POA", travelRoutes.first().connections.first().to) },
            { assertEquals(BigDecimal(168.98), travelRoutes.first().connections.first().cost) }
        )
    }

    @Test
    fun `when from-to parameters is uppercase should return the existing travel route`(){
        val from = "BEL"
        val to = "POA"

        val connection = Connection(1105, "BEL", "POA", BigDecimal(168.98))

        val connectionsFromBEL = mutableListOf(connection)

        `when`(connectionService.findConnectionsFrom("BEL")).thenReturn(connectionsFromBEL)
        `when`(connectionService.isEndConnection(connection, to)).thenReturn(true)

        val travelRoutes = travelService.findAvailableTravelRoutes(from, to)

        assertAll(
            { assertEquals("BEL", travelRoutes.first().connections.first().from) },
            { assertEquals("POA", travelRoutes.first().connections.first().to) },
            { assertEquals(BigDecimal(168.98), travelRoutes.first().connections.first().cost) }
        )
    }

    @Test
    fun `when from-to parameters is lowercase and uppercase mixed should return return the existing travel route`(){
        val from = "bEL"
        val to = "pOa"

        val connection = Connection(1105, "BEL", "POA", BigDecimal(168.98))

        val connectionsFromBEL = mutableListOf(connection)

        `when`(connectionService.findConnectionsFrom("bEL")).thenReturn(connectionsFromBEL)
        `when`(connectionService.isEndConnection(connection, to)).thenReturn(true)

        val travelRoutes = travelService.findAvailableTravelRoutes(from, to)

        assertAll(
            { assertEquals("BEL", travelRoutes.first().connections.first().from) },
            { assertEquals("POA", travelRoutes.first().connections.first().to) },
            { assertEquals(BigDecimal(168.98), travelRoutes.first().connections.first().cost) }
        )
    }

    @Test
    fun `when available travel route is not found should throw NoTravelRouteFoundException`() {
        val from = "BEL"
        val to = "GRU"

        val connectionBELtoBSB = Connection(1002, "BEL", "BSB", BigDecimal(100.00))
        val connectionsFromBEL = mutableListOf(connectionBELtoBSB)

        `when`(connectionService.findConnectionsFrom("BEL")).thenReturn(connectionsFromBEL)

        `when`(connectionService.isEndConnection(connectionBELtoBSB, to)).thenReturn(false)

        assertThrows<NoTravelRouteFoundException> { travelService.findBestTravelRoute(from, to) }
    }

    @Test
    fun `when there is no connections from the given 'from' parameter NoTravelRouteFoundException`() {
        val from = "BEL"
        val to = "GRU"

        `when`(connectionService.findConnectionsFrom("BEL")).thenReturn(emptyList())

        assertThrows<NoTravelRouteFoundException> { travelService.findBestTravelRoute(from, to) }
    }

    @Test
    fun `when there is no connections to from-to parameters should throw NoTravelRouteFoundException`() {}
}
