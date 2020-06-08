package br.com.bexs.service

import br.com.bexs.domain.Route
import br.com.bexs.exception.AlreadyExistingRouteException
import br.com.bexs.exception.NoResourceFoundException
import br.com.bexs.repository.RouteRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
internal class RouteServiceTest {

    @Mock
    private lateinit var routeRepository: RouteRepository

    @InjectMocks
    private lateinit var routeService: RouteService

    @Test
    fun `when adding a route should return a non nullable route`() {

        val route = Route(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(routeRepository.save(route))
            .thenReturn(Route(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        assertNotNull(routeService.addRoute(route))
    }

    @Test
    fun `when adding a route with lower case 'from' parameter should return a route`() {

        val formattedRoute = Route(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(routeRepository.save(formattedRoute))
            .thenReturn(Route(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val routeParameter = Route(from = "bel", to = "GRU", cost = BigDecimal(750.00))
        assertNotNull(routeService.addRoute(routeParameter))
    }

    @Test
    fun `when adding a route with lower case 'to' parameter should return a route`() {

        val formattedRoute = Route(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(routeRepository.save(formattedRoute))
            .thenReturn(Route(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val routeParameter = Route(from = "BEL", to = "gru", cost = BigDecimal(750.00))
        assertNotNull(routeService.addRoute(routeParameter))
    }

    @Test
    fun `when adding a route with lower case 'from-to' parameters should return a route`() {

        val formattedRoute = Route(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(routeRepository.save(formattedRoute))
            .thenReturn(Route(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val routeParameter = Route(from = "bel", to = "gru", cost = BigDecimal(750.00))
        assertNotNull(routeService.addRoute(routeParameter))
    }

    @Test
    fun `when adding a route mixed lower-upper case 'from-to' parameters should return a route`() {

        val formattedRoute = Route(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(routeRepository.save(formattedRoute))
            .thenReturn(Route(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val routeParameter = Route(from = "bEl", to = "gRU", cost = BigDecimal(750.00))
        assertNotNull(routeService.addRoute(routeParameter))
    }

    @Test
    fun `when there is routes for the given origin parameter should return a non empty collection`() {

        val routesWithOriginInBEL = mutableListOf(
            Route(1105, "BEL", "POA", BigDecimal(168.98)),
            Route(1002, "BEL", "BSB", BigDecimal(100.00)),
            Route(1105, "BSB", "POA", BigDecimal(168.98))
        )

        Mockito.`when`(routeRepository.findByFrom("BEL")).thenReturn(routesWithOriginInBEL)
        val routes = routeService.findRoutesFrom("BEL")
        assertTrue { routes.isNotEmpty() }
    }

    @Test
    fun `when finding routes by origin should return the same origin in collection items`() {

        val routesWithOriginInBEL = mutableListOf(
            Route(1105, "BEL", "POA", BigDecimal(168.98)),
            Route(1002, "BEL", "BSB", BigDecimal(100.00))
        )

        `when`(routeRepository.findByFrom("BEL")).thenReturn(routesWithOriginInBEL)
        val routes = routeService.findRoutesFrom("BEL")
        routes.forEach { route -> assertEquals("BEL", route.from) }
    }

    @Test
    fun `when adding a route and it already exists should throws AlreadyExistingRouteException`() {

        val route = Route(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))

        `when`(routeRepository.findByFromAndTo("BEL", "POA")).thenReturn(mutableListOf(route))

        assertThrows<AlreadyExistingRouteException> {
            routeService.addRoute(Route(from = "BEL", to = "POA", cost = BigDecimal(10.00)))
        }
    }

    @Test
    fun `when 'to' parameter is lowercase should return true`() {

        val route = Route(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))
        val to = "poa"

        assertTrue { routeService.isEndRoute(route, to) }
    }

    @Test
    fun `when 'to' parameter is uppercase should return true`() {

        val route = Route(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))
        val to = "POA"

        assertTrue { routeService.isEndRoute(route, to) }
    }

    @Test
    fun `when 'to' parameter is uppercase and lowercase mixed should return true`() {

        val route = Route(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))
        val to = "PoA"

        assertTrue { routeService.isEndRoute(route, to) }
    }

    @Test
    fun `when finding a route by an existing id should return the founded route`() {

        val route = Route(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))

        `when`(routeRepository.findById(1005)).thenReturn(Optional.of(route))

        val returnedRoute = routeService.findRoute(1005)

        assertAll(
            { assertEquals(1005, returnedRoute.id) },
            { assertEquals("BEL", returnedRoute.from) },
            { assertEquals("POA", returnedRoute.to) },
            {
                assertEquals(
                    BigDecimal(168.98).round(MathContext(5, RoundingMode.HALF_UP)),
                    returnedRoute.cost.round(MathContext(5, RoundingMode.HALF_UP))
                )
            }
        )
    }

    @Test
    fun `when finding a route by a non existent id should throw NoResourceFoundException`() {

        `when`(routeRepository.findById(1005)).thenReturn(Optional.empty())

        assertThrows<NoResourceFoundException> { routeService.findRoute(1005) }
    }
}