package br.com.bexs.service

import br.com.bexs.domain.Route
import br.com.bexs.exception.NoResourceFoundException
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
internal class TravelRouteServiceTest {

    @Mock
    private lateinit var routeService: RouteService

    @InjectMocks
    private lateinit var travelRouteService: TravelRouteService

    @Test
    fun `given valid parameters when finding available travel route then return one travel route`() {

        val from = "BEL"
        val to = "POA"

        val route = Route(1105, "BEL", "POA", BigDecimal(168.98))

        val routesFromBEL = mutableListOf(route)

        `when`(routeService.findRoutesFrom("BEL")).thenReturn(routesFromBEL)
        `when`(routeService.isEndRoute(route, to)).thenReturn(true)

        val travelRoutes = travelRouteService.findAvailableTravelRoutes(from, to)

        assertAll(
            { assertEquals("BEL", travelRoutes.first().routes.first().from) },
            { assertEquals("POA", travelRoutes.first().routes.first().to) },
            { assertEquals(BigDecimal(168.98), travelRoutes.first().routes.first().cost) }
        )
    }

    @Test
    fun `given BEL to POA parameters when finding available travel routes then return two travel routes`() {

        val from = "BEL"
        val to = "POA"

        val routeBELtoPOA = Route(1105, "BEL", "POA", BigDecimal(168.98))
        val routeBSBtoPOA = Route(1105, "BSB", "POA", BigDecimal(168.98))

        val routesFromBEL = mutableListOf(
            Route(1002, "BEL", "BSB", BigDecimal(100.00)),
            routeBELtoPOA
        )

        val routesWithOriginInBSB = mutableListOf(routeBSBtoPOA)

        `when`(routeService.findRoutesFrom("BEL")).thenReturn(routesFromBEL)
        `when`(routeService.findRoutesFrom("BSB")).thenReturn(routesWithOriginInBSB)

        `when`(routeService.isEndRoute(routeBELtoPOA, to)).thenReturn(true)
        `when`(routeService.isEndRoute(routeBSBtoPOA, to)).thenReturn(true)

        val travelRoutes = travelRouteService.findAvailableTravelRoutes(from, to)

        assertEquals(2, travelRoutes.size)
    }

    @Test
    fun `given valid parameters when finding the best available travel route then return the cheapest one`() {

        val from = "BEL"
        val to = "POA"

        val routeBELtoPOA = Route(1105, "BEL", "POA", BigDecimal(750.00))
        val routeBSBtoPOA = Route(1106, "BSB", "POA", BigDecimal(168.98))

        val routesFromBEL =
            mutableListOf(Route(1002, "BEL", "BSB", BigDecimal(100.00)), routeBELtoPOA)

        val routesWithOriginInBSB = mutableListOf(routeBSBtoPOA)

        `when`(routeService.findRoutesFrom("BEL")).thenReturn(routesFromBEL)
        `when`(routeService.findRoutesFrom("BSB")).thenReturn(routesWithOriginInBSB)

        `when`(routeService.isEndRoute(routeBELtoPOA, to)).thenReturn(true)
        `when`(routeService.isEndRoute(routeBSBtoPOA, to)).thenReturn(true)

        val bestTravelRoute = travelRouteService.findBestTravelRoute(from, to)

        val travelCost =
            bestTravelRoute
                ?.routes
                ?.map { it.cost }
                ?.fold(BigDecimal.ZERO, BigDecimal::add)

        assertAll(
            { assertNotNull(bestTravelRoute) },
            { assertEquals(2, bestTravelRoute?.routes?.size) },
            { assertEquals("BEL", bestTravelRoute?.routes?.first()?.from) },
            { assertEquals("BSB", bestTravelRoute?.routes?.first()?.to) },
            { assertEquals("BSB", bestTravelRoute?.routes?.get(1)?.from) },
            { assertEquals("POA", bestTravelRoute?.routes?.get(1)?.to) },
            {
                assertEquals(
                    BigDecimal(268.98).round(MathContext(5, RoundingMode.HALF_UP)),
                    travelCost?.round(MathContext(5, RoundingMode.HALF_UP))
                )
            }
        )
    }

    @Test
    fun `given lowercase from-to parameters when finding the best available travel route then return a travel route`(){

        val from = "bel"
        val to = "poa"

        val route = Route(1105, "BEL", "POA", BigDecimal(168.98))

        val routesFromBEL = mutableListOf(route)

        `when`(routeService.findRoutesFrom("bel")).thenReturn(routesFromBEL)
        `when`(routeService.isEndRoute(route, to)).thenReturn(true)

        val travelRoutes = travelRouteService.findAvailableTravelRoutes(from, to)

        assertAll(
            { assertEquals("BEL", travelRoutes.first().routes.first().from) },
            { assertEquals("POA", travelRoutes.first().routes.first().to) },
            { assertEquals(BigDecimal(168.98), travelRoutes.first().routes.first().cost) }
        )
    }

    @Test
    fun `given uppercase from-to parameters when finding the best available travel route then return a travel route`(){

        val from = "BEL"
        val to = "POA"

        val route = Route(1105, "BEL", "POA", BigDecimal(168.98))

        val routesFromBEL = mutableListOf(route)

        `when`(routeService.findRoutesFrom("BEL")).thenReturn(routesFromBEL)
        `when`(routeService.isEndRoute(route, to)).thenReturn(true)

        val travelRoutes = travelRouteService.findAvailableTravelRoutes(from, to)

        assertAll(
            { assertEquals("BEL", travelRoutes.first().routes.first().from) },
            { assertEquals("POA", travelRoutes.first().routes.first().to) },
            { assertEquals(BigDecimal(168.98), travelRoutes.first().routes.first().cost) }
        )
    }

    @Test
    fun `given mixed lowercase and uppercase from-to parameters when finding the best available travel route then return a travel route`(){

        val from = "bEL"
        val to = "pOa"

        val route = Route(1105, "BEL", "POA", BigDecimal(168.98))

        val routesFromBEL = mutableListOf(route)

        `when`(routeService.findRoutesFrom("bEL")).thenReturn(routesFromBEL)
        `when`(routeService.isEndRoute(route, to)).thenReturn(true)

        val travelRoutes = travelRouteService.findAvailableTravelRoutes(from, to)

        assertAll(
            { assertEquals("BEL", travelRoutes.first().routes.first().from) },
            { assertEquals("POA", travelRoutes.first().routes.first().to) },
            { assertEquals(BigDecimal(168.98), travelRoutes.first().routes.first().cost) }
        )
    }

    @Test
    fun `given there is no routes for parameters when finding the best available travel route then throw NoTravelRouteFoundException`() {
        val from = "BEL"
        val to = "GRU"

        val routeBELtoBSB = Route(1002, "BEL", "BSB", BigDecimal(100.00))
        val routesFromBEL = mutableListOf(routeBELtoBSB)

        `when`(routeService.findRoutesFrom("BEL")).thenReturn(routesFromBEL)

        `when`(routeService.isEndRoute(routeBELtoBSB, to)).thenReturn(false)

        assertThrows<NoResourceFoundException> { travelRouteService.findBestTravelRoute(from, to) }
    }

    @Test
    fun `given there is no routes coming from parameters when finding the best available travel route then throw NoTravelRouteFoundException`() {
        val from = "BEL"
        val to = "GRU"

        `when`(routeService.findRoutesFrom("BEL")).thenReturn(emptyList())

        assertThrows<NoResourceFoundException> { travelRouteService.findBestTravelRoute(from, to) }
    }
}
