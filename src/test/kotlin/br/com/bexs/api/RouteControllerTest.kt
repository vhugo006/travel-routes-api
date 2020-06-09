package br.com.bexs.api

import br.com.bexs.domain.Route
import br.com.bexs.service.RouteService
import br.com.bexs.service.TravelRouteService
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@WebMvcTest(RouteController::class)
class RouteControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    lateinit var routeService: RouteService

    @MockBean
    lateinit var travelRouteService: TravelRouteService

    @Test
    fun `given existing route when route is requested the resource is retrieved`() {

        given(this.routeService.findRoute(1001))
            .willReturn(Route(1001, "GRU", "POA", BigDecimal("100")))

        mvc.perform(get("/travel-routes/v1/routes/1001"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$._links").doesNotExist())
            .andExpect(jsonPath("$.id", `is`(1001)))
    }


}