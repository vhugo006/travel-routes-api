package br.com.bexs.service

import br.com.bexs.domain.Connection
import br.com.bexs.domain.TravelRoute
import br.com.bexs.exception.NoTravelRouteFoundException
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TravelService(private val connectionService: ConnectionService) {

    fun findBestTravelRoute(from: String, to: String): TravelRoute? {

        val availableTravelRoutes = findAvailableTravelRoutes(from, to)
        validateAvailableTravelRoutes(availableTravelRoutes)
        return getTheCheapestTravelRoute(availableTravelRoutes)
    }

    fun findAvailableTravelRoutes(
        from: String,
        to: String,
        travelRouteConnections: MutableList<Connection> = mutableListOf(),
        travelRoutes: MutableList<TravelRoute> = mutableListOf()
    ): List<TravelRoute> {

        val availableConnectionsByOrigin = connectionService.findConnectionsFrom(from)

        if (availableConnectionsByOrigin.isEmpty()) {
            return travelRoutes
        }

        availableConnectionsByOrigin.forEach connections@{ connection ->

            if (travelRouteConnections.isNotEmpty() && travelRouteConnections.last().to != connection.from) {
                travelRouteConnections.clear()
            }

            travelRouteConnections.add(connection)
            if (connectionService.isEndConnection(connection, to)) {
                val connectionsToAdd = mutableListOf<Connection>()
                connectionsToAdd.addAll(travelRouteConnections)
                travelRoutes.add(TravelRoute(connectionsToAdd))
            }

            if (connection.to == to) return@connections
            findAvailableTravelRoutes(connection.to, to, travelRouteConnections, travelRoutes)
        }
        return travelRoutes
    }

    private fun getTheCheapestTravelRoute(travelRoutes: List<TravelRoute>): TravelRoute {

        val travelRoutesWithTotalCostsCalculated = calculateTravelRoutesTotalCost(travelRoutes)

        return travelRoutesWithTotalCostsCalculated
            .stream()
            .min(Comparator.comparing(TravelRoute::totalCost))
            .orElseThrow { NoTravelRouteFoundException("There is no travel route for the given parameters") }
    }

    private fun calculateTravelRoutesTotalCost(travelRoutes: List<TravelRoute>): List<TravelRoute> {

        travelRoutes.forEach { travelRoute ->
            travelRoute.totalCost =
                travelRoute.connections
                    .map { it.cost }
                    .fold(BigDecimal.ZERO, BigDecimal::add)
        }

        return travelRoutes
    }

    private fun validateAvailableTravelRoutes(availableTravelRoutes: List<TravelRoute>) {
        if (availableTravelRoutes.isEmpty()) throw NoTravelRouteFoundException("There is no travel route for the given parameters")
    }
}