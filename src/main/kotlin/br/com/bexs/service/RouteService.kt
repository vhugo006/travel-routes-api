package br.com.bexs.service

import br.com.bexs.domain.Route
import br.com.bexs.exception.AlreadyExistingRouteException
import br.com.bexs.exception.NoResourceFoundException
import br.com.bexs.repository.RouteRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class RouteService(private val routeRepository: RouteRepository) {

    fun addRoute(route: Route): Route {
        validateRoute(route.from, route.to)
        val formattedRoute = formatRouteParameters(route)
        return routeRepository.save(formattedRoute)
    }

    fun findRoute(id: Long): Route {
        return routeRepository.findById(id)
            .orElseThrow { NoResourceFoundException("There is no route for the id $id.") }
    }

    fun findRoutesFrom(from: String): List<Route> {
        return routeRepository.findByFrom(from.toUpperCase())
    }

    fun isEndRoute(route: Route, to: String): Boolean {
        return route.to == to.toUpperCase()
    }

    private fun validateRoute(from: String, to: String) {
        if (findRoutesFromTo(from, to).toList().isNotEmpty()) {
            throw AlreadyExistingRouteException("There is already a route departing from $from and arriving in $to")
        }
    }

    fun findRoutesFromTo(from: String, to: String): List<Route> {
        return routeRepository.findByFromAndTo(from.toUpperCase(), to.toUpperCase())
    }

    private fun formatRouteParameters(route: Route): Route {
        return Route(
            from = route.from.toUpperCase(),
            to = route.to.toUpperCase(),
            cost = route.cost
        )
    }

    fun getAllRoutes(page: Int, size: Int): Page<Route> {
        return routeRepository.findAll(PageRequest.of(page, size))
    }
}