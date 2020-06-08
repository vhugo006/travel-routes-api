package br.com.bexs.api

import br.com.bexs.domain.TravelRoute
import br.com.bexs.service.TravelService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(value = ["/travel-routes/v1/routes"])
@Api(tags = ["travel-routes"])
class TravelRouteController(private val travelService: TravelService) {

    @GetMapping(
        value = ["/from/{from}/to/{to}"],
        produces = ["application/json", "application/xml"]
    )
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiOperation(
        value = "Return a travel route based on path param from/to.",
        notes = "Return the best travel route in terms of cost regardless of connections number."
    )
    fun getCheapestTravelRoute(
        @PathVariable("from") from: String,
        @PathVariable("to") to: String
    ): TravelRoute? {
        return travelService.findBestTravelRoute(from, to)
    }
}