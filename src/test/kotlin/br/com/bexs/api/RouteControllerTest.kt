package br.com.bexs.api

import br.com.bexs.domain.Route
import br.com.bexs.exception.AlreadyExistingRouteException
import br.com.bexs.exception.NoResourceFoundException
import br.com.bexs.service.RouteService
import br.com.bexs.service.TravelRouteService
import br.com.bexs.util.ResponseUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal

@WebMvcTest(RouteController::class)
class RouteControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    lateinit var routeService: RouteService

    @MockBean
    lateinit var responseUtil: ResponseUtil

    @MockBean
    lateinit var travelRouteService: TravelRouteService

    @Test
    fun `given existing route when route is requested then resource is retrieved`() {

        given(this.routeService.findRoute(1001))
            .willReturn(Route(1001, "GRU", "POA", BigDecimal("100")))

        mvc.perform(get("/travel-routes/v1/routes/1001"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.from", `is`("GRU")))
            .andExpect(jsonPath("$.to", `is`("POA")))
            .andExpect(jsonPath("$.cost", `is`(100)))
    }

    @Test
    fun `given a non existing route when route is requested then return 404 http status code`() {

        given(this.routeService.findRoute(1001))
            .willThrow(NoResourceFoundException("There is no route for the id 1001."))

        mvc.perform(get("/travel-routes/v1/routes/1001"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message", `is`("There is no route for the id 1001.")))
    }

    @Test
    fun `given a valid route when creating a route then return 201 http status code`() {

        val requestRoute = Route(from = "GRU", to = "POA", cost = BigDecimal("100"))

        given(this.routeService.addRoute(requestRoute))
            .willReturn(Route(1002, "GRU", "POA", BigDecimal("100")))

        mvc.perform(
            post("/travel-routes/v1/routes")
                .content(getObjectAsJson(requestRoute))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated)
    }

    @Test
    fun `given a existing route when creating a route then return 400 http status code`() {

        val requestRoute = Route(from = "GRU", to = "POA", cost = BigDecimal("100"))

        given(
            this.routeService
                .addRoute(requestRoute)
        ).willThrow(
            AlreadyExistingRouteException("There is already a route departing from ${requestRoute.from} and arriving in ${requestRoute.to}")
        )

        mvc
            .perform(
                post("/travel-routes/v1/routes")
                    .content(getObjectAsJson(requestRoute))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message", `is`("There is already a route departing from GRU and arriving in POA")))
    }

    @Test
    fun `given a valid route when creating a route then return 'Location' header`() {

        val requestRoute = Route(from = "GRU", to = "POA", cost = BigDecimal("100"))

        given(this.routeService.addRoute(requestRoute))
            .willReturn(Route(1002, "GRU", "POA", BigDecimal("100")))

        mvc.perform(
            post("/travel-routes/v1/routes")
                .content(getObjectAsJson(requestRoute))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated)
            .andExpect(redirectedUrlPattern("http://*/travel-routes/v1/routes/1002"))

    }

    private fun getObjectAsJson(route: Route): String {
        val mapper = ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        val ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(route)
    }
}