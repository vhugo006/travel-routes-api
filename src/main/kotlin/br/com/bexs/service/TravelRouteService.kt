package br.com.bexs.service

import br.com.bexs.domain.Route
import br.com.bexs.domain.TravelRoute
import br.com.bexs.exception.NoResourceFoundException
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TravelRouteService(private val routeService: RouteService) {

    fun findBestTravelRoute(from: String, to: String): TravelRoute? {

        val availableTravelRoutes = findAvailableTravelRoutes(from, to)
        validateAvailableTravelRoutes(availableTravelRoutes)
        return getTheCheapestTravelRoute(availableTravelRoutes)
    }

    fun findAvailableTravelRoutes(
        from: String,
        to: String,
        travelRouteRoutes: MutableList<Route> = mutableListOf(),
        travelRoutes: MutableList<TravelRoute> = mutableListOf()
    ): List<TravelRoute> {

        val availableRoutesByOrigin = routeService.findRoutesFrom(from)

        if (availableRoutesByOrigin.isEmpty()) {
            return travelRoutes
        }

        availableRoutesByOrigin.forEach routes@{ route ->

            if (travelRouteRoutes.isNotEmpty() && travelRouteRoutes.last().to != route.from) {
                travelRouteRoutes.clear()
            }

            travelRouteRoutes.add(route)
            if (routeService.isEndRoute(route, to)) {
                val routesToAdd = mutableListOf<Route>()
                routesToAdd.addAll(travelRouteRoutes)
                travelRoutes.add(TravelRoute(routesToAdd))
            }

            if (route.to == to) return@routes
            findAvailableTravelRoutes(route.to, to, travelRouteRoutes, travelRoutes)
        }
        return travelRoutes
    }

    private fun getTheCheapestTravelRoute(travelRoutes: List<TravelRoute>): TravelRoute {

        val travelRoutesWithTotalCostsCalculated = calculateTravelRoutesTotalCost(travelRoutes)

        return travelRoutesWithTotalCostsCalculated
            .stream()
            .min(Comparator.comparing(TravelRoute::totalCost))
            .orElseThrow { NoResourceFoundException("There is no travel route for the given parameters") }
    }

    private fun calculateTravelRoutesTotalCost(travelRoutes: List<TravelRoute>): List<TravelRoute> {

        travelRoutes.forEach { travelRoute ->
            travelRoute.totalCost =
                travelRoute.routes
                    .map { it.cost }
                    .fold(BigDecimal.ZERO, BigDecimal::add)
        }

        return travelRoutes
    }

    private fun validateAvailableTravelRoutes(availableTravelRoutes: List<TravelRoute>) {
        if (availableTravelRoutes.isEmpty()) throw NoResourceFoundException("There is no travel route for the given parameters")
    }
}