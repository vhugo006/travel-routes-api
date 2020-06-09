package br.com.bexs.util

import com.google.common.net.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.util.*
import javax.servlet.http.HttpServletResponse

@Component
class ResponseUtil {

    fun addLinkHeaderOnPagedResourceRetrieval(
        uriBuilder: UriComponentsBuilder,
        response: HttpServletResponse,
        clazz: Class<*>,
        page: Int,
        totalPages: Int,
        pageSize: Int
    ): HttpServletResponse {

        val linkHeader = StringJoiner(", ")

        if (hasNextPage(page, totalPages)) {
            val uriForNextPage: String = constructNextPageUri(uriBuilder, page, pageSize)
            linkHeader.add(LinkUtil.createLinkHeader(uriForNextPage, LinkUtil.REL_NEXT))
        }

        if (hasPreviousPage(page)) {
            val uriForPrevPage: String = constructPrevPageUri(uriBuilder, page, pageSize)
            linkHeader.add(LinkUtil.createLinkHeader(uriForPrevPage, LinkUtil.REL_PREV))
        }

        if (hasFirstPage(page)) {
            val uriForFirstPage: String = constructFirstPageUri(uriBuilder, pageSize)
            linkHeader.add(LinkUtil.createLinkHeader(uriForFirstPage, LinkUtil.REL_FIRST))
        }

        if (hasLastPage(page, totalPages)) {
            val uriForLastPage: String = constructLastPageUri(uriBuilder, totalPages, pageSize)
            linkHeader.add(LinkUtil.createLinkHeader(uriForLastPage, LinkUtil.REL_LAST))
        }

        if (linkHeader.length() > 0) {
            response.addHeader(HttpHeaders.LINK, linkHeader.toString())
        }

        return response
    }

    fun constructPrevPageUri(
        uriBuilder: UriComponentsBuilder,
        page: Int,
        size: Int
    ): String {
        return uriBuilder.replaceQueryParam(LinkUtil.PAGE, page - 1)
            .replaceQueryParam("size", size)
            .build()
            .encode()
            .toUriString()
    }

    fun constructNextPageUri(
        uriBuilder: UriComponentsBuilder,
        page: Int,
        size: Int
    ): String {
        return uriBuilder.replaceQueryParam(LinkUtil.PAGE, page + 1)
            .replaceQueryParam("size", size)
            .build()
            .encode()
            .toUriString()
    }

    fun constructFirstPageUri(uriBuilder: UriComponentsBuilder, size: Int): String {
        return uriBuilder.replaceQueryParam(LinkUtil.PAGE, 0)
            .replaceQueryParam("size", size)
            .build()
            .encode()
            .toUriString()
    }

    fun constructLastPageUri(
        uriBuilder: UriComponentsBuilder,
        totalPages: Int,
        size: Int
    ): String {
        return uriBuilder.replaceQueryParam(LinkUtil.PAGE, totalPages - 1)
            .replaceQueryParam("size", size)
            .build()
            .encode()
            .toUriString()
    }

    fun hasNextPage(page: Int, totalPages: Int): Boolean {
        return page < totalPages - 1
    }

    fun hasPreviousPage(page: Int): Boolean {
        return page > 0
    }

    fun hasFirstPage(page: Int): Boolean {
        return hasPreviousPage(page)
    }

    fun hasLastPage(page: Int, totalPages: Int): Boolean {
        return totalPages > 1 && hasNextPage(page, totalPages)
    }
}
