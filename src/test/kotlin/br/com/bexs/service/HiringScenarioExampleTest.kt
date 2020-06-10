package br.com.bexs.service

import br.com.bexs.domain.Route
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
    private lateinit var routeService: RouteService

    @InjectMocks
    private lateinit var travelRouteService: TravelRouteService

    @Test
    fun `given GRU to CDG parameters when finding the best available travel route then return the cheapest one`() {

        val from = "GRU"
        val to = "CDG"

        `when`(routeService.findRoutesFrom("GRU")).thenReturn(getRoutesFromGRU())
        `when`(routeService.findRoutesFrom("BRC")).thenReturn(getRoutesFromBRC())
        `when`(routeService.findRoutesFrom("SCL")).thenReturn(getRoutesFromSCL())
        `when`(routeService.findRoutesFrom("ORL")).thenReturn(getRoutesFromORL())

        val routeGRUtoCDG = Route(100, "GRU", "CDG", BigDecimal(75.00))
        `when`(routeService.isEndRoute(routeGRUtoCDG, to)).thenReturn(true)

        val routeORLtoCDG = Route(100, "ORL", "CDG", BigDecimal(5.00))
        `when`(routeService.isEndRoute(routeORLtoCDG, to)).thenReturn(true)

        val bestTravelRoute = travelRouteService.findBestTravelRoute(from, to)
        val travelCost =
            bestTravelRoute
                ?.routes
                ?.map { it.cost }
                ?.fold(BigDecimal.ZERO, BigDecimal::add)

        assertAll(
            { assertNotNull(bestTravelRoute) },
            { assertEquals(4, bestTravelRoute?.routes?.size) },
            { assertEquals("GRU", bestTravelRoute?.routes?.first()?.from) },
            { assertEquals("BRC", bestTravelRoute?.routes?.first()?.to) },
            { assertEquals("BRC", bestTravelRoute?.routes?.get(1)?.from) },
            { assertEquals("SCL", bestTravelRoute?.routes?.get(1)?.to) },
            { assertEquals("SCL", bestTravelRoute?.routes?.get(2)?.from) },
            { assertEquals("ORL", bestTravelRoute?.routes?.get(2)?.to) },
            { assertEquals("ORL", bestTravelRoute?.routes?.get(3)?.from) },
            { assertEquals("CDG", bestTravelRoute?.routes?.get(3)?.to) },
            { assertEquals(BigDecimal(40), travelCost) }
        )
    }

    @Test
    fun `given BRC to CDG parameters when finding the best available travel route then return the cheapest one`() {

        val from = "BRC"
        val to = "CDG"

        `when`(routeService.findRoutesFrom("GRU")).thenReturn(getRoutesFromGRU())
        `when`(routeService.findRoutesFrom("BRC")).thenReturn(getRoutesFromBRC())
        `when`(routeService.findRoutesFrom("SCL")).thenReturn(getRoutesFromSCL())
        `when`(routeService.findRoutesFrom("ORL")).thenReturn(getRoutesFromORL())

        val routeGRUtoCDG = Route(100, "GRU", "CDG", BigDecimal(75))
        `when`(routeService.isEndRoute(routeGRUtoCDG, to)).thenReturn(true)

        val routeORLtoCDG = Route(100, "ORL", "CDG", BigDecimal(5))
        `when`(routeService.isEndRoute(routeORLtoCDG, to)).thenReturn(true)
        val bestTravelRoute = travelRouteService.findBestTravelRoute(from, to)

        val travelCost =
            bestTravelRoute
                ?.routes
                ?.map { it.cost }
                ?.fold(BigDecimal.ZERO, BigDecimal::add)

        assertAll(
            { assertNotNull(bestTravelRoute) },
            { assertEquals(3, bestTravelRoute?.routes?.size) },
            { assertEquals("BRC", bestTravelRoute?.routes?.first()?.from) },
            { assertEquals("SCL", bestTravelRoute?.routes?.first()?.to) },
            { assertEquals("SCL", bestTravelRoute?.routes?.get(1)?.from) },
            { assertEquals("ORL", bestTravelRoute?.routes?.get(1)?.to) },
            { assertEquals("ORL", bestTravelRoute?.routes?.get(2)?.from) },
            { assertEquals("CDG", bestTravelRoute?.routes?.get(2)?.to) },
            { assertEquals(BigDecimal(30), travelCost) }
        )
    }

    private fun getRoutesFromGRU(): List<Route> {

        return listOf(
            Route(100, "GRU", "BRC", BigDecimal(10.00)),
            Route(100, "GRU", "CDG", BigDecimal(75.00)),
            Route(100, "GRU", "SCL", BigDecimal(20.00)),
            Route(100, "GRU", "ORL", BigDecimal(56.00))
        )
    }

    private fun getRoutesFromBRC() = listOf(Route(100, "BRC", "SCL", BigDecimal(5.00)))

    private fun getRoutesFromSCL() = listOf(Route(100, "SCL", "ORL", BigDecimal(20.00)))

    private fun getRoutesFromORL() = listOf(Route(100, "ORL", "CDG", BigDecimal(5.00)))
}
