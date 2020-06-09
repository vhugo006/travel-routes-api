package br.com.bexs.api

import br.com.bexs.exception.AlreadyExistingRouteException
import br.com.bexs.exception.NoResourceFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime
import java.util.*


@ControllerAdvice
class ExceptionController : ResponseEntityExceptionHandler() {
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoTravelRouteFoundException(ex: NoResourceFoundException?): ResponseEntity<Any>? {

        val body = LinkedHashMap<String, Any>()

        body["timestamp"] = LocalDateTime.now()
        body["message"] = ex?.message!!

        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AlreadyExistingRouteException::class)
    fun handleAlreadyExistingRouteException(ex: AlreadyExistingRouteException?): ResponseEntity<Any>? {

        val body = LinkedHashMap<String, Any>()

        body["timestamp"] = LocalDateTime.now()
        body["message"] = ex?.message!!

        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }
}