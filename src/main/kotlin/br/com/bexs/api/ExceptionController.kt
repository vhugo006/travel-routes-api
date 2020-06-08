package br.com.bexs.api

import br.com.bexs.exception.AlreadyExistingRouteException
import br.com.bexs.exception.NoTravelRouteFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime
import java.util.*


@ControllerAdvice
class ExceptionController : ResponseEntityExceptionHandler() {
    @ExceptionHandler(NoTravelRouteFoundException::class)
    fun handleNoTravelRouteFoundException(ex: NoTravelRouteFoundException?): ResponseEntity<Any>? {

        val body: MutableMap<String, Any> = LinkedHashMap()

        body["timestamp"] = LocalDateTime.now()
        body["message"] = ex?.message!!

        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AlreadyExistingRouteException::class)
    fun handleAlreadyExistingRouteException(ex: AlreadyExistingRouteException?): ResponseEntity<Any>? {

        val body: MutableMap<String, Any> = LinkedHashMap()

        body["timestamp"] = LocalDateTime.now()
        body["message"] = ex?.message!!

        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }
}