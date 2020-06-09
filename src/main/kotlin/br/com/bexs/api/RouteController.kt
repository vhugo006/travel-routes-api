package br.com.bexs.api

import br.com.bexs.domain.Route
import br.com.bexs.domain.TravelRoute
import br.com.bexs.domain.dto.CostUpdateDTO
import br.com.bexs.service.RouteService
import br.com.bexs.service.TravelRouteService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping(value = ["/travel-routes/v1/routes"])
@Api(tags = ["travel-routes"])
class RouteController(
    private val routeService: RouteService,
    private val travelRouteRouteService: TravelRouteService
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val defaultPageSize = "100"
    private val defaultPageNum = "0"

    @PostMapping(value = [""], produces = ["application/json"])
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

    @GetMapping(value = ["/{id}"], produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Get a single route.",
        notes = "You have to provide a valid route ID."
    )
    fun findRoute(
        @ApiParam(value = "The ID of the route.", required = true)
        @PathVariable id: Long
    ): Route {
        return routeService.findRoute(id)
    }

    @GetMapping(value = ["/from/{from}/to/{to}"], produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiOperation(
        value = "Return a travel route based on path param from/to.",
        notes = "Return the best travel route in terms of cost regardless of routes number."
    )
    fun getCheapestTravelRoute(
        @PathVariable("from") from: String,
        @PathVariable("to") to: String
    ): TravelRoute? {
        return travelRouteRouteService.findBestTravelRoute(from, to)
    }

    @GetMapping(value = [""], params = ["page", "size"], produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Get a paginated list of all routes.",
        notes = "The list is paginated. You can provide a page number (default 0) and a page size (default 100)"
    )
    @ResponseBody
    fun getAllRoutes(
        @ApiParam(value = "The page number (zero-based)", required = true)
        @RequestParam(value = "page", required = true, defaultValue = "0") page: Int,
        @ApiParam(value = "Tha page size", required = true)
        @RequestParam(value = "size", required = true, defaultValue = "100") size: Int,
        response: HttpServletResponse
    ): List<Route> {
        return this.routeService.getAllRoutes(page, size)
    }

    @PutMapping(value = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Update the cost of the given route ID.",
        notes = "You have to provide a valid route ID."
    )
    fun update(
        @ApiParam(value = "The ID of the route.", required = true)
        @PathVariable("id") id: Long,
        @RequestBody costUpdateDTO: CostUpdateDTO
    ) {
        routeService.updateCost(id, costUpdateDTO.cost)
    }

    @DeleteMapping(value = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Delete the route resource for the given route ID.",
        notes = "You have to provide a valid route ID."
    )
    fun delete(
        @ApiParam(value = "The ID of the route.", required = true)
        @PathVariable("id") id: Long
    ) {
        routeService.deleteById(id)
    }
}