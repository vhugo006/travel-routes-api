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
    fun `given a valid route parameter when adding a new route then return a non nullable route`() {

        val route = Route(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(routeRepository.save(route))
            .thenReturn(Route(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        assertNotNull(routeService.addRoute(route))
    }

    @Test
    fun `given lower case 'from' parameter when adding a route then return a route`() {

        val formattedRoute = Route(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(routeRepository.save(formattedRoute))
            .thenReturn(Route(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val routeParameter = Route(from = "bel", to = "GRU", cost = BigDecimal(750.00))
        assertNotNull(routeService.addRoute(routeParameter))
    }

    @Test
    fun `given lower case 'to' parameter when adding a route then return a route`() {

        val formattedRoute = Route(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(routeRepository.save(formattedRoute))
            .thenReturn(Route(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val routeParameter = Route(from = "BEL", to = "gru", cost = BigDecimal(750.00))
        assertNotNull(routeService.addRoute(routeParameter))
    }

    @Test
    fun `given lower case 'from-to' parameters when adding a route then return a route`() {

        val formattedRoute = Route(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(routeRepository.save(formattedRoute))
            .thenReturn(Route(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val routeParameter = Route(from = "bel", to = "gru", cost = BigDecimal(750.00))
        assertNotNull(routeService.addRoute(routeParameter))
    }

    @Test
    fun `given mixed lower-upper case 'from-to' parameters when adding a route then return a route`() {

        val formattedRoute = Route(from = "BEL", to = "GRU", cost = BigDecimal(750.00))
        `when`(routeRepository.save(formattedRoute))
            .thenReturn(Route(id = 112, from = "BEL", to = "GRU", cost = BigDecimal(750.00)))

        val routeParameter = Route(from = "bEl", to = "gRU", cost = BigDecimal(750.00))
        assertNotNull(routeService.addRoute(routeParameter))
    }

    @Test
    fun `given a valid from parameter when finding routes then a non empty collection`() {

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
    fun `given a valid from parameter when finding routes then return the same parameter in collection items`() {

        val routesWithOriginInBEL = mutableListOf(
            Route(1105, "BEL", "POA", BigDecimal(168.98)),
            Route(1002, "BEL", "BSB", BigDecimal(100.00))
        )

        `when`(routeRepository.findByFrom("BEL")).thenReturn(routesWithOriginInBEL)
        val routes = routeService.findRoutesFrom("BEL")
        routes.forEach { route -> assertEquals("BEL", route.from) }
    }

    @Test
    fun `given an existent route when adding a new route then throws AlreadyExistingRouteException`() {

        val route = Route(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))

        `when`(routeRepository.findByFromAndTo("BEL", "POA")).thenReturn(mutableListOf(route))

        assertThrows<AlreadyExistingRouteException> {
            routeService.addRoute(Route(from = "BEL", to = "POA", cost = BigDecimal(10.00)))
        }
    }

    @Test
    fun `given lowercase 'to' parameter when validating end route then return true`() {

        val route = Route(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))
        val to = "poa"

        assertTrue { routeService.isEndRoute(route, to) }
    }

    @Test
    fun `given uppercase 'to' parameter when validating end route then return true`() {

        val route = Route(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))
        val to = "POA"

        assertTrue { routeService.isEndRoute(route, to) }
    }

    @Test
    fun `given mixed uppercase and lowercase 'to' parameter when validating end route then return true`() {

        val route = Route(1005, from = "BEL", to = "POA", cost = BigDecimal(168.98))
        val to = "PoA"

        assertTrue { routeService.isEndRoute(route, to) }
    }

    @Test
    fun `given an existent id when finding a route then return a route`() {

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
    fun `given a non existent id when finding a route then throw NoResourceFoundException`() {

        `when`(routeRepository.findById(1005)).thenReturn(Optional.empty())

        assertThrows<NoResourceFoundException> { routeService.findRoute(1005) }
    }
}