package br.com.bexs.api

import br.com.bexs.domain.Route
import br.com.bexs.service.RouteService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping(value = ["/travel-routes/v1/routes"])
@Api(tags = ["travel-routes"])
class RouteController(
    private val routeService: RouteService
) {


    @PostMapping(
        value = [""],
        produces = ["application/json", "application/xml"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(
        value = "Create a route resource.",
        notes = "Returns the URL of the new resource in the Location header."
    )
    fun addRoute(
        @RequestBody route: Route,
        request: HttpServletRequest, response: HttpServletResponse
    ) {
        val createdRoute = routeService.addRoute(route)
        response.setHeader("Location", request.requestURL.append("/").append(createdRoute.id).toString())
    }
}