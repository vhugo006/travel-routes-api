package br.com.bexs.api

import br.com.bexs.domain.Connection
import br.com.bexs.service.ConnectionService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping(value = ["/travel-routes/v1/connections"])
@Api(tags = ["travel-routes"])
class ConnectionController(
    private val connectionService: ConnectionService
) {

    @PostMapping(
        value = [""],
        produces = ["application/json", "application/xml"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(
        value = "Create a connection resource.",
        notes = "Returns the URL of the new resource in the Location header."
    )
    fun addConnection(
        @RequestBody connection: Connection,
        request: HttpServletRequest, response: HttpServletResponse
    ) {
        val createdConnection = connectionService.addConnection(connection)
        response.setHeader("Location", request.requestURL.append("/").append(createdConnection.id).toString())
    }
}